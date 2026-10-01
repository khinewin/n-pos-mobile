package com.npos.app.pos

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast

/**
 * JavaScript interface exposed to the WebView as "AndroidBridge" and "AndroidPOS".
 * Enables seamless communication between the Vue.js POS web app and native Android POS hardware.
 */
class PosBridge(
    private val context: Context,
    private val printerManager: ThermalPrinterManager,
    private val customerDisplayManager: CustomerDisplayManager,
    private val onPrintRequested: (String) -> Unit,
    private val onOpenCustomerDisplayRequested: () -> Unit
) {

    companion object {
        private const val TAG = "PosBridge"
        const val BRIDGE_NAME = "AndroidBridge"
        const val ALT_BRIDGE_NAME = "AndroidPOS"
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun printThermal(jsonData: String) {
        Log.d(TAG, "printThermal invoked with data: $jsonData")
        mainHandler.post {
            onPrintRequested(jsonData)
        }
    }

    @JavascriptInterface
    fun openCustomerDisplay() {
        Log.d(TAG, "openCustomerDisplay invoked")
        mainHandler.post {
            customerDisplayManager.showCustomerDisplayManually()
            onOpenCustomerDisplayRequested()
        }
    }

    @JavascriptInterface
    fun updateCustomerData(jsonData: String) {
        Log.d(TAG, "updateCustomerData invoked: $jsonData")
        customerDisplayManager.updateCustomerData(jsonData)
    }

    @JavascriptInterface
    fun isPrinterConnected(): Boolean {
        return printerManager.isPrinterConfigured()
    }

    @JavascriptInterface
    fun getPrinterName(): String {
        return printerManager.getPrinterDisplayName()
    }

    @JavascriptInterface
    fun getLoginEmail(): String {
        return "n1@web.app"
    }

    @JavascriptInterface
    fun getLoginPassword(): String {
        return "password"
    }

    @JavascriptInterface
    fun showToast(message: String) {
        mainHandler.post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
