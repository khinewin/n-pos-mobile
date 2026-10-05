package com.npos.app.data.offline

import android.content.Context
import android.util.Log
import com.npos.app.data.FirebaseService
import com.npos.app.data.model.Customer
import com.npos.app.data.model.Product
import com.npos.app.data.model.SaleRecord
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

    /**
     * Intelligent hybrid sale save:
     * - If Online: saves directly to Firebase Firestore Cloud; caches locally as SYNCED.
     *   (If Firestore call encounters network timeout, immediately falls back to Offline Queue).
     * - If Offline: saves directly to SQLite Offline Queue as PENDING.
     */
    suspend fun processSale(
        firebaseService: FirebaseService,
        idToken: String,
        sale: SaleRecord,
        forceOffline: Boolean = false
    ): Pair<Result<String>, Boolean> = withContext(Dispatchers.IO) {
        val online = !forceOffline && networkMonitor.isOnline.value

        if (online && idToken.isNotBlank()) {
            // Attempt cloud save
            val cloudResult = firebaseService.saveSaleRecord(idToken, sale)
            if (cloudResult.isSuccess) {
                // Record cached copy as SYNCED
                dbHelper.saveOfflineSale(sale, PosLocalDatabaseHelper.STATUS_SYNCED)
                refreshPendingCount()
                return@withContext Pair(cloudResult, false) // false = saved online
            } else {
                Log.w(TAG, "Cloud save failed (${cloudResult.exceptionOrNull()?.message}). Falling back to Offline Queue.")
            }
        }

        // Save in local SQLite Offline Queue
        val saved = dbHelper.saveOfflineSale(sale, PosLocalDatabaseHelper.STATUS_PENDING)
        refreshPendingCount()

        if (saved) {
            Pair(Result.success(sale.voucherNo), true) // true = saved offline
        } else {
            Pair(Result.failure(Exception("Failed to save sale to local offline database")), true)
        }
    }

    /**
     * Upload all pending offline sales to Firebase Firestore.
     */
    suspend fun syncPendingSales(
        firebaseService: FirebaseService,
        idToken: String
    ): Result<Int> = withContext(Dispatchers.IO) {
        if (!networkMonitor.isOnline.value) {
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
