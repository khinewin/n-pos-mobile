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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

/**
 * Manages Bluetooth ESC/POS Thermal Printing.
 * Supports silent, direct printing on 58mm and 80mm thermal receipt printers.
 */
class ThermalPrinterManager(private val context: Context) {

    companion object {
        private const val TAG = "ThermalPrinterManager"
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private const val PREFS_NAME = "npos_printer_prefs"
        private const val KEY_PRINTER_MAC = "selected_printer_mac"
        private const val KEY_PRINTER_NAME = "selected_printer_name"
        private const val KEY_PAPER_WIDTH = "paper_width" // "58mm" or "80mm"
        private const val KEY_AUTO_CUT = "auto_cut"
        private const val KEY_SHOP_NAME = "receipt_shop_name"
        private const val KEY_SHOP_TAGLINE = "receipt_shop_tagline"
        private const val KEY_SHOP_ADDRESS = "receipt_shop_address"
        private const val KEY_SHOP_PHONE = "receipt_shop_phone"
        private const val KEY_FOOTER_GREETING = "receipt_footer_greeting"
        private const val KEY_FOOTER_NOTE = "receipt_footer_note"
        private const val KEY_CUSTOM_MM = "paper_custom_mm"
        private const val KEY_CUSTOM_COLUMNS = "paper_custom_columns"

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

    private val _shopName = MutableStateFlow(prefs.getString(KEY_SHOP_NAME, "N-POS") ?: "N-POS")
    val shopName: StateFlow<String> = _shopName.asStateFlow()

    private val _shopTagline = MutableStateFlow(prefs.getString(KEY_SHOP_TAGLINE, "POS Software for Minimart") ?: "POS Software for Minimart")
    val shopTagline: StateFlow<String> = _shopTagline.asStateFlow()

    private val _shopAddress = MutableStateFlow(prefs.getString(KEY_SHOP_ADDRESS, "Mawlamyine, Mon State") ?: "Mawlamyine, Mon State")
    val shopAddress: StateFlow<String> = _shopAddress.asStateFlow()

    private val _shopPhone = MutableStateFlow(prefs.getString(KEY_SHOP_PHONE, "09-970488345") ?: "09-970488345")
    val shopPhone: StateFlow<String> = _shopPhone.asStateFlow()

    private val _footerGreeting = MutableStateFlow(prefs.getString(KEY_FOOTER_GREETING, "Thank you for shopping with us!") ?: "Thank you for shopping with us!")
    val footerGreeting: StateFlow<String> = _footerGreeting.asStateFlow()

    private val _footerNote = MutableStateFlow(prefs.getString(KEY_FOOTER_NOTE, "Please come again") ?: "Please come again")
    val footerNote: StateFlow<String> = _footerNote.asStateFlow()

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

    fun updateReceiptConfig(
        name: String,
        tagline: String,
        address: String,
        phone: String,
        greeting: String,
        note: String
    ) {
        val finalName = name.trim().ifEmpty { "N-POS" }
        val finalTagline = tagline.trim()
        val finalAddress = address.trim()
        val finalPhone = phone.trim()
        val finalGreeting = greeting.trim()
        val finalNote = note.trim()

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

    fun isPrinterConfigured(): Boolean {
        return _selectedDevice.value != null
    }

    @SuppressLint("MissingPermission")
    fun getPrinterDisplayName(): String {
        val dev = _selectedDevice.value ?: return "No Printer Configured"
        return try {
            dev.name ?: dev.address
        } catch (e: SecurityException) {
            dev.address
        }
    }

    /**
     * Executes direct silent print for sales receipt data (JSON string).
     */
    suspend fun printReceipt(jsonData: String): Result<String> = withContext(Dispatchers.IO) {
        val device = _selectedDevice.value
            ?: return@withContext Result.failure(Exception("No Bluetooth printer selected. Please configure in settings."))

        _isPrinting.value = true
        _printerStatus.value = "Connecting to ${getPrinterDisplayName()}..."

        var socket: BluetoothSocket? = null
        try {
            // Cancel discovery before connecting as it slows down connection
            try {
                bluetoothAdapter?.cancelDiscovery()
            } catch (_: Exception) {}

            socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            socket.connect()

            _printerStatus.value = "Printing receipt..."
            val outputStream: OutputStream = socket.outputStream

            val escPosBytes = generateEscPosReceipt(jsonData, _paperWidth.value)
            outputStream.write(escPosBytes)
            outputStream.flush()

            // Small delay to ensure printer buffer completes
            kotlinx.coroutines.delay(200)

            _printerStatus.value = "Print successful"
            Result.success("Receipt printed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Silent print failed", e)
            _printerStatus.value = "Print error: ${e.message}"
            Result.failure(e)
        } finally {
            try {
                socket?.close()
            } catch (_: Exception) {}
            _isPrinting.value = false
        }
    }

    /**
     * Test print function to verify printer connection and alignment.
     */
    suspend fun printTestReceipt(): Result<String> = withContext(Dispatchers.IO) {
        val testJson = JSONObject().apply {
            put("voucher_no", "TEST-" + (1000..9999).random())
            put("sale_date", java.text.SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(java.util.Date()))
            put("total_amount", 12500)
            put("extra_discount", 500)
            put("net_amount", 12000)
            put("paid", 12000)
            put("change", 0)
            put("customer", JSONObject().apply {
                put("name", "Test Customer")
                put("phone", "09-123456789")
            })
            put("user", JSONObject().apply {
                put("displayName", "Cashier 01")
            })
            val items = org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("item_name", "Drinking Water 1L")
                    put("sale_qty", 2)
                    put("qty_sale_price", 1000)
                    put("final_amount", 2000)
                })
                put(JSONObject().apply {
                    put("item_name", "Coffee Mix (Pack)")
                    put("sale_qty", 1)
                    put("qty_sale_price", 10000)
                    put("final_amount", 10000)
                })
            }
            put("saleItems", items)
        }
        printReceipt(testJson.toString())
    }

    /**
     * Generates ESC/POS byte commands for thermal receipt.
     */
    private fun generateEscPosReceipt(jsonStr: String, paperWidth: String): ByteArray {
        val maxChars = getPaperColumns(paperWidth)
        val stream = ByteArrayOutputStream()

        fun write(bytes: ByteArray) = stream.write(bytes)
        fun writeText(text: String) = stream.write(text.toByteArray(charset("ISO-8859-1")))

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
        fun doubleSizeOn() = write(byteArrayOf(GS, 0x21, 0x11))
        fun normalSize() = write(byteArrayOf(GS, 0x21, 0x00))
        fun printLine(text: String = "") = writeText("$text\n")
        fun printDivider() = printLine("-".repeat(maxChars))

        try {
            val json = JSONObject(jsonStr)

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
            val customerName = customerObj?.optString("name", "General") ?: "General"
            val userObj = json.optJSONObject("user")
            val cashier = userObj?.optString("displayName", "Admin") ?: "Admin"

            val totalAmount = json.optDouble("total_amount", 0.0)
            val discount = json.optDouble("extra_discount", 0.0)
            val netAmount = json.optDouble("net_amount", totalAmount - discount)
            val paid = json.optDouble("paid", 0.0)
            val change = json.optDouble("change", 0.0)

            val saleItems = json.optJSONArray("saleItems") ?: json.optJSONArray("products")

            // --- HEADER ---
            alignCenter()
            boldOn()
            doubleSizeOn()
            printLine(_shopName.value)
            normalSize()
            if (_shopTagline.value.isNotEmpty()) {
                printLine(_shopTagline.value)
            }
            boldOff()
            if (_shopAddress.value.isNotEmpty()) {
                printLine(_shopAddress.value)
            }
            if (_shopPhone.value.isNotEmpty()) {
                printLine("Ph: ${_shopPhone.value}")
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

            // --- ITEMS TABLE HEADER ---
            boldOn()
            printLine(formatItemRow("#", "Item", "Qty", "Amount", maxChars))
            boldOff()
            printDivider()

            // --- ITEMS ---
            if (saleItems != null && saleItems.length() > 0) {
                for (i in 0 until saleItems.length()) {
                    val item = saleItems.getJSONObject(i)
                    val idx = (i + 1).toString()
                    val itemName = item.optString("item_name", "Item")
                    val qty = item.optInt("sale_qty", 1).toString()
                    val amount = formatMoney(item.optDouble("final_amount", 0.0))

                    printLine(formatItemRow(idx, itemName, qty, amount, maxChars))
                }
            } else {
                alignCenter()
                printLine("No items")
                alignLeft()
            }

            printDivider()

            // --- SUMMARY TOTALS ---
            boldOn()
            printLine(formatTwoColumns("Total:", "${formatMoney(totalAmount)} MMK", maxChars))
            if (discount > 0) {
                printLine(formatTwoColumns("Discount:", "-${formatMoney(discount)} MMK", maxChars))
            }
            doubleSizeOn()
            val netColWidth = (maxChars / 2).coerceAtLeast(16)
            printLine(formatTwoColumns("Net Amount:", "${formatMoney(netAmount)} MMK", netColWidth))
            normalSize()
            printLine(formatTwoColumns("Paid:", "${formatMoney(paid)} MMK", maxChars))
            printLine(formatTwoColumns("Change:", "${formatMoney(change)} MMK", maxChars))
            boldOff()

            printDivider()

            // --- FOOTER ---
            alignCenter()
            boldOn()
            if (_footerGreeting.value.isNotEmpty()) {
                printLine(_footerGreeting.value)
            }
            boldOff()
            if (_footerNote.value.isNotEmpty()) {
                printLine(_footerNote.value)
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

    private fun formatItemRow(idx: String, item: String, qty: String, amt: String, totalCols: Int): String {
        val idxWidth = if (totalCols >= 64) 4 else 3
        val qtyWidth = if (totalCols >= 64) 7 else if (totalCols >= 48) 6 else if (totalCols >= 40) 5 else 4
        val amtWidth = if (totalCols >= 64) 18 else if (totalCols >= 48) 14 else if (totalCols >= 40) 12 else 10
        val itemWidth = (totalCols - idxWidth - qtyWidth - amtWidth - 3).coerceAtLeast(8)

        val trimmedItem = if (item.length > itemWidth) item.take(itemWidth - 1) + "." else item.padEnd(itemWidth)
        val padIdx = idx.padEnd(idxWidth)
        val padQty = qty.padStart(qtyWidth)
        val padAmt = amt.padStart(amtWidth)
        return "$padIdx $trimmedItem $padQty $padAmt".take(totalCols)
    }

    private fun formatMoney(amount: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val formatter = DecimalFormat("#,##0.00", symbols)
        return formatter.format(amount)
    }
}
