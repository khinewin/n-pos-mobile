package com.npos.app.data.offline

import android.content.Context
import android.util.Log
import com.npos.app.data.FirebaseService
import com.npos.app.data.model.Customer
import com.npos.app.data.model.Product
import com.npos.app.data.model.SaleRecord
import com.npos.app.data.model.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * High-performance Offline Synchronization Coordinator.
 * Seamlessly manages offline queue, automatic cloud sync, and local caching.
 */
class OfflineSyncManager(
    context: Context,
    val dbHelper: PosLocalDatabaseHelper,
    val networkMonitor: NetworkMonitor
) {
    companion object {
        private const val TAG = "OfflineSyncManager"
    }

    private val _pendingSalesCount = MutableStateFlow(dbHelper.getPendingOfflineSalesCount())
    val pendingSalesCount: StateFlow<Int> = _pendingSalesCount.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    fun refreshPendingCount() {
        _pendingSalesCount.value = dbHelper.getPendingOfflineSalesCount()
    }

    /**
     * Cache freshly loaded Cloud Firestore data into local SQLite database.
     */
    suspend fun cacheProducts(products: List<Product>) = withContext(Dispatchers.IO) {
        dbHelper.cacheProducts(products)
    }

    suspend fun cacheCategories(categories: List<String>) = withContext(Dispatchers.IO) {
        dbHelper.cacheCategories(categories)
    }

    suspend fun cacheCustomers(customers: List<Customer>) = withContext(Dispatchers.IO) {
        dbHelper.cacheCustomers(customers)
    }

    /**
     * Retrieve offline cached products, categories, or customers.
     */
    suspend fun getCachedProducts(): List<Product> = withContext(Dispatchers.IO) {
        dbHelper.getAllCachedProducts()
    }

    suspend fun getCachedCategories(): List<String> = withContext(Dispatchers.IO) {
        dbHelper.getAllCachedCategories()
    }

    suspend fun getCachedCustomers(): List<Customer> = withContext(Dispatchers.IO) {
        dbHelper.getAllCachedCustomers()
    }

    suspend fun clearAllCachedData() = withContext(Dispatchers.IO) {
        dbHelper.clearAllCachedData()
    }

    /**
     * Intelligent sale save:
     * - Primary requirement: Always update to Firebase Firestore Cloud when internet is available.
     * - Checks and refreshes token if expired or close to expiry.
     * - Retries Cloud save cleanly with detailed error logging.
     * - Fallback: Use Firebase offline mode ONLY when internet is truly unavailable or completely unreachable.
     */
    suspend fun processSale(
        firebaseService: FirebaseService,
        idToken: String,
        sale: SaleRecord,
        forceOffline: Boolean = false,
        onSessionRefreshed: ((UserSession) -> Unit)? = null
    ): Pair<Result<String>, Boolean> = withContext(Dispatchers.IO) {
        var activeToken = idToken

        // If not forced offline and Firebase is configured, ALWAYS attempt and prioritize Firebase Cloud update!
        if (!forceOffline && firebaseService.isConfigured) {
            val session = firebaseService.authState.value
            // If token is blank but session exists, try to get fresh token first
            if (activeToken.isBlank() && session != null) {
                val relogRes = firebaseService.reloginCurrentUser(session)
                if (relogRes.isSuccess) {
                    val refreshedSession = relogRes.getOrThrow()
                    activeToken = refreshedSession.idToken
                    onSessionRefreshed?.invoke(refreshedSession)
                }
            }

            var lastError: Exception? = null

            // First attempt to save directly to Firebase Firestore Cloud
            if (activeToken.isNotBlank()) {
                val cloudResult = firebaseService.saveSaleRecord(activeToken, sale)
                if (cloudResult.isSuccess) {
                    dbHelper.saveOfflineSale(sale, PosLocalDatabaseHelper.STATUS_SYNCED)
                    refreshPendingCount()
                    NetworkMonitor.notifySuccess()
                    return@withContext Pair(cloudResult, false) // Online save to Firebase succeeded!
                }

                val err = cloudResult.exceptionOrNull()
                val errMsg = err?.message.orEmpty()
                lastError = err as? Exception ?: Exception(errMsg)
                Log.w(TAG, "First Cloud save attempt failed: $errMsg")

                // If error is 401 / UNAUTHENTICATED / Token expired, automatically refresh token & retry immediately
                if (errMsg.contains("401") || errMsg.contains("UNAUTHENTICATED") || errMsg.contains("expired", ignoreCase = true)) {
                    val currentSession = firebaseService.authState.value
                    if (currentSession != null) {
                        Log.i(TAG, "Token expired during sale. Silently refreshing Firebase session...")
                        val relogRes = firebaseService.reloginCurrentUser(currentSession)
                        if (relogRes.isSuccess) {
                            val refreshedSession = relogRes.getOrThrow()
                            activeToken = refreshedSession.idToken
                            onSessionRefreshed?.invoke(refreshedSession)

                            val retryResult = firebaseService.saveSaleRecord(activeToken, sale)
                            if (retryResult.isSuccess) {
                                dbHelper.saveOfflineSale(sale, PosLocalDatabaseHelper.STATUS_SYNCED)
                                refreshPendingCount()
                                NetworkMonitor.notifySuccess()
                                return@withContext Pair(retryResult, false) // Online save succeeded after token refresh!
                            } else {
                                lastError = retryResult.exceptionOrNull() as? Exception ?: lastError
                                Log.e(TAG, "Retry with refreshed token also failed: ${lastError?.message}")
                            }
                        }
                    }
                }
            }

            // If we have internet connectivity (or device has network), try 1 more immediate attempt
            val hasNetwork = networkMonitor.isOnline.value || networkMonitor.checkActualConnectivityFast()
            if (hasNetwork && activeToken.isNotBlank()) {
                try {
                    val retryResult = firebaseService.saveSaleRecord(activeToken, sale)
                    if (retryResult.isSuccess) {
                        dbHelper.saveOfflineSale(sale, PosLocalDatabaseHelper.STATUS_SYNCED)
                        refreshPendingCount()
                        NetworkMonitor.notifySuccess()
                        return@withContext Pair(retryResult, false)
                    } else {
                        lastError = retryResult.exceptionOrNull() as? Exception ?: lastError
                    }
                } catch (e: Exception) {
                    lastError = e
                    Log.w(TAG, "Secondary online attempt threw exception: ${e.message}")
                }
            }
        }

        // Only when internet is truly disconnected or Cloud is unreachable, use Firebase Offline Mode
        Log.i(TAG, "No internet or Cloud unreachable -> Saving sale in Firebase Offline Mode (${sale.voucherNo})")
        val saved = dbHelper.saveOfflineSale(sale, PosLocalDatabaseHelper.STATUS_PENDING)
        refreshPendingCount()

        if (saved) {
            Pair(Result.success(sale.voucherNo), true) // true = saved in offline mode
        } else {
            Pair(Result.failure(Exception("Failed to save sale to offline storage")), true)
        }
    }

    /**
     * Upload all pending offline sales to Firebase Firestore.
     */
    suspend fun syncPendingSales(
        firebaseService: FirebaseService,
        idToken: String
    ): Result<Int> = withContext(Dispatchers.IO) {
        if (!networkMonitor.isOnline.value && !networkMonitor.checkActualConnectivityFast()) {
            return@withContext Result.failure(Exception("အင်တာနက် မရှိပါ (Offline ဖြစ်နေပါသည်)"))
        }
        if (idToken.isBlank()) {
            return@withContext Result.failure(Exception("User session token is missing"))
        }

        if (_isSyncing.value) {
            return@withContext Result.success(0)
        }

        _isSyncing.value = true
        var syncedCount = 0

        try {
            val pendingSales = dbHelper.getPendingOfflineSales()
            Log.i(TAG, "Starting sync for ${pendingSales.size} pending offline sales...")

            for (sale in pendingSales) {
                val result = firebaseService.saveSaleRecord(idToken, sale)
                if (result.isSuccess) {
                    dbHelper.markSaleSynced(sale.voucherNo)
                    syncedCount++
                    Log.d(TAG, "Synced offline sale: ${sale.voucherNo}")
                } else {
                    Log.e(TAG, "Failed to sync voucher ${sale.voucherNo}: ${result.exceptionOrNull()?.message}")
                }
            }

            refreshPendingCount()
            Result.success(syncedCount)
        } catch (e: Exception) {
            Log.e(TAG, "Error during offline sync", e)
            Result.failure(e)
        } finally {
            _isSyncing.value = false
        }
    }
}
