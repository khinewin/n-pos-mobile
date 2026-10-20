package com.npos.app.data.model

import org.json.JSONObject

data class UserSession(
    val uid: String,
    val email: String,
    val displayName: String,
    val idToken: String,
    val refreshToken: String = ""
)

data class Product(
    val id: String,
    val codeNo: String,
    val itemName: String,
    val qty: Int,
    val retailPrice: Double,
    val categoryName: String,
    val barcode: String,
    val unit: String,
    val rawFields: JSONObject? = null,
    val wholesalePrice: Double = parseWholesalePriceFromFields(rawFields, retailPrice)
)

fun parseWholesalePriceFromFields(fields: JSONObject?, fallbackRetail: Double = 0.0): Double {
    if (fields == null) return fallbackRetail
    fun readFirestoreNumber(obj: JSONObject?): Double? {
        if (obj == null) return null
        obj.optString("integerValue").takeIf { it.isNotBlank() }?.toDoubleOrNull()?.let { if (it > 0) return it }
        obj.optString("doubleValue").takeIf { it.isNotBlank() }?.toDoubleOrNull()?.let { if (it > 0) return it }
        obj.optString("stringValue").takeIf { it.isNotBlank() }?.toDoubleOrNull()?.let { if (it > 0) return it }
        val d = obj.optDouble("doubleValue", -1.0)
        if (d > 0) return d
        return null
    }

    val keys = listOf("whole_sale_price", "wholesale_price", "wholeSalePrice", "qty_whole_sale_price")
    for (key in keys) {
        val fieldObj = fields.optJSONObject(key) ?: continue
        // Check if it's a Firestore mapValue (e.g., smallest_unit / medium_unit like retail_price)
        val mapFields = fieldObj.optJSONObject("mapValue")?.optJSONObject("fields")
        if (mapFields != null) {
            readFirestoreNumber(mapFields.optJSONObject("smallest_unit"))?.let { return it }
            readFirestoreNumber(mapFields.optJSONObject("medium_unit"))?.let { return it }
            readFirestoreNumber(mapFields.optJSONObject("largest_unit"))?.let { return it }
        }
        // Or direct numeric/string field
        readFirestoreNumber(fieldObj)?.let { return it }
    }
    return fallbackRetail
}

data class Customer(
    val id: String,
    val name: String,
    val phone: String,
    val code: String,
    val address: String = ""
) {
    companion object {
        val DEFAULT = Customer(
            id = "123456789",
            name = "Default Customer",
            phone = "09xxxxxxxxx",
            code = "123456789",
            address = ""
        )
    }
}

data class CartItem(
    val product: Product,
    val qty: Int = 1,
    val unitPrice: Double = product.retailPrice,
    val discount: Double = 0.0,
    val finalAmount: Double = (unitPrice * qty) - discount
)

data class SaleRecord(
    val voucherNo: String,
    val saleDate: String,
    val totalAmount: Double,
    val extraDiscount: Double,
    val netAmount: Double,
    val paid: Double,
    val change: Double,
    val credit: Double,
    val paymentType: String,
    val saleType: String = "retail",
    val remark: String = "",
    val customer: Customer,
    val user: UserSession,
    val items: List<CartItem>
)

data class UserSalesSummary(
    val todayAmount: Double = 0.0,
    val thisMonthAmount: Double = 0.0,
    val todayCount: Int = 0,
    val thisMonthCount: Int = 0
)

enum class DeviceViewMode(val titleEn: String, val titleMm: String, val description: String) {
    AUTO("Auto Detect", "အလိုအလျောက်", "Adjusts automatically based on screen size"),
    PHONE("Phone Mode", "မိုဘိုင်းလ်ဖုန်း မုဒ်", "Handheld mobile layout with tabbed products & cart"),
    TABLET("Tablet / POS Mode", "တက်ဘလက် / POS မုဒ်", "Dual-pane desktop POS layout with products & cart side-by-side")
}
