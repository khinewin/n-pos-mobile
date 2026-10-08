package com.npos.app.data.offline

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Ultra-Fast Real-time Network Connectivity Monitor for N-POS.
 * Instantaneous detection of network loss and recovery.
 */
class NetworkMonitor(context: Context) {

    companion object {
        private const val TAG = "NetworkMonitor"

        @Volatile
        var activeInstance: NetworkMonitor? = null

        fun notifyFailure() {
            activeInstance?.setOfflineImmediately()
        }

        fun notifySuccess() {
            activeInstance?.setOnlineImmediately()
        }
    }

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _isOnline = MutableStateFlow(checkActualConnectivityFast())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    fun setOfflineImmediately() {
        if (_isOnline.value) {
            Log.d(TAG, "Instant offline triggered by network failure")
            _isOnline.value = false
        }
    }

    fun setOnlineImmediately() {
        if (!_isOnline.value) {
            Log.d(TAG, "Instant online confirmed by successful network response")
            _isOnline.value = true
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            Log.d(TAG, "Default network became available -> Online")
            _isOnline.value = true
        }

        override fun onLost(network: Network) {
            Log.d(TAG, "Default network lost -> Instant Offline")
            _isOnline.value = checkActualConnectivityFast()
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            Log.d(TAG, "Network losing")
            _isOnline.value = checkActualConnectivityFast()
        }

        override fun onUnavailable() {
            Log.d(TAG, "Network unavailable -> Offline")
            _isOnline.value = false
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            if (_isOnline.value != hasInternet) {
                _isOnline.value = hasInternet
            }
        }
    }

    init {
        activeInstance = this
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback)
            } else {
                val request = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
                connectivityManager.registerNetworkCallback(request, networkCallback)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error registering network callback", e)
        }

        // Periodic check to ensure state stays in sync
        scope.launch {
            while (isActive) {
                delay(3000)
                val actual = checkActualConnectivityFast()
                if (_isOnline.value != actual) {
                    _isOnline.value = actual
                }
            }
        }
    }

    fun checkActualConnectivityFast(): Boolean {
        return try {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (_: Exception) {
            false
        }
    }

    fun release() {
        if (activeInstance == this) {
            activeInstance = null
        }
        scope.cancel()
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (_: Exception) {}
    }
}
