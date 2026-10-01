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
    }

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

            val escPosBytes = generateEscPosReceipt(jsonData, _paperWidth.value == "80mm")
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
            put("sale_date", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(java.util.Date()))
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
    private fun generateEscPosReceipt(jsonStr: String, is80mm: Boolean): ByteArray {
        val maxChars = if (is80mm) 48 else 32
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
            val saleDate = json.optString("sale_date", json.optString("print_date", ""))
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
            printLine("N-POS")
            normalSize()
            printLine("POS Software for Minimart")
            boldOff()
            printLine("Mawlamyine, Mon State")
            printLine("Ph: 09-970488345")
            printDivider()

            // --- METADATA ---
            alignLeft()
            boldOn()
            printLine(formatTwoColumns("Invoice No:", voucherNo, maxChars))
            printLine(formatTwoColumns("Date:", saleDate, maxChars))
            printLine(formatTwoColumns("Customer:", customerName, maxChars))
            printLine(formatTwoColumns("Cashier:", cashier, maxChars))
            boldOff()
            printDivider()

            // --- ITEMS TABLE HEADER ---
            boldOn()
            if (is80mm) {
                // 48 columns: # (3) + Item (23) + Qty (6) + Amount (16)
                printLine(formatRow80("#", "Item", "Qty", "Amount"))
            } else {
                // 32 columns: # (2) + Item (14) + Qty (4) + Amount (12)
                printLine(formatRow58("#", "Item", "Qty", "Amount"))
            }
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

                    if (is80mm) {
                        printLine(formatRow80(idx, itemName, qty, amount))
                    } else {
                        printLine(formatRow58(idx, itemName, qty, amount))
                    }
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
            printLine(formatTwoColumns("Net Amount:", "${formatMoney(netAmount)} MMK", if (is80mm) 24 else 16))
            normalSize()
            printLine(formatTwoColumns("Paid:", "${formatMoney(paid)} MMK", maxChars))
            printLine(formatTwoColumns("Change:", "${formatMoney(change)} MMK", maxChars))
            boldOff()

            printDivider()

            // --- FOOTER ---
            alignCenter()
            boldOn()
            printLine("Thank you for shopping with us!")
            boldOff()
            printLine("Powered by NTG Technology")
            printLine("PH: 09-970488345")

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

    private fun formatRow58(idx: String, item: String, qty: String, amt: String): String {
        // Total 32 columns:
        // idx: 2 chars + 1 space = 3
        // item: 14 chars + 1 space = 15
        // qty: 4 chars + 1 space = 5
        // amt: 9 chars
        val trimmedItem = if (item.length > 14) item.take(13) + "." else item.padEnd(14)
        val padIdx = idx.padEnd(2)
        val padQty = qty.padStart(4)
        val padAmt = amt.padStart(9)
        return "$padIdx $trimmedItem $padQty $padAmt".take(32)
    }

    private fun formatRow80(idx: String, item: String, qty: String, amt: String): String {
        // Total 48 columns:
        // idx: 3 chars + 1 space = 4
        // item: 23 chars + 1 space = 24
        // qty: 6 chars + 1 space = 7
        // amt: 13 chars
        val trimmedItem = if (item.length > 23) item.take(22) + "." else item.padEnd(23)
        val padIdx = idx.padEnd(3)
        val padQty = qty.padStart(6)
        val padAmt = amt.padStart(13)
        return "$padIdx $trimmedItem $padQty $padAmt".take(48)
    }

    private fun formatMoney(amount: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val formatter = DecimalFormat("#,##0.00", symbols)
        return formatter.format(amount)
    }
}
