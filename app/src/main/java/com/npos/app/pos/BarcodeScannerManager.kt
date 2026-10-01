package com.npos.app.pos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Manages auto-detection of barcode scans on Android POS devices:
 * 1. Sunmi POS built-in laser scanner broadcasts
 * 2. Hardware USB / Bluetooth barcode gun keyboard wedge (rapid key input ending with ENTER)
 */
class BarcodeScannerManager(private val context: Context) {

    companion object {
        private const val TAG = "BarcodeScannerManager"
        private const val SUNMI_SCAN_ACTION = "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED"
        private const val SUNMI_INTENT_ACTION = "sunmi.intent.action.SCAN_RESULT"
    }

    private val _scannedCodeFlow = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val scannedCodeFlow: SharedFlow<String> = _scannedCodeFlow.asSharedFlow()

    private val barcodeBuffer = StringBuilder()
    private var lastKeyTime = 0L

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            Log.w(TAG, "ToneGenerator init failed", e)
        }
        registerSunmiReceiver()
    }

    private val sunmiReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            intent ?: return
            val action = intent.action
            if (action == SUNMI_SCAN_ACTION || action == SUNMI_INTENT_ACTION) {
                val data = intent.getStringExtra("data")
                    ?: intent.getStringExtra("scan_result")
                    ?: intent.getByteArrayExtra("source")?.let { String(it) }

                if (!data.isNullOrEmpty()) {
                    val cleanCode = data.trim()
                    Log.i(TAG, "Sunmi broadcast barcode scanned: $cleanCode")
                    emitBarcode(cleanCode)
                }
            }
        }
    }

    private fun registerSunmiReceiver() {
        try {
            val filter = IntentFilter().apply {
                addAction(SUNMI_SCAN_ACTION)
                addAction(SUNMI_INTENT_ACTION)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(sunmiReceiver, filter, Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(sunmiReceiver, filter)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Sunmi broadcast receiver registration error", e)
        }
    }

    /**
     * Intercepts hardware barcode scanner keyboard wedge events.
     * Hardware barcode scanners typically emit characters in rapid bursts (< 50ms)
     * and terminate with KEYCODE_ENTER.
     */
    fun onDispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return false

        val now = System.currentTimeMillis()
        val timeDiff = now - lastKeyTime

        if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
            if (barcodeBuffer.isNotEmpty()) {
                val code = barcodeBuffer.toString().trim()
                barcodeBuffer.clear()
                if (code.length >= 2) {
                    Log.i(TAG, "Keyboard wedge scanner auto-read: $code")
                    emitBarcode(code)
                    return true
                }
            }
            return false
        }

        val unicodeChar = event.unicodeChar
        if (unicodeChar != 0 && unicodeChar != 10 && unicodeChar != 13) {
            // Scanner characters arrive rapidly
            if (timeDiff < 70 || barcodeBuffer.isEmpty()) {
                barcodeBuffer.append(unicodeChar.toChar())
                lastKeyTime = now
            } else {
                // If paused too long (> 120ms), start fresh buffer
                barcodeBuffer.clear()
                barcodeBuffer.append(unicodeChar.toChar())
                lastKeyTime = now
            }
        }

        return false
    }

    fun emitBarcode(code: String) {
        val cleanCode = code.trim()
        if (cleanCode.isNotEmpty()) {
            playBeep()
            _scannedCodeFlow.tryEmit(cleanCode)
        }
    }

    fun playBeep() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
        } catch (e: Exception) {
            Log.w(TAG, "Beep failed", e)
        }
    }

    fun release() {
        try {
            context.unregisterReceiver(sunmiReceiver)
        } catch (_: Exception) { }
        toneGenerator?.release()
        toneGenerator = null
    }
}
