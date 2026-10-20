package com.npos.app.pos

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import com.npos.app.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import kotlin.math.ceil

/**
 * Printer Connection Type: Bluetooth (SPP) or Network (TCP/IP socket port 9100)
 */
enum class PrinterConnectionType {
    BLUETOOTH,
    NETWORK
}

/**
 * Receipt Print Mode:
 * GRAPHIC_MYANMAR - Renders receipt as a high-clarity 203 DPI raster bitmap with native Myanmar Unicode font (Padauk), preventing '???' characters.
 * AUTO - Automatically switches to Graphic Mode if Myanmar text is detected in receipt or shop info.
 * RAW_TEXT - Traditional ESC/POS text command printing (English/Numbers only).
 */
enum class ReceiptPrintMode {
    GRAPHIC_MYANMAR,
    AUTO,
    RAW_TEXT
}

/**
 * Manages Bluetooth and Network ESC/POS Thermal Printing.
 * Supports silent, direct printing on 58mm, 72mm, 80mm, and custom thermal receipt printers.
 */
class ThermalPrinterManager(private val context: Context) {

    companion object {
        private const val TAG = "ThermalPrinterManager"
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private const val PREFS_NAME = "npos_printer_prefs"
        private const val KEY_PRINTER_TYPE = "selected_printer_type" // "BLUETOOTH" or "NETWORK"
        private const val KEY_PRINTER_MAC = "selected_printer_mac"
        private const val KEY_PRINTER_NAME = "selected_printer_name"
        private const val KEY_NETWORK_IP = "network_printer_ip"
        private const val KEY_NETWORK_PORT = "network_printer_port"
        private const val KEY_PAPER_WIDTH = "paper_width" // "58mm", "80mm", etc.
        private const val KEY_AUTO_CUT = "auto_cut"
        private const val KEY_SHOP_NAME = "receipt_shop_name"
        private const val KEY_SHOP_TAGLINE = "receipt_shop_tagline"
        private const val KEY_SHOP_ADDRESS = "receipt_shop_address"
        private const val KEY_SHOP_PHONE = "receipt_shop_phone"
        private const val KEY_FOOTER_GREETING = "receipt_footer_greeting"
        private const val KEY_FOOTER_NOTE = "receipt_footer_note"
        private const val KEY_CUSTOM_MM = "paper_custom_mm"
        private const val KEY_CUSTOM_COLUMNS = "paper_custom_columns"
        private const val KEY_PRINT_MODE = "receipt_print_mode"

        const val DEFAULT_NETWORK_PORT = 9100

        val PAPER_SIZES = listOf(
            PaperSizeInfo("58mm", "58mm (2\" Portable)", 32),
            PaperSizeInfo("72mm", "72mm (2.8\" Compact)", 42),
            PaperSizeInfo("76mm", "76mm (3\" Impact/Dot Matrix)", 40),
            PaperSizeInfo("80mm", "80mm (3\" Standard POS)", 48),
            PaperSizeInfo("100mm", "100mm (4\" Wide Logistics)", 64)
        )
    }

    data class PaperSizeInfo(
        val id: String,
        val label: String,
        val columns: Int
    )

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val bluetoothManager: BluetoothManager? = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    private val _connectionType = MutableStateFlow(
        try {
            PrinterConnectionType.valueOf(prefs.getString(KEY_PRINTER_TYPE, PrinterConnectionType.BLUETOOTH.name) ?: PrinterConnectionType.BLUETOOTH.name)
        } catch (_: Exception) {
            PrinterConnectionType.BLUETOOTH
        }
    )
    val connectionType: StateFlow<PrinterConnectionType> = _connectionType.asStateFlow()

    private val _networkIp = MutableStateFlow(prefs.getString(KEY_NETWORK_IP, "") ?: "")
    val networkIp: StateFlow<String> = _networkIp.asStateFlow()

    private val _networkPort = MutableStateFlow(prefs.getInt(KEY_NETWORK_PORT, DEFAULT_NETWORK_PORT))
    val networkPort: StateFlow<Int> = _networkPort.asStateFlow()

    private val _printerStatus = MutableStateFlow("Ready")
    val printerStatus: StateFlow<String> = _printerStatus.asStateFlow()

    private val _isPrinting = MutableStateFlow(false)
    val isPrinting: StateFlow<Boolean> = _isPrinting.asStateFlow()

    private val _selectedDevice = MutableStateFlow<BluetoothDevice?>(null)
    val selectedDevice: StateFlow<BluetoothDevice?> = _selectedDevice.asStateFlow()

    private val _paperWidth = MutableStateFlow(prefs.getString(KEY_PAPER_WIDTH, "58mm") ?: "58mm")
    val paperWidth: StateFlow<String> = _paperWidth.asStateFlow()

    private val _customMm = MutableStateFlow(prefs.getInt(KEY_CUSTOM_MM, 58))
    val customMm: StateFlow<Int> = _customMm.asStateFlow()

    private val _customColumns = MutableStateFlow(prefs.getInt(KEY_CUSTOM_COLUMNS, 32))
    val customColumns: StateFlow<Int> = _customColumns.asStateFlow()

    private val _shopName = MutableStateFlow(
        prefs.getString(KEY_SHOP_NAME, "N-POS")?.replace("!", "")?.trim()?.ifEmpty { "N-POS" } ?: "N-POS"
    )
    val shopName: StateFlow<String> = _shopName.asStateFlow()

    private val _shopTagline = MutableStateFlow(
        prefs.getString(KEY_SHOP_TAGLINE, "POS Software for Minimart")?.replace("!", "")?.trim() ?: "POS Software for Minimart"
    )
    val shopTagline: StateFlow<String> = _shopTagline.asStateFlow()

    private val _shopAddress = MutableStateFlow(
        prefs.getString(KEY_SHOP_ADDRESS, "Mawlamyine, Mon State")?.replace("!", "")?.trim() ?: "Mawlamyine, Mon State"
    )
    val shopAddress: StateFlow<String> = _shopAddress.asStateFlow()

    private val _shopPhone = MutableStateFlow(
        prefs.getString(KEY_SHOP_PHONE, "09-970488345")?.replace("!", "")?.trim() ?: "09-970488345"
    )
    val shopPhone: StateFlow<String> = _shopPhone.asStateFlow()

    private val _footerGreeting = MutableStateFlow(
        prefs.getString(KEY_FOOTER_GREETING, "Thank you for shopping with us")?.replace("!", "")?.trim() ?: "Thank you for shopping with us"
    )
    val footerGreeting: StateFlow<String> = _footerGreeting.asStateFlow()

    private val _footerNote = MutableStateFlow(
        prefs.getString(KEY_FOOTER_NOTE, "Please come again")?.replace("!", "")?.trim() ?: "Please come again"
    )
    val footerNote: StateFlow<String> = _footerNote.asStateFlow()

    private val _printMode = MutableStateFlow(
        try {
            ReceiptPrintMode.valueOf(prefs.getString(KEY_PRINT_MODE, ReceiptPrintMode.GRAPHIC_MYANMAR.name) ?: ReceiptPrintMode.GRAPHIC_MYANMAR.name)
        } catch (_: Exception) {
            ReceiptPrintMode.GRAPHIC_MYANMAR
        }
    )
    val printMode: StateFlow<ReceiptPrintMode> = _printMode.asStateFlow()

    fun setPrintMode(mode: ReceiptPrintMode) {
        _printMode.value = mode
        prefs.edit().putString(KEY_PRINT_MODE, mode.name).apply()
    }

    init {
        restoreSavedPrinter()
    }

    private fun restoreSavedPrinter() {
        val savedMac = prefs.getString(KEY_PRINTER_MAC, null)
        if (!savedMac.isNullOrEmpty() && bluetoothAdapter != null) {
            try {
                val device = bluetoothAdapter.getRemoteDevice(savedMac)
                _selectedDevice.value = device
            } catch (e: Exception) {
                Log.w(TAG, "Failed to restore printer device: ${e.message}")
            }
        }
    }

    fun setPaperWidth(width: String) {
        _paperWidth.value = width
        prefs.edit().putString(KEY_PAPER_WIDTH, width).apply()
    }

    fun setCustomPaperWidth(mm: Int, columns: Int) {
        val safeMm = mm.coerceIn(30, 250)
        val safeCols = columns.coerceIn(16, 120)
        _customMm.value = safeMm
        _customColumns.value = safeCols
        _paperWidth.value = "custom"
        prefs.edit()
            .putString(KEY_PAPER_WIDTH, "custom")
            .putInt(KEY_CUSTOM_MM, safeMm)
            .putInt(KEY_CUSTOM_COLUMNS, safeCols)
            .apply()
    }

    fun getPaperColumns(width: String = _paperWidth.value): Int {
        return when (width) {
            "58mm" -> 32
            "72mm" -> 42
            "76mm" -> 40
            "80mm" -> 48
            "100mm" -> 64
            "custom" -> _customColumns.value
            else -> {
                if (width.startsWith("custom")) {
                    _customColumns.value
                } else {
                    val num = width.filter { it.isDigit() }.toIntOrNull()
                    if (num != null) {
                        if (num in 20..96) num else (num * 0.6).toInt().coerceIn(20, 96)
                    } else {
                        32
                    }
                }
            }
        }
    }

    /**
     * Printable width in dots (pixels) for bitmap rendering.
     * Guaranteed to be a multiple of 8 for ESC/POS raster byte alignment.
     */
    fun getPaperWidthDots(width: String = _paperWidth.value): Int {
        return when (width) {
            "58mm" -> 384
            "72mm" -> 512
            "76mm" -> 512
            "80mm" -> 576
            "100mm" -> 800
            "custom" -> ((_customMm.value * 8) / 8 * 8).coerceIn(384, 800)
            else -> 384
        }
    }

    fun updateReceiptConfig(
        name: String,
        tagline: String,
        address: String,
        phone: String,
        greeting: String,
        note: String
    ) {
        val finalName = name.replace("!", "").trim().ifEmpty { "N-POS" }
        val finalTagline = tagline.replace("!", "").trim()
        val finalAddress = address.replace("!", "").trim()
        val finalPhone = phone.replace("!", "").trim()
        val finalGreeting = greeting.replace("!", "").trim()
        val finalNote = note.replace("!", "").trim()

        _shopName.value = finalName
        _shopTagline.value = finalTagline
        _shopAddress.value = finalAddress
        _shopPhone.value = finalPhone
        _footerGreeting.value = finalGreeting
        _footerNote.value = finalNote

        prefs.edit()
            .putString(KEY_SHOP_NAME, finalName)
            .putString(KEY_SHOP_TAGLINE, finalTagline)
            .putString(KEY_SHOP_ADDRESS, finalAddress)
            .putString(KEY_SHOP_PHONE, finalPhone)
            .putString(KEY_FOOTER_GREETING, finalGreeting)
            .putString(KEY_FOOTER_NOTE, finalNote)
            .apply()
    }

    fun saveSelectedPrinter(device: BluetoothDevice) {
        _selectedDevice.value = device
        try {
            val name = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                device.name ?: device.address
            } else {
                @Suppress("DEPRECATION")
                device.name ?: device.address
            }
            prefs.edit()
                .putString(KEY_PRINTER_MAC, device.address)
                .putString(KEY_PRINTER_NAME, name)
                .apply()
        } catch (e: SecurityException) {
            prefs.edit()
                .putString(KEY_PRINTER_MAC, device.address)
                .putString(KEY_PRINTER_NAME, device.address)
                .apply()
        }
    }

    @SuppressLint("MissingPermission")
    fun getPairedPrinters(): List<BluetoothDevice> {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            return emptyList()
        }
        return try {
            bluetoothAdapter.bondedDevices?.toList() ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting paired devices", e)
            emptyList()
        }
    }

    fun setConnectionType(type: PrinterConnectionType) {
        _connectionType.value = type
        prefs.edit().putString(KEY_PRINTER_TYPE, type.name).apply()
    }

    fun saveNetworkPrinter(ip: String, port: Int = DEFAULT_NETWORK_PORT) {
        val cleanIp = ip.trim()
        val cleanPort = if (port in 1..65535) port else DEFAULT_NETWORK_PORT
        _networkIp.value = cleanIp
        _networkPort.value = cleanPort
        _connectionType.value = PrinterConnectionType.NETWORK
        prefs.edit()
            .putString(KEY_PRINTER_TYPE, PrinterConnectionType.NETWORK.name)
            .putString(KEY_NETWORK_IP, cleanIp)
            .putInt(KEY_NETWORK_PORT, cleanPort)
            .apply()
    }

    suspend fun testNetworkConnection(ip: String, port: Int = DEFAULT_NETWORK_PORT): Result<String> = withContext(Dispatchers.IO) {
        val cleanIp = ip.trim()
        if (cleanIp.isEmpty()) {
            return@withContext Result.failure(Exception("IP address cannot be empty"))
        }
        var socket: Socket? = null
        try {
            socket = Socket()
            socket.connect(InetSocketAddress(cleanIp, port), 3000)
            Result.success("Connection to $cleanIp:$port succeeded!")
        } catch (e: Exception) {
            Result.failure(Exception("Cannot connect to $cleanIp:$port: ${e.message}"))
        } finally {
            try {
                socket?.close()
            } catch (_: Exception) {}
        }
    }

    fun isPrinterConfigured(): Boolean {
        return when (_connectionType.value) {
            PrinterConnectionType.BLUETOOTH -> _selectedDevice.value != null
            PrinterConnectionType.NETWORK -> _networkIp.value.trim().isNotEmpty()
        }
    }

    @SuppressLint("MissingPermission")
    fun getPrinterDisplayName(): String {
        return when (_connectionType.value) {
            PrinterConnectionType.BLUETOOTH -> {
                val dev = _selectedDevice.value ?: return "No Printer Configured"
                try {
                    dev.name ?: dev.address
                } catch (e: SecurityException) {
                    dev.address
                }
            }
            PrinterConnectionType.NETWORK -> {
                val ip = _networkIp.value.trim()
                if (ip.isEmpty()) "No Network Printer" else "LAN: $ip:${_networkPort.value}"
            }
        }
    }

    /**
     * Executes direct silent print for sales receipt data (JSON string)
     * using either Bluetooth SPP socket or Network TCP/IP raw socket (port 9100).
     */
    suspend fun printReceipt(jsonData: String): Result<String> = withContext(Dispatchers.IO) {
        if (!isPrinterConfigured()) {
            val msg = if (_connectionType.value == PrinterConnectionType.BLUETOOTH) {
                "No Bluetooth printer selected. Please configure in settings."
            } else {
                "No Network printer IP configured. Please configure in settings."
            }
            return@withContext Result.failure(Exception(msg))
        }

        _isPrinting.value = true
        _printerStatus.value = "Connecting to ${getPrinterDisplayName()}..."

        val escPosBytes = generateReceiptBytes(jsonData, _paperWidth.value)

        when (_connectionType.value) {
            PrinterConnectionType.BLUETOOTH -> {
                val device = _selectedDevice.value
                    ?: run {
                        _isPrinting.value = false
                        return@withContext Result.failure(Exception("No Bluetooth printer selected"))
                    }

                var socket: BluetoothSocket? = null
                try {
                    try {
                        bluetoothAdapter?.cancelDiscovery()
                    } catch (_: Exception) {}

                    socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                    socket.connect()

                    _printerStatus.value = "Printing receipt..."
                    val outputStream: OutputStream = socket.outputStream
                    outputStream.write(escPosBytes)
                    outputStream.flush()

                    kotlinx.coroutines.delay(200)

                    _printerStatus.value = "Print successful"
                    Result.success("Receipt printed successfully via Bluetooth")
                } catch (e: Exception) {
                    Log.e(TAG, "Bluetooth print failed", e)
                    _printerStatus.value = "Print error: ${e.message}"
                    Result.failure(e)
                } finally {
                    try {
                        socket?.close()
                    } catch (_: Exception) {}
                    _isPrinting.value = false
                }
            }
            PrinterConnectionType.NETWORK -> {
                val ip = _networkIp.value.trim()
                val port = _networkPort.value
                var socket: Socket? = null
                try {
                    _printerStatus.value = "Connecting to $ip:$port..."
                    socket = Socket()
                    socket.connect(InetSocketAddress(ip, port), 4000)
                    socket.soTimeout = 5000

                    _printerStatus.value = "Sending print data..."
                    val outputStream: OutputStream = socket.getOutputStream()
                    outputStream.write(escPosBytes)
                    outputStream.flush()

                    kotlinx.coroutines.delay(200)

                    _printerStatus.value = "Print successful"
                    Result.success("Receipt printed successfully via Network ($ip:$port)")
                } catch (e: Exception) {
                    Log.e(TAG, "Network print failed", e)
                    _printerStatus.value = "Network print error: ${e.message}"
                    Result.failure(e)
                } finally {
                    try {
                        socket?.close()
                    } catch (_: Exception) {}
                    _isPrinting.value = false
                }
            }
        }
    }

    /**
     * Sends raw ESC/POS or TSPL bytes to the configured Bluetooth or Network printer.
     */
    suspend fun sendRawPrinterBytes(rawBytes: ByteArray): Result<String> = withContext(Dispatchers.IO) {
        if (!isPrinterConfigured()) {
            val msg = if (_connectionType.value == PrinterConnectionType.BLUETOOTH) {
                "No Bluetooth printer selected. Please configure in settings."
            } else {
                "No Network printer IP configured. Please configure in settings."
            }
            return@withContext Result.failure(Exception(msg))
        }

        _isPrinting.value = true
        _printerStatus.value = "Connecting to ${getPrinterDisplayName()}..."

        when (_connectionType.value) {
            PrinterConnectionType.BLUETOOTH -> {
                val device = _selectedDevice.value
                    ?: run {
                        _isPrinting.value = false
                        return@withContext Result.failure(Exception("No Bluetooth printer selected"))
                    }

                var socket: BluetoothSocket? = null
                try {
                    try {
                        bluetoothAdapter?.cancelDiscovery()
                    } catch (_: Exception) {}

                    socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                    socket.connect()

                    _printerStatus.value = "Printing barcode label..."
                    val outputStream: OutputStream = socket.outputStream
                    outputStream.write(rawBytes)
                    outputStream.flush()

                    kotlinx.coroutines.delay(250)

                    _printerStatus.value = "Print successful"
                    Result.success("Barcode label printed via Bluetooth")
                } catch (e: Exception) {
                    Log.e(TAG, "Bluetooth barcode print failed", e)
                    _printerStatus.value = "Print error: ${e.message}"
                    Result.failure(e)
                } finally {
                    try {
                        socket?.close()
                    } catch (_: Exception) {}
                    _isPrinting.value = false
                }
            }
            PrinterConnectionType.NETWORK -> {
                val ip = _networkIp.value.trim()
                val port = _networkPort.value
                var socket: Socket? = null
                try {
                    _printerStatus.value = "Connecting to $ip:$port..."
                    socket = Socket()
                    socket.connect(InetSocketAddress(ip, port), 4000)
                    socket.soTimeout = 5000

                    _printerStatus.value = "Printing barcode label..."
                    val outputStream: OutputStream = socket.getOutputStream()
                    outputStream.write(rawBytes)
                    outputStream.flush()

                    kotlinx.coroutines.delay(250)

                    _printerStatus.value = "Print successful"
                    Result.success("Barcode label printed via Network ($ip:$port)")
                } catch (e: Exception) {
                    Log.e(TAG, "Network barcode print failed", e)
                    _printerStatus.value = "Network print error: ${e.message}"
                    Result.failure(e)
                } finally {
                    try {
                        socket?.close()
                    } catch (_: Exception) {}
                    _isPrinting.value = false
                }
            }
        }
    }

    /**
     * Prints a single product's barcode label (with specified number of copies).
     */
    suspend fun printBarcodeLabel(
        data: BarcodeLabelData,
        config: BarcodeLabelConfig,
        copies: Int = 1
    ): Result<String> = withContext(Dispatchers.IO) {
        val safeCopies = copies.coerceIn(1, 200)
        val bitmap = BarcodeLabelGenerator.renderLabelBitmap(context, data, config)
        val size = BarcodeLabelGenerator.getSizeById(config.labelSizeId)

        val payloadBytes = when (config.protocol) {
            BarcodePrintProtocol.TSPL_LABEL -> {
                BarcodeLabelGenerator.generateTsplBytes(bitmap, size, safeCopies)
            }
            BarcodePrintProtocol.ESC_POS_GRAPHIC -> {
                val out = ByteArrayOutputStream()
                val singleBytes = convertBitmapToEscPos(bitmap)
                for (i in 0 until safeCopies) {
                    out.write(singleBytes)
                }
                out.toByteArray()
            }
        }
        sendRawPrinterBytes(payloadBytes)
    }

    /**
     * Prints a batch queue of barcode labels in one connection.
     */
    suspend fun printBarcodeLabelsBatch(
        batch: List<Pair<BarcodeLabelData, Int>>,
        config: BarcodeLabelConfig
    ): Result<String> = withContext(Dispatchers.IO) {
        if (batch.isEmpty()) {
            return@withContext Result.failure(Exception("Print queue is empty"))
        }
        val size = BarcodeLabelGenerator.getSizeById(config.labelSizeId)
        val out = ByteArrayOutputStream()

        for ((data, copies) in batch) {
            val safeCopies = copies.coerceIn(1, 200)
            val bitmap = BarcodeLabelGenerator.renderLabelBitmap(context, data, config)
            when (config.protocol) {
                BarcodePrintProtocol.TSPL_LABEL -> {
                    out.write(BarcodeLabelGenerator.generateTsplBytes(bitmap, size, safeCopies))
                }
                BarcodePrintProtocol.ESC_POS_GRAPHIC -> {
                    val singleBytes = convertBitmapToEscPos(bitmap)
                    for (i in 0 until safeCopies) {
                        out.write(singleBytes)
                    }
                }
            }
        }
        sendRawPrinterBytes(out.toByteArray())
    }

    /**
     * Test print function to verify printer connection, alignment, and Myanmar Unicode font rendering.
     */
    suspend fun printTestReceipt(): Result<String> = withContext(Dispatchers.IO) {
        val testJson = JSONObject().apply {
            put("voucher_no", "n1-261015-131515")
            put("sale_date", "15-10-2026 13:15")
            put("total_amount", 48200)
            put("extra_discount", 0)
            put("paid", 50000)
            put("change", 1800)
            put("customer", JSONObject().apply {
                put("name", "Default Customer")
            })
            put("user", JSONObject().apply {
                put("displayName", "Cashier-1")
            })
            val items = org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("item_name", "ဇကာ(1200)")
                    put("sale_qty", 1)
                    put("qty_sale_price", 1200)
                    put("final_amount", 1200)
                })
                put(JSONObject().apply {
                    put("item_name", "Centella Ampoule")
                    put("sale_qty", 1)
                    put("qty_sale_price", 47000)
                    put("final_amount", 47000)
                })
            }
            put("saleItems", items)
        }
        printReceipt(testJson.toString())
    }

    /**
     * Dispatches receipt generation based on user's selected ReceiptPrintMode.
     * In GRAPHIC_MYANMAR (default) or AUTO when Myanmar characters are present,
     * the receipt is rendered directly onto a high-contrast 203 DPI raster bitmap
     * with Android's native Padauk Myanmar Unicode font engine.
     */
    fun generateReceiptBytes(jsonStr: String, paperWidth: String): ByteArray {
        return when (_printMode.value) {
            ReceiptPrintMode.GRAPHIC_MYANMAR -> generateBitmapReceipt(jsonStr, paperWidth)
            ReceiptPrintMode.RAW_TEXT -> generateEscPosReceipt(jsonStr, paperWidth)
            ReceiptPrintMode.AUTO -> {
                if (containsMyanmarText(jsonStr) ||
                    containsMyanmarText(_shopName.value) ||
                    containsMyanmarText(_shopTagline.value) ||
                    containsMyanmarText(_shopAddress.value) ||
                    containsMyanmarText(_footerGreeting.value) ||
                    containsMyanmarText(_footerNote.value)
                ) {
                    generateBitmapReceipt(jsonStr, paperWidth)
                } else {
                    generateEscPosReceipt(jsonStr, paperWidth)
                }
            }
        }
    }

    /**
     * Checks if a string contains any Myanmar Unicode code points
     * (Base Myanmar: 0x1000 - 0x109F, Extended-A: 0xAA60 - 0xAA7F, Extended-B: 0xA9E0 - 0xA9FF).
     */
    fun containsMyanmarText(text: String?): Boolean {
        if (text == null) return false
        for (i in 0 until text.length) {
            val cp = text.codePointAt(i)
            if ((cp in 0x1000..0x109F) || (cp in 0xAA60..0xAA7F) || (cp in 0xA9E0..0xA9FF)) {
                return true
            }
        }
        return false
    }

    private fun getMyanmarTypeface(): Typeface {
        return try {
            ResourcesCompat.getFont(context, R.font.padauk) ?: Typeface.DEFAULT
        } catch (_: Exception) {
            Typeface.DEFAULT
        }
    }

    /**
     * Generates a high-clarity 203 DPI monochrome raster bitmap of the receipt
     * using Android's native Canvas and Padauk Myanmar font.
     * Completely eliminates '???' marks and garbled characters on ESC/POS thermal printers.
     */
    fun generateBitmapReceipt(jsonStr: String, paperWidth: String): ByteArray {
        val width = getPaperWidthDots(paperWidth)
        val is80mm = width >= 576
        val margin = if (is80mm) 16 else 10
        val contentWidth = width - (margin * 2)

        val tf = getMyanmarTypeface()
        val tfBold = Typeface.create(tf, Typeface.BOLD)

        // Shop name made bigger as requested (prominent branding)
        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            typeface = tfBold
            textSize = if (is80mm) 44f else 34f
        }
        val subTitlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            typeface = tf
            textSize = if (is80mm) 18f else 15f
        }
        val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            typeface = tf
            textSize = if (is80mm) 19f else 16f
        }
        val bodyBoldPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            typeface = tfBold
            textSize = if (is80mm) 19f else 16f
        }
        val dottedLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            strokeWidth = 1.5f
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
        }
        val solidLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }

        fun makeLayout(
            text: String,
            paint: TextPaint,
            availWidth: Int,
            alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL
        ): StaticLayout {
            val safeWidth = availWidth.coerceAtLeast(10)
            return StaticLayout.Builder.obtain(text, 0, text.length, paint, safeWidth)
                .setAlignment(alignment)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()
        }

        // Clean Header Strings (strip any leading or stray '!' marks)
        val cleanShopName = _shopName.value.replace("!", "").trim().ifEmpty { "N-POS" }
        val cleanTagline = _shopTagline.value.replace("!", "").trim()
        val cleanAddress = _shopAddress.value.replace("!", "").trim()
        val cleanPhone = _shopPhone.value.replace("!", "").trim()
        val cleanGreeting = _footerGreeting.value.replace("!", "").trim()
        val cleanNote = _footerNote.value.replace("!", "").trim()

        // Parse JSON data
        val json = try { JSONObject(jsonStr) } catch (_: Exception) { JSONObject() }
        val voucherNo = json.optString("voucher_no", "---")
        val rawDate = json.optString("sale_date", json.optString("print_date", ""))
        val saleDate = if (rawDate.contains("-") && rawDate.indexOf("-") == 4) {
            try {
                val inFmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
                val outFmt = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                inFmt.parse(rawDate)?.let { outFmt.format(it) } ?: rawDate
            } catch (_: Exception) { rawDate }
        } else {
            rawDate
        }
        val customerObj = json.optJSONObject("customer")
        val customerName = customerObj?.optString("name", "Default Customer") ?: "Default Customer"
        val userObj = json.optJSONObject("user")
        val cashier = userObj?.optString("displayName", "Cashier-1") ?: "Cashier-1"

        val totalAmount = json.optDouble("total_amount", 0.0)
        val discount = json.optDouble("extra_discount", 0.0)
        val netAmount = json.optDouble("net_amount", totalAmount - discount)
        val paid = json.optDouble("paid", 0.0)
        val change = json.optDouble("change", 0.0)

        val saleItems = json.optJSONArray("saleItems") ?: json.optJSONArray("products")

        // Table column dimensions: #, Item (includes qty * price), Amount
        // Qty column is removed as requested
        val colIdxW = if (is80mm) 36f else 26f
        val colAmtW = if (is80mm) 140f else 110f
        val colSpace = if (is80mm) 8f else 6f

        val colIdxX = margin.toFloat()
        val colItemX = colIdxX + colIdxW + colSpace
        val colAmtRight = (width - margin).toFloat()
        val maxSingleLineItemW = colAmtRight - colAmtW - colSpace - colItemX

        // Precompute layouts for items (supporting: qty * price after item_name & auto-wrap)
        data class ItemRenderInfo(
            val idx: String,
            val fullDesc: String,
            val itemNameOnly: String,
            val qtyPriceStr: String,
            val amount: String,
            val isSingleLine: Boolean,
            val nameLayout: StaticLayout,
            val rowHeight: Float
        )
        val itemRenderList = mutableListOf<ItemRenderInfo>()
        if (saleItems != null && saleItems.length() > 0) {
            for (i in 0 until saleItems.length()) {
                val itObj = saleItems.getJSONObject(i)
                val idx = (i + 1).toString()
                val itemName = itObj.optString("item_name", "Item").replace("!", "").trim()
                val qty = itObj.optInt("sale_qty", 1).toString()
                val unitPrice = itObj.optDouble("qty_sale_price", itObj.optDouble("unit_price", 0.0))
                val amt = formatMoney(itObj.optDouble("final_amount", 0.0))

                // Format: qty * qty_sale_price after item_name
                val qtyPriceStr = if (unitPrice > 0) " (${qty} x ${formatMoney(unitPrice)})" else if ((itObj.optInt("sale_qty", 1)) > 1) " (${qty})" else ""
                val fullDesc = "$itemName$qtyPriceStr"

                val fullMeasuredW = bodyPaint.measureText(fullDesc)

                if (fullMeasuredW <= maxSingleLineItemW) {
                    // Fits cleanly on 1 line
                    val layout = makeLayout(fullDesc, bodyPaint, maxSingleLineItemW.toInt().coerceAtLeast(40), Layout.Alignment.ALIGN_NORMAL)
                    val rowH = bodyPaint.textSize + 4f
                    itemRenderList.add(ItemRenderInfo(idx, fullDesc, itemName, qtyPriceStr, amt, true, layout, rowH))
                } else {
                    // Does NOT fit on 1 line -> Name on top line, qty * price and Amount on next line
                    val fullItemW = (width - margin - colItemX).toInt().coerceAtLeast(60)
                    val layout = makeLayout(itemName, bodyPaint, fullItemW, Layout.Alignment.ALIGN_NORMAL)
                    val rowH = layout.height + bodyPaint.textSize + 4f
                    itemRenderList.add(ItemRenderInfo(idx, fullDesc, itemName, qtyPriceStr, amt, false, layout, rowH))
                }
            }
        }

        // Layouts for Header & Footer
        val shopNameLayout = makeLayout(cleanShopName, titlePaint, contentWidth, Layout.Alignment.ALIGN_CENTER)
        val taglineLayout = if (cleanTagline.isNotEmpty()) makeLayout(cleanTagline, subTitlePaint, contentWidth, Layout.Alignment.ALIGN_CENTER) else null
        val addressLayout = if (cleanAddress.isNotEmpty()) makeLayout(cleanAddress, bodyPaint, contentWidth, Layout.Alignment.ALIGN_CENTER) else null
        val phoneLayout = if (cleanPhone.isNotEmpty()) makeLayout("Ph: $cleanPhone", bodyPaint, contentWidth, Layout.Alignment.ALIGN_CENTER) else null

        val greetingLayout = if (cleanGreeting.isNotEmpty()) makeLayout(cleanGreeting, bodyBoldPaint, contentWidth, Layout.Alignment.ALIGN_CENTER) else null
        val noteLayout = if (cleanNote.isNotEmpty()) makeLayout(cleanNote, bodyPaint, contentWidth, Layout.Alignment.ALIGN_CENTER) else null

        // --- Calculate Total Height (Tight, exact margins) ---
        var measuredHeight = 10f // top margin
        measuredHeight += shopNameLayout.height + 2f // tightened margin between shop and category
        taglineLayout?.let { measuredHeight += it.height + 2f }
        addressLayout?.let { measuredHeight += it.height + 2f }
        phoneLayout?.let { measuredHeight += it.height + 3f }
        measuredHeight += 8f // divider + spacing

        val metaLineHeight = bodyPaint.textSize + 3f
        measuredHeight += (metaLineHeight * 4) + 4f // Invoice No, Date, Customer, Cashier
        measuredHeight += 8f // divider

        val headerLineHeight = bodyBoldPaint.textSize + 3f
        measuredHeight += headerLineHeight + 4f // Table Header (#, Item, Amount)
        measuredHeight += 8f // divider under header

        if (itemRenderList.isNotEmpty()) {
            for (it in itemRenderList) {
                measuredHeight += it.rowHeight
            }
        } else {
            measuredHeight += 24f // "No items"
        }
        measuredHeight += 8f // divider

        val summaryLineHeight = bodyPaint.textSize + 2f
        if (discount > 0) {
            measuredHeight += summaryLineHeight // Subtotal
            measuredHeight += summaryLineHeight // Discount
        }
        measuredHeight += bodyBoldPaint.textSize + 2f // Total
        measuredHeight += summaryLineHeight // Paid (no extra gap between Total and Paid)
        measuredHeight += summaryLineHeight // Change
        measuredHeight += 8f // divider

        greetingLayout?.let { measuredHeight += it.height + 2f }
        noteLayout?.let { measuredHeight += it.height + 2f }
        measuredHeight += 16f // bottom margin

        val totalHeight = ceil(measuredHeight).toInt().coerceAtLeast(100)

        // --- Draw on Bitmap ---
        val bitmap = Bitmap.createBitmap(width, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        var curY = 10f

        // Centered Text Drawer using exact paper midpoint: width / 2f
        val centerTitlePaint = TextPaint(titlePaint).apply { textAlign = Paint.Align.CENTER }
        val centerSubTitlePaint = TextPaint(subTitlePaint).apply { textAlign = Paint.Align.CENTER }
        val centerBodyPaint = TextPaint(bodyPaint).apply { textAlign = Paint.Align.CENTER }
        val centerBodyBoldPaint = TextPaint(bodyBoldPaint).apply { textAlign = Paint.Align.CENTER }

        fun drawCenterText(text: String, paint: TextPaint, spacingAfter: Float = 2f) {
            val clean = text.replace("!", "").trim()
            if (clean.isEmpty()) return
            val w = paint.measureText(clean)
            val fm = paint.fontMetrics
            val lineH = fm.descent - fm.ascent
            if (w <= contentWidth) {
                val baseline = curY - fm.ascent
                canvas.drawText(clean, width / 2f, baseline, paint)
                curY += lineH + spacingAfter
            } else {
                val sl = makeLayout(clean, paint, contentWidth, Layout.Alignment.ALIGN_CENTER)
                for (line in 0 until sl.lineCount) {
                    val start = sl.getLineStart(line)
                    val end = sl.getLineEnd(line)
                    val lineText = clean.substring(start, end).trim()
                    if (lineText.isNotEmpty()) {
                        val baseline = curY - fm.ascent
                        canvas.drawText(lineText, width / 2f, baseline, paint)
                        curY += lineH + 1f
                    }
                }
                curY += spacingAfter
            }
        }

        fun drawDivider(dotted: Boolean = true, spacingAfter: Float = 8f) {
            val p = if (dotted) dottedLinePaint else solidLinePaint
            val path = Path().apply {
                moveTo(margin.toFloat(), curY + 3f)
                lineTo((width - margin).toFloat(), curY + 3f)
            }
            canvas.drawPath(path, p)
            curY += spacingAfter
        }

        fun drawTwoCols(left: String, right: String, pLeft: TextPaint, pRight: TextPaint = pLeft, spacingAfter: Float = 2f) {
            val maxTextSize = maxOf(pLeft.textSize, pRight.textSize)
            val baseLine = curY + maxTextSize
            canvas.drawText(left, margin.toFloat(), baseLine, pLeft)
            val rightW = pRight.measureText(right)
            canvas.drawText(right, (width - margin).toFloat() - rightW, baseLine, pRight)
            curY += maxTextSize + spacingAfter
        }

        // 1. Header (N-POS, POS Software for Minimart, Mawlamyine, Mon State, Ph: 09-970488345 ALL Strictly Centered)
        drawCenterText(cleanShopName, centerTitlePaint, spacingAfter = 2f)
        if (cleanTagline.isNotEmpty()) {
            drawCenterText(cleanTagline, centerSubTitlePaint, spacingAfter = 2f)
        }
        if (cleanAddress.isNotEmpty()) {
            drawCenterText(cleanAddress, centerBodyPaint, spacingAfter = 2f)
        }
        if (cleanPhone.isNotEmpty()) {
            drawCenterText("Ph: $cleanPhone", centerBodyPaint, spacingAfter = 3f)
        }
        drawDivider(dotted = true, spacingAfter = 8f)

        // 2. Metadata
        drawTwoCols("Invoice No :", voucherNo, bodyBoldPaint, bodyPaint, spacingAfter = 2f)
        drawTwoCols("Date       :", saleDate, bodyBoldPaint, bodyPaint, spacingAfter = 2f)
        drawTwoCols("Customer   :", customerName, bodyBoldPaint, bodyPaint, spacingAfter = 2f)
        drawTwoCols("Cashier    :", cashier, bodyBoldPaint, bodyPaint, spacingAfter = 2f)
        drawDivider(dotted = true, spacingAfter = 8f)

        // 3. Table Header (Qty column removed as requested: #, Item, Amount)
        val headerBaseLine = curY + bodyBoldPaint.textSize
        canvas.drawText("#", colIdxX, headerBaseLine, bodyBoldPaint)
        canvas.drawText("Item", colItemX, headerBaseLine, bodyBoldPaint)
        val amtHeaderW = bodyBoldPaint.measureText("Amount")
        canvas.drawText("Amount", colAmtRight - amtHeaderW, headerBaseLine, bodyBoldPaint)
        curY += bodyBoldPaint.textSize + 4f
        drawDivider(dotted = false, spacingAfter = 6f)

        // 4. Items (Qty column removed, qty * price added after item_name)
        if (itemRenderList.isNotEmpty()) {
            for (item in itemRenderList) {
                if (item.isSingleLine) {
                    val rowBaseline = curY + bodyPaint.textSize
                    canvas.drawText(item.idx, colIdxX, rowBaseline, bodyPaint)
                    canvas.drawText(item.fullDesc, colItemX, rowBaseline, bodyPaint)
                    val aW = bodyPaint.measureText(item.amount)
                    canvas.drawText(item.amount, colAmtRight - aW, rowBaseline, bodyPaint)
                    curY += item.rowHeight
                } else {
                    // Line 1: Index + full Item Name
                    val firstLineBaseline = curY + bodyPaint.textSize
                    canvas.drawText("${item.idx}.", colIdxX, firstLineBaseline, bodyPaint)
                    canvas.save()
                    canvas.translate(colItemX, curY)
                    item.nameLayout.draw(canvas)
                    canvas.restore()
                    curY += item.nameLayout.height + 1f

                    // Line 2: (qty * price) on left, Amount on right
                    val secondLineBaseline = curY + bodyPaint.textSize
                    canvas.drawText(item.qtyPriceStr.trim(), colItemX, secondLineBaseline, bodyPaint)
                    val aW = bodyPaint.measureText(item.amount)
                    canvas.drawText(item.amount, colAmtRight - aW, secondLineBaseline, bodyPaint)
                    curY += bodyPaint.textSize + 3f
                }
            }
        } else {
            drawCenterText("No items", centerBodyPaint, 4f)
        }
        drawDivider(dotted = true, spacingAfter = 8f)

        // 5. Summary Totals (No extra gap between Total and Paid; Net Total removed)
        val finalPayable = if (discount > 0) netAmount else totalAmount
        if (discount > 0) {
            drawTwoCols("Subtotal:", "${formatMoney(totalAmount)} MMK", bodyPaint, bodyPaint, spacingAfter = 1f)
            drawTwoCols("Discount:", "-${formatMoney(discount)} MMK", bodyPaint, bodyPaint, spacingAfter = 1f)
        }
        drawTwoCols("Total:", "${formatMoney(finalPayable)} MMK", bodyBoldPaint, bodyBoldPaint, spacingAfter = 1f)
        drawTwoCols("Paid:", "${formatMoney(paid)} MMK", bodyPaint, bodyPaint, spacingAfter = 1f)
        drawTwoCols("Change:", "${formatMoney(change)} MMK", bodyPaint, bodyPaint, spacingAfter = 1f)
        drawDivider(dotted = true, spacingAfter = 8f)

        // 6. Footer (Strictly Center Aligned)
        if (cleanGreeting.isNotEmpty()) {
            drawCenterText(cleanGreeting, centerBodyBoldPaint, spacingAfter = 2f)
        }
        if (cleanNote.isNotEmpty()) {
            drawCenterText(cleanNote, centerBodyPaint, spacingAfter = 2f)
        }

        // Convert Bitmap to ESC/POS Raster Bytes
        return convertBitmapToEscPos(bitmap)
    }

    /**
     * Converts a monochrome Bitmap into ESC/POS GS v 0 raster bit image bytes.
     * Slices into 256-line chunks to prevent printer buffer overflows on portable/LAN printers.
     */
    fun convertBitmapToEscPos(bitmap: Bitmap): ByteArray {
        val width = bitmap.width
        val height = bitmap.height
        val widthBytes = (width + 7) / 8

        val output = ByteArrayOutputStream()

        // ESC @ (initialize printer)
        output.write(byteArrayOf(0x1B, 0x40))
        // ESC 3 0 (set line spacing to 0 for seamless slice concatenation)
        output.write(byteArrayOf(0x1B, 0x33, 0x00))

        val sliceHeight = 256
        var y = 0

        while (y < height) {
            val currentSliceHeight = minOf(sliceHeight, height - y)

            // ESC/POS command: GS v 0 m xL xH yL yH
            val xL = (widthBytes % 256).toByte()
            val xH = (widthBytes / 256).toByte()
            val yL = (currentSliceHeight % 256).toByte()
            val yH = (currentSliceHeight / 256).toByte()

            output.write(byteArrayOf(0x1D, 0x76, 0x30, 0x00, xL, xH, yL, yH))

            for (row in 0 until currentSliceHeight) {
                val actualY = y + row
                for (colByte in 0 until widthBytes) {
                    var byteVal = 0
                    for (bit in 0 until 8) {
                        val px = colByte * 8 + bit
                        if (px < width) {
                            val pixel = bitmap.getPixel(px, actualY)
                            val alpha = (pixel shr 24) and 0xFF
                            if (alpha > 50) {
                                val r = (pixel shr 16) and 0xFF
                                val g = (pixel shr 8) and 0xFF
                                val b = pixel and 0xFF
                                val lum = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                                if (lum < 200) { // Dark pixel -> thermal black dot
                                    byteVal = byteVal or (1 shl (7 - bit))
                                }
                            }
                        }
                    }
                    output.write(byteVal)
                }
            }
            y += currentSliceHeight
        }

        // Reset line spacing: ESC 2
        output.write(byteArrayOf(0x1B, 0x32))
        // Feed lines: ESC d 4
        output.write(byteArrayOf(0x1B, 0x64, 0x04))
        // Cut paper: GS V 66 0
        output.write(byteArrayOf(0x1D, 0x56, 0x42, 0x00))

        return output.toByteArray()
    }

    /**
     * Generates ESC/POS byte commands for thermal receipt in raw text mode.
     */
    private fun generateEscPosReceipt(jsonStr: String, paperWidth: String): ByteArray {
        val maxChars = getPaperColumns(paperWidth)
        val stream = ByteArrayOutputStream()

        fun write(bytes: ByteArray) = stream.write(bytes)
        fun writeText(text: String) = stream.write(text.toByteArray(Charsets.UTF_8))

        // ESC/POS Commands
        val ESC: Byte = 0x1B
        val GS: Byte = 0x1D

        // Initialize printer
        write(byteArrayOf(ESC, 0x40))

        // Alignment helpers
        fun alignCenter() = write(byteArrayOf(ESC, 0x61, 0x01))
        fun alignLeft() = write(byteArrayOf(ESC, 0x61, 0x00))
        fun alignRight() = write(byteArrayOf(ESC, 0x61, 0x02))
        fun boldOn() = write(byteArrayOf(ESC, 0x45, 0x01))
        fun boldOff() = write(byteArrayOf(ESC, 0x45, 0x00))
        fun printLine(text: String = "") = writeText("$text\n")
        fun printDivider() = printLine("-".repeat(maxChars))

        fun printCentered(text: String) {
            val clean = text.replace("!", "").trim()
            if (clean.isEmpty()) return
            val pad = ((maxChars - clean.length) / 2).coerceAtLeast(0)
            printLine(" ".repeat(pad) + clean)
        }

        try {
            val json = JSONObject(jsonStr)

            // Clean Header values (no '!' marks)
            val cleanShopName = _shopName.value.replace("!", "").trim().ifEmpty { "N-POS" }
            val cleanTagline = _shopTagline.value.replace("!", "").trim()
            val cleanAddress = _shopAddress.value.replace("!", "").trim()
            val cleanPhone = _shopPhone.value.replace("!", "").trim()
            val cleanGreeting = _footerGreeting.value.replace("!", "").trim()
            val cleanNote = _footerNote.value.replace("!", "").trim()

            // Parse fields
            val voucherNo = json.optString("voucher_no", "---")
            val rawDate = json.optString("sale_date", json.optString("print_date", ""))
            val saleDate = if (rawDate.contains("-") && rawDate.indexOf("-") == 4) {
                try {
                    val inFmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
                    val outFmt = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                    inFmt.parse(rawDate)?.let { outFmt.format(it) } ?: rawDate
                } catch (_: Exception) { rawDate }
            } else {
                rawDate
            }
            val customerObj = json.optJSONObject("customer")
            val customerName = customerObj?.optString("name", "Default Customer") ?: "Default Customer"
            val userObj = json.optJSONObject("user")
            val cashier = userObj?.optString("displayName", "Cashier-1") ?: "Cashier-1"

            val totalAmount = json.optDouble("total_amount", 0.0)
            val discount = json.optDouble("extra_discount", 0.0)
            val netAmount = json.optDouble("net_amount", totalAmount - discount)
            val paid = json.optDouble("paid", 0.0)
            val change = json.optDouble("change", 0.0)

            val saleItems = json.optJSONArray("saleItems") ?: json.optJSONArray("products")

            // --- HEADER (Strictly Centered, tight spacing, no blank lines) ---
            alignLeft()
            boldOn()
            printCentered(cleanShopName)
            boldOff()
            if (cleanTagline.isNotEmpty()) {
                printCentered(cleanTagline)
            }
            if (cleanAddress.isNotEmpty()) {
                printCentered(cleanAddress)
            }
            if (cleanPhone.isNotEmpty()) {
                printCentered("Ph: $cleanPhone")
            }
            printDivider()

            // --- METADATA ---
            alignLeft()
            boldOn()
            printLine("Invoice No : $voucherNo")
            printLine("Date       : $saleDate")
            printLine("Customer   : $customerName")
            printLine("Cashier    : $cashier")
            boldOff()
            printDivider()

            // --- ITEMS TABLE HEADER (Qty column removed) ---
            boldOn()
            printLine(formatItemRowHeader(maxChars))
            boldOff()
            printDivider()

            // --- ITEMS (Qty column removed, qty * price added after item_name) ---
            val idxWidth = if (maxChars >= 64) 4 else 3
            val amtWidth = if (maxChars >= 64) 18 else if (maxChars >= 48) 14 else if (maxChars >= 40) 12 else 10
            val itemWidth = (maxChars - idxWidth - amtWidth - 2).coerceAtLeast(10)

            if (saleItems != null && saleItems.length() > 0) {
                for (i in 0 until saleItems.length()) {
                    val item = saleItems.getJSONObject(i)
                    val idx = (i + 1).toString()
                    val itemName = item.optString("item_name", "Item").replace("!", "").trim()
                    val qty = item.optInt("sale_qty", 1).toString()
                    val unitPrice = item.optDouble("qty_sale_price", item.optDouble("unit_price", 0.0))
                    val amount = formatMoney(item.optDouble("final_amount", 0.0))

                    val qtyPriceStr = if (unitPrice > 0) " (${qty} x ${formatMoney(unitPrice)})" else if ((item.optInt("sale_qty", 1)) > 1) " (${qty})" else ""
                    val fullDesc = "$itemName$qtyPriceStr"

                    if (fullDesc.length <= itemWidth) {
                        // Fits on single line
                        val padIdx = idx.padEnd(idxWidth)
                        val padItem = fullDesc.padEnd(itemWidth)
                        val padAmt = amount.padStart(amtWidth)
                        printLine("$padIdx $padItem $padAmt")
                    } else {
                        // Does not fit on one line -> drop down to next line (နောက်တစ်လိုင်း ဆင်းပေးပါ)
                        printLine("$idx. $itemName")
                        val priceLine = if (qtyPriceStr.isNotEmpty()) "   ${qtyPriceStr.trim()}" else "   Qty: $qty"
                        printLine(formatTwoColumns(priceLine, "$amount MMK", maxChars))
                    }
                }
            } else {
                alignCenter()
                printLine("No items")
                alignLeft()
            }

            printDivider()

            // --- SUMMARY TOTALS (No extra gap between Total and Paid; Net Total removed) ---
            if (discount > 0) {
                printLine(formatTwoColumns("Subtotal:", "${formatMoney(totalAmount)} MMK", maxChars))
                printLine(formatTwoColumns("Discount:", "-${formatMoney(discount)} MMK", maxChars))
            }
            val finalPayable = if (discount > 0) netAmount else totalAmount
            boldOn()
            printLine(formatTwoColumns("Total:", "${formatMoney(finalPayable)} MMK", maxChars))
            boldOff()
            printLine(formatTwoColumns("Paid:", "${formatMoney(paid)} MMK", maxChars))
            printLine(formatTwoColumns("Change:", "${formatMoney(change)} MMK", maxChars))

            printDivider()

            // --- FOOTER (Strictly Centered, tight spacing) ---
            alignLeft()
            boldOn()
            if (cleanGreeting.isNotEmpty()) {
                printCentered(cleanGreeting)
            }
            boldOff()
            if (cleanNote.isNotEmpty()) {
                printCentered(cleanNote)
            }

            // Feed and Paper cut
            printLine()
            printLine()
            printLine()
            // ESC/POS Cut Paper: GS V 66 0 (cut with feed)
            write(byteArrayOf(GS, 0x56, 0x42, 0x00))

        } catch (e: Exception) {
            Log.e(TAG, "Error building ESC/POS receipt", e)
            alignLeft()
            printLine("Error generating receipt: ${e.message}")
            printLine()
            printLine()
            write(byteArrayOf(GS, 0x56, 0x42, 0x00))
        }

        return stream.toByteArray()
    }

    private fun formatItemRowHeader(totalCols: Int): String {
        val idxWidth = if (totalCols >= 64) 4 else 3
        val amtWidth = if (totalCols >= 64) 18 else if (totalCols >= 48) 14 else if (totalCols >= 40) 12 else 10
        val itemWidth = (totalCols - idxWidth - amtWidth - 2).coerceAtLeast(8)
        return "#".padEnd(idxWidth) + " " + "Item".padEnd(itemWidth) + " " + "Amount".padStart(amtWidth)
    }

    private fun formatTwoColumns(left: String, right: String, totalWidth: Int): String {
        val available = totalWidth - left.length - right.length
        return if (available >= 0) {
            left + " ".repeat(available) + right
        } else {
            // Truncate left if needed
            val maxLeft = totalWidth - right.length - 1
            if (maxLeft > 0) {
                left.take(maxLeft) + " " + right
            } else {
                left + "\n" + right
            }
        }
    }

    private fun formatMoney(amount: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val formatter = DecimalFormat("#,##0.00", symbols)
        return formatter.format(amount)
    }
}
