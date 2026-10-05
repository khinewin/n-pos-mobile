package com.npos.app.pos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.input.InputManager
import android.hardware.usb.UsbManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.util.Log
import android.view.InputDevice
import android.view.KeyEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages auto-detection and connection status of barcode scanners on Android POS devices:
 * 1. Real-time hardware status detection: USB OTG & Bluetooth HID scanners, Sunmi built-in laser scanner
 * 2. Sunmi POS built-in laser scanner broadcasts
 * 3. Hardware USB / Bluetooth barcode gun keyboard wedge (rapid key input ending with ENTER)
 */
class BarcodeScannerManager(private val context: Context) {

    companion object {
        private const val TAG = "BarcodeScannerManager"
        private const val SUNMI_SCAN_ACTION = "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED"
        private const val SUNMI_INTENT_ACTION = "sunmi.intent.action.SCAN_RESULT"
    }

    private val _scannedCodeFlow = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val scannedCodeFlow: SharedFlow<String> = _scannedCodeFlow.asSharedFlow()

    private val _isScannerConnected = MutableStateFlow(false)
    val isScannerConnected: StateFlow<Boolean> = _isScannerConnected.asStateFlow()

    private val _connectedDeviceName = MutableStateFlow<String?>(null)
    val connectedDeviceName: StateFlow<String?> = _connectedDeviceName.asStateFlow()

    private val inputManager = context.getSystemService(Context.INPUT_SERVICE) as? InputManager
    private val usbManager = context.getSystemService(Context.USB_SERVICE) as? UsbManager

    private val barcodeBuffer = StringBuilder()
    private var lastKeyTime = 0L

    private var toneGenerator: ToneGenerator? = null

    private val inputDeviceListener = object : InputManager.InputDeviceListener {
        override fun onInputDeviceAdded(deviceId: Int) {
            updateScannerConnectionStatus()
        }
        override fun onInputDeviceRemoved(deviceId: Int) {
            updateScannerConnectionStatus()
        }
        override fun onInputDeviceChanged(deviceId: Int) {
            updateScannerConnectionStatus()
        }
    }

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            updateScannerConnectionStatus()
        }
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

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            Log.w(TAG, "ToneGenerator init failed", e)
        }
        registerSunmiReceiver()
        registerDeviceListeners()
        updateScannerConnectionStatus()
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

    private fun registerDeviceListeners() {
        try {
            inputManager?.registerInputDeviceListener(inputDeviceListener, null)
        } catch (e: Exception) {
            Log.w(TAG, "InputDeviceListener registration failed", e)
        }

        try {
            val usbFilter = IntentFilter().apply {
                addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(usbReceiver, usbFilter, Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(usbReceiver, usbFilter)
            }
        } catch (e: Exception) {
            Log.w(TAG, "USB receiver registration failed", e)
        }
    }

    /**
     * Checks if a hardware barcode scanner is connected via USB, Bluetooth, or built-in POS laser.
     */
    fun updateScannerConnectionStatus(): Boolean {
        var connected = false
        var deviceName: String? = null

        // 1. Sunmi POS check (Built-in hardware laser scanner)
        val isSunmi = Build.MANUFACTURER.contains("SUNMI", ignoreCase = true) ||
                Build.BRAND.contains("SUNMI", ignoreCase = true) ||
                Build.MODEL.contains("SUNMI", ignoreCase = true)
        if (isSunmi) {
            connected = true
            deviceName = "Sunmi Laser Scanner"
        }

        // 2. External USB / Bluetooth hardware keyboard scanners (HID)
        if (!connected) {
            try {
                val deviceIds = InputDevice.getDeviceIds()
                for (id in deviceIds) {
                    val dev = InputDevice.getDevice(id) ?: continue
                    val hasKeyboard = (dev.sources and InputDevice.SOURCE_KEYBOARD) == InputDevice.SOURCE_KEYBOARD
                    val isExternalDevice = dev.isExternal || !dev.isVirtual
                    val nameLower = dev.name.lowercase()
                    val isExcluded = nameLower.contains("power") ||
                            nameLower.contains("gpio") ||
                            nameLower.contains("volume") ||
                            nameLower.contains("vkey") ||
                            nameLower.contains("virtual") ||
                            nameLower.contains("touchscreen")

                    if (hasKeyboard && isExternalDevice && !isExcluded && dev.keyboardType == InputDevice.KEYBOARD_TYPE_ALPHABETIC) {
                        connected = true
                        deviceName = dev.name
                        break
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "InputDevice check failed", e)
            }
        }

        // 3. USB Manager check for plugged USB devices (OTG scanner, USB barcode reader)
        if (!connected && usbManager != null) {
            try {
                val deviceList = usbManager.deviceList
                if (deviceList.isNotEmpty()) {
                    for ((_, usbDev) in deviceList) {
                        connected = true
                        deviceName = usbDev.productName ?: usbDev.deviceName ?: "USB Scanner"
                        break
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "UsbManager check failed", e)
            }
        }

        _isScannerConnected.value = connected
        _connectedDeviceName.value = if (connected) deviceName else null
        return connected
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
        try {
            inputManager?.unregisterInputDeviceListener(inputDeviceListener)
        } catch (_: Exception) { }
        try {
            context.unregisterReceiver(usbReceiver)
        } catch (_: Exception) { }
        toneGenerator?.release()
        toneGenerator = null
    }
}
