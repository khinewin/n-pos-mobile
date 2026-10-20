package com.npos.app.pos

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import com.npos.app.R
import java.io.ByteArrayOutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class BarcodeLabelSize(
    val id: String,
    val titleEn: String,
    val titleMm: String,
    val widthMm: Int,
    val heightMm: Int,
    val widthDots: Int,
    val heightDots: Int
)

enum class BarcodePrintProtocol(val titleEn: String, val titleMm: String, val descEn: String, val descMm: String) {
    ESC_POS_GRAPHIC(
        "ESC/POS Graphic (Myanmar Font)",
        "ESC/POS ပုံနှိပ်မုဒ် (မြန်မာစာအမှန်)",
        "Prints high-clarity bitmap on any thermal printer",
        "မည်သည့် Thermal Printer တွင်မဆို မြန်မာစာနှင့် ဘားကုဒ် အကြည်လင်ဆုံးထုတ်ပေးသည်"
    ),
    TSPL_LABEL(
        "TSPL Sticker Printer",
        "TSPL စတစ်ကာ ပရင်တာမုဒ်",
        "For dedicated barcode label sticker printers (GAP sensor)",
        "ဘားကုဒ် စတစ်ကာသီးသန့် ပရင်တာများအတွက် (Xprinter / Gprinter Label)"
    )
}

data class BarcodeLabelConfig(
    val includeItemName: Boolean = true,
    val includeRetailPrice: Boolean = true,
    val includeCodeNo: Boolean = true,
    val includeShopName: Boolean = false,
    val includeUnit: Boolean = false,
    val includeBarcodeGraphic: Boolean = true,
    val showFieldLabels: Boolean = false,
    val currencySuffix: String = "Ks",
    val labelSizeId: String = "40x30",
    val protocol: BarcodePrintProtocol = BarcodePrintProtocol.ESC_POS_GRAPHIC,
    val fontSizeScale: Float = 1.0f,
    val barcodeHeightScale: Float = 1.0f
)

data class BarcodeLabelData(
    val itemName: String,
    val codeNo: String,
    val barcodeValue: String,
    val retailPrice: Double,
    val unit: String = "ခု",
    val shopName: String = "N-POS",
    val categoryName: String = ""
)

object BarcodeLabelGenerator {

    private const val PREFS_BARCODE = "npos_barcode_label_prefs"

    val LABEL_SIZES = listOf(
        BarcodeLabelSize("40x30", "40 × 30 mm (Standard)", "40 × 30 mm (ပုံမှန် စတစ်ကာ)", 40, 30, 320, 240),
        BarcodeLabelSize("50x30", "50 × 30 mm (Wide)", "50 × 30 mm (အကျယ် စတစ်ကာ)", 50, 30, 384, 240),
        BarcodeLabelSize("40x25", "40 × 25 mm (Compact)", "40 × 25 mm (အသေး စတစ်ကာ)", 40, 25, 320, 200),
        BarcodeLabelSize("30x20", "30 × 20 mm (Mini Tag)", "30 × 20 mm (မီနီ တံဆိပ်)", 30, 20, 240, 160),
        BarcodeLabelSize("58x40", "58 × 40 mm (58mm Roll)", "58 × 40 mm (58mm စက္ကူလိပ်)", 58, 40, 384, 320)
    )

    fun getSizeById(id: String): BarcodeLabelSize {
        return LABEL_SIZES.find { it.id == id } ?: LABEL_SIZES.first()
    }

    fun loadConfig(context: Context): BarcodeLabelConfig {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_BARCODE, Context.MODE_PRIVATE)
        val protocolName = prefs.getString("protocol", BarcodePrintProtocol.ESC_POS_GRAPHIC.name)
            ?: BarcodePrintProtocol.ESC_POS_GRAPHIC.name
        val protocol = try {
            BarcodePrintProtocol.valueOf(protocolName)
        } catch (_: Exception) {
            BarcodePrintProtocol.ESC_POS_GRAPHIC
        }
        return BarcodeLabelConfig(
            includeItemName = prefs.getBoolean("include_item_name", true),
            includeRetailPrice = prefs.getBoolean("include_retail_price", true),
            includeCodeNo = prefs.getBoolean("include_code_no", true),
            includeShopName = prefs.getBoolean("include_shop_name", false),
            includeUnit = prefs.getBoolean("include_unit", false),
            includeBarcodeGraphic = prefs.getBoolean("include_barcode_graphic", true),
            showFieldLabels = prefs.getBoolean("show_field_labels", false),
            currencySuffix = prefs.getString("currency_suffix", "Ks") ?: "Ks",
            labelSizeId = prefs.getString("label_size_id", "40x30") ?: "40x30",
            protocol = protocol,
            fontSizeScale = prefs.getFloat("font_size_scale", 1.0f).coerceIn(0.75f, 1.35f),
            barcodeHeightScale = prefs.getFloat("barcode_height_scale", 1.0f).coerceIn(0.7f, 1.4f)
        )
    }

    fun saveConfig(context: Context, config: BarcodeLabelConfig) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_BARCODE, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("include_item_name", config.includeItemName)
            .putBoolean("include_retail_price", config.includeRetailPrice)
            .putBoolean("include_code_no", config.includeCodeNo)
            .putBoolean("include_shop_name", config.includeShopName)
            .putBoolean("include_unit", config.includeUnit)
            .putBoolean("include_barcode_graphic", config.includeBarcodeGraphic)
            .putBoolean("show_field_labels", config.showFieldLabels)
            .putString("currency_suffix", config.currencySuffix)
            .putString("label_size_id", config.labelSizeId)
            .putString("protocol", config.protocol.name)
            .putFloat("font_size_scale", config.fontSizeScale)
            .putFloat("barcode_height_scale", config.barcodeHeightScale)
            .apply()
    }

    fun formatPrice(price: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val fmt = if (price % 1.0 == 0.0) {
            DecimalFormat("#,##0", symbols)
        } else {
            DecimalFormat("#,##0.00", symbols)
        }
        return fmt.format(price)
    }

    /**
     * Renders a crisp monochrome-ready Bitmap of the barcode label based on [data] and [config].
     * Uses the bundled Myanmar Unicode font (Padauk) so Burmese product names render clearly.
     */
    fun renderLabelBitmap(
        context: Context,
        data: BarcodeLabelData,
        config: BarcodeLabelConfig
    ): Bitmap {
        val size = getSizeById(config.labelSizeId)
        val width = (size.widthDots / 8) * 8 // Ensure multiple of 8 for ESC/POS & TSPL
        val height = size.heightDots

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val tf = try {
            ResourcesCompat.getFont(context, R.font.padauk) ?: Typeface.DEFAULT
        } catch (_: Exception) {
            Typeface.DEFAULT
        }
        val tfBold = Typeface.create(tf, Typeface.BOLD)
        val tfMonoBold = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)

        val scale = config.fontSizeScale * (width / 320f).coerceIn(0.8f, 1.25f)
        val marginH = 10
        val contentWidth = (width - marginH * 2).coerceAtLeast(100)

        val shopPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            typeface = tfBold
            textSize = (15f * scale).coerceIn(11f, 22f)
        }

        val itemPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            typeface = tfBold
            textSize = (19f * scale).coerceIn(13f, 28f)
        }

        val codePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            typeface = tfMonoBold
            textSize = (15f * scale).coerceIn(11f, 22f)
        }

        val pricePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            typeface = tfBold
            textSize = (21f * scale).coerceIn(14f, 30f)
        }

        fun makeCenteredLayout(text: String, paint: TextPaint, maxLines: Int = 2): StaticLayout {
            return StaticLayout.Builder.obtain(text, 0, text.length, paint, contentWidth)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0f, 0.95f)
                .setIncludePad(false)
                .setMaxLines(maxLines)
                .build()
        }

        val shopLayout = if (config.includeShopName && data.shopName.isNotBlank()) {
            makeCenteredLayout(data.shopName.trim(), shopPaint, maxLines = 1)
        } else null

        val itemText = if (config.includeItemName && data.itemName.isNotBlank()) {
            if (config.showFieldLabels) "Item: ${data.itemName.trim()}" else data.itemName.trim()
        } else ""
        val itemLayout = if (itemText.isNotEmpty()) {
            makeCenteredLayout(itemText, itemPaint, maxLines = 2)
        } else null

        val rawCode = data.codeNo.ifBlank { data.barcodeValue }.trim().ifBlank { "000000" }
        val codeDisplayText = if (config.includeCodeNo) {
            if (config.showFieldLabels) "Code: $rawCode" else rawCode
        } else ""
        val codeLayout = if (codeDisplayText.isNotEmpty()) {
            makeCenteredLayout(codeDisplayText, codePaint, maxLines = 1)
        } else null

        val priceText = if (config.includeRetailPrice) {
            val formattedPrice = formatPrice(data.retailPrice)
            val suffix = config.currencySuffix.trim()
            val priceWithCurrency = if (suffix.isNotEmpty()) "$formattedPrice $suffix" else formattedPrice
            val withUnit = if (config.includeUnit && data.unit.isNotBlank()) {
                "$priceWithCurrency / ${data.unit.trim()}"
            } else {
                priceWithCurrency
            }
            if (config.showFieldLabels) "Price: $withUnit" else withUnit
        } else ""
        val priceLayout = if (priceText.isNotEmpty()) {
            makeCenteredLayout(priceText, pricePaint, maxLines = 1)
        } else null

        // Calculate total text height so we can size the barcode bars harmoniously
        var totalTextHeight = 0
        var activeBlocks = 0
        shopLayout?.let { totalTextHeight += it.height; activeBlocks++ }
        itemLayout?.let { totalTextHeight += it.height; activeBlocks++ }
        codeLayout?.let { totalTextHeight += it.height; activeBlocks++ }
        priceLayout?.let { totalTextHeight += it.height; activeBlocks++ }

        val verticalPadding = 8
        val availableForBarcode = (height - (verticalPadding * 2) - totalTextHeight - (activeBlocks * 4)).coerceAtLeast(36)
        val desiredBarcodeHeight = if (config.includeBarcodeGraphic) {
            val baseH = (height * 0.34f * config.barcodeHeightScale).toInt()
            baseH.coerceIn(36, availableForBarcode.coerceAtLeast(36))
        } else 0

        if (config.includeBarcodeGraphic) {
            activeBlocks++
        }

        val totalContentHeight = totalTextHeight + desiredBarcodeHeight + ((activeBlocks - 1).coerceAtLeast(0) * 4)
        var currentY = ((height - totalContentHeight) / 2f).coerceAtLeast(verticalPadding.toFloat())

        // 1. Draw Shop Name (if enabled)
        shopLayout?.let { layout ->
            canvas.save()
            canvas.translate(marginH.toFloat(), currentY)
            layout.draw(canvas)
            canvas.restore()
            currentY += layout.height + 3f
        }

        // 2. Draw Item Name (if enabled)
        itemLayout?.let { layout ->
            canvas.save()
            canvas.translate(marginH.toFloat(), currentY)
            layout.draw(canvas)
            canvas.restore()
            currentY += layout.height + 4f
        }

        // 3. Draw Code 128 Barcode Bars (if enabled)
        if (config.includeBarcodeGraphic && desiredBarcodeHeight > 0) {
            val barcodeStr = data.barcodeValue.ifBlank { data.codeNo }.trim().ifBlank { "123456" }
            val pattern = encodeCode128B(barcodeStr)
            val totalModules = pattern.length
            if (totalModules > 0) {
                val maxBarcodeWidth = contentWidth - 8
                val moduleWidth = (maxBarcodeWidth / totalModules).coerceAtLeast(1)
                val actualBarcodeWidth = totalModules * moduleWidth
                val startX = ((width - actualBarcodeWidth) / 2f).coerceAtLeast(marginH.toFloat())

                val barPaint = Paint().apply {
                    color = Color.BLACK
                    style = Paint.Style.FILL
                    isAntiAlias = false
                }

                var x = startX
                for (i in pattern.indices) {
                    if (pattern[i] == '1') {
                        canvas.drawRect(
                            x,
                            currentY,
                            x + moduleWidth,
                            currentY + desiredBarcodeHeight,
                            barPaint
                        )
                    }
                    x += moduleWidth
                }
            }
            currentY += desiredBarcodeHeight + 3f
        }

        // 4. Draw Code No (if enabled)
        codeLayout?.let { layout ->
            canvas.save()
            canvas.translate(marginH.toFloat(), currentY)
            layout.draw(canvas)
            canvas.restore()
            currentY += layout.height + 3f
        }

        // 5. Draw Retail Price (if enabled)
        priceLayout?.let { layout ->
            canvas.save()
            canvas.translate(marginH.toFloat(), currentY)
            layout.draw(canvas)
            canvas.restore()
        }

        return bitmap
    }

    /**
     * Generates TSPL command bytes for dedicated barcode sticker printers (e.g. Xprinter XP-365B, Gprinter).
     * Uses TSPL BITMAP command so Myanmar Unicode product names and Code 128 barcode print accurately.
     */
    fun generateTsplBytes(bitmap: Bitmap, size: BarcodeLabelSize, copies: Int = 1): ByteArray {
        val safeCopies = copies.coerceIn(1, 200)
        val width = bitmap.width
        val height = bitmap.height
        val widthBytes = (width + 7) / 8

        val out = ByteArrayOutputStream()
        fun writeAscii(cmd: String) {
            out.write(cmd.toByteArray(Charsets.US_ASCII))
        }

        writeAscii("SIZE ${size.widthMm} mm, ${size.heightMm} mm\r\n")
        writeAscii("GAP 2 mm, 0 mm\r\n")
        writeAscii("DIRECTION 1\r\n")
        writeAscii("CLS\r\n")

        // TSPL BITMAP x,y,width_bytes,height,mode,data
        // In TSPL BITMAP mode 0: 0 bit = black (printed), 1 bit = white (unprinted)
        writeAscii("BITMAP 0,0,$widthBytes,$height,0,")

        for (y in 0 until height) {
            for (colByte in 0 until widthBytes) {
                var byteVal = 0xFF // all white (1s) by default in TSPL
                for (bit in 0 until 8) {
                    val px = colByte * 8 + bit
                    if (px < width) {
                        val pixel = bitmap.getPixel(px, y)
                        val r = (pixel shr 16) and 0xFF
                        val g = (pixel shr 8) and 0xFF
                        val b = pixel and 0xFF
                        val lum = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                        if (lum < 190) {
                            // Set bit to 0 for black dot in TSPL BITMAP
                            byteVal = byteVal and (1 shl (7 - bit)).inv()
                        }
                    }
                }
                out.write(byteVal)
            }
        }

        writeAscii("\r\nPRINT $safeCopies,1\r\n")
        return out.toByteArray()
    }

    /**
     * Standard Code 128 Subset B encoder returning a binary module string ('1' = bar, '0' = space).
     * Non-ASCII characters are sanitized to ASCII digits/letters so every product produces a valid scannable barcode.
     */
    fun encodeCode128B(rawInput: String): String {
        val sanitized = rawInput.map { ch ->
            if (ch.code in 32..126) ch else '0'
        }.joinToString("").take(28).ifEmpty { "123456" }

        val patterns = CODE128_PATTERNS
        val startCodeB = 104
        val stopCode = 106

        val sb = StringBuilder()
        sb.append(patterns[startCodeB])

        var checksum = startCodeB
        for (i in sanitized.indices) {
            val ch = sanitized[i]
            val codeVal = (ch.code - 32).coerceIn(0, 94)
            sb.append(patterns[codeVal])
            checksum += codeVal * (i + 1)
        }

        val checkDigit = checksum % 103
        sb.append(patterns[checkDigit])
        sb.append(patterns[stopCode])
        return sb.toString()
    }

    private val CODE128_PATTERNS = arrayOf(
        "11011001100", "11001101100", "11001100110", "10010011000", "10010001100",
        "10001001100", "10011001000", "10011000100", "10001100100", "11001001000",
        "11001000100", "11000100100", "10110011100", "10011011100", "10011001110",
        "10111001100", "10011101100", "10011100110", "11001110010", "11001011100",
        "11001001110", "11011100100", "11001110100", "11101101110", "11101001100",
        "11100101100", "11100100110", "11101100100", "11100110100", "11100110010",
        "11011011000", "11011000110", "11000110110", "10100011000", "10001011000",
        "10001000110", "10110001000", "10001101000", "10001100010", "11010001000",
        "11000101000", "11000100010", "10110111000", "10110001110", "10001101110",
        "10111011000", "10111000110", "10001110110", "11101110110", "11010001110",
        "11000101110", "11011101000", "11011100010", "11011101110", "11101011000",
        "11101000110", "11100010110", "11101101000", "11101100010", "11100011010",
        "11101111010", "11001000010", "11110001010", "10100110000", "10100001100",
        "10010110000", "10010000110", "10000101100", "10000100110", "10110010000",
        "10110000100", "10011010000", "10011000010", "10000110100", "10000110010",
        "11000010010", "11001010000", "11110111010", "11000010100", "10001111010",
        "10100111100", "10010111100", "10010011110", "10111100100", "10011110100",
        "10011110010", "11110100100", "11110010100", "11110010010", "11011011110",
        "11011110110", "11110110110", "10101111000", "10100011110", "10001011110",
        "10111101000", "10111100010", "11110101000", "11110100010", "10111011110",
        "10111101110", "11101011110", "11110101110", "11010000100", "11010010000",
        "11010011100", "1100011101011"
    )
}
