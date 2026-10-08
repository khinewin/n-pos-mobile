package com.npos.app.data.offline

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.npos.app.data.model.CartItem
import com.npos.app.data.model.Customer
import com.npos.app.data.model.Product
import com.npos.app.data.model.SaleRecord
import com.npos.app.data.model.UserSession
import org.json.JSONArray
import org.json.JSONObject

/**
 * SQLite Local Database for Offline Mode persistence, Product Caching, and Offline Sales Queue.
 */
class PosLocalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "PosLocalDatabaseHelper"
        private const val DATABASE_NAME = "npos_local.db"
        private const val DATABASE_VERSION = 2

        // Tables
        const val TABLE_PRODUCTS = "cached_products"
        const val TABLE_CATEGORIES = "cached_categories"
        const val TABLE_CUSTOMERS = "cached_customers"
        const val TABLE_OFFLINE_SALES = "offline_sales"

        // Status Constants
        const val STATUS_PENDING = "PENDING"
        const val STATUS_SYNCED = "SYNCED"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Products cache table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_PRODUCTS (
                id TEXT PRIMARY KEY,
                code_no TEXT,
                item_name TEXT,
                qty INTEGER,
                retail_price REAL,
                category_name TEXT,
                barcode TEXT,
                unit TEXT,
                raw_json TEXT,
                updated_at INTEGER
            )
            """.trimIndent()
        )

        // Categories cache table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_CATEGORIES (
                name TEXT PRIMARY KEY
            )
            """.trimIndent()
        )

        // Customers cache table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_CUSTOMERS (
                id TEXT PRIMARY KEY,
                name TEXT,
                phone TEXT,
                code TEXT,
                address TEXT
            )
            """.trimIndent()
        )

        // Offline Sales Queue table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS $TABLE_OFFLINE_SALES (
                voucher_no TEXT PRIMARY KEY,
                sale_date TEXT,
                total_amount REAL,
                extra_discount REAL,
                net_amount REAL,
                paid REAL,
                change REAL,
                credit REAL,
                payment_type TEXT,
                sale_type TEXT,
                remark TEXT,
                customer_id TEXT,
                customer_name TEXT,
                customer_phone TEXT,
                customer_code TEXT,
                customer_address TEXT,
                user_uid TEXT,
                user_email TEXT,
                user_display_name TEXT,
                user_id_token TEXT,
                items_json TEXT,
                status TEXT,
                created_at INTEGER,
                synced_at INTEGER
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Purge old cached data tables so obsolete, deleted, or old-project items never linger
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CUSTOMERS")
        onCreate(db)
    }

    // ----------------------------------------------------
    // Products Caching & Offline Retrieval
    // ----------------------------------------------------
    fun replaceProductsCache(products: List<Product>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_PRODUCTS, null, null)
            val now = System.currentTimeMillis()
            for (p in products) {
                val cv = ContentValues().apply {
                    put("id", p.id)
                    put("code_no", p.codeNo)
                    put("item_name", p.itemName)
                    put("qty", p.qty)
                    put("retail_price", p.retailPrice)
                    put("category_name", p.categoryName)
                    put("barcode", p.barcode)
                    put("unit", p.unit)
                    put("raw_json", p.rawFields?.toString() ?: "")
                    put("updated_at", now)
                }
                db.insertWithOnConflict(TABLE_PRODUCTS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
            Log.d(TAG, "Successfully replaced cached products with ${products.size} fresh items")
        } catch (e: Exception) {
            Log.e(TAG, "Error replacing products cache", e)
        } finally {
            db.endTransaction()
        }
    }

    fun cacheProducts(products: List<Product>) {
        replaceProductsCache(products)
    }

    fun getAllCachedProducts(): List<Product> {
        val list = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(TABLE_PRODUCTS, null, "qty > 0 AND retail_price > 0", null, null, null, "rowid ASC")
        cursor.use { c ->
            val idIdx = c.getColumnIndexOrThrow("id")
            val codeNoIdx = c.getColumnIndexOrThrow("code_no")
            val nameIdx = c.getColumnIndexOrThrow("item_name")
            val qtyIdx = c.getColumnIndexOrThrow("qty")
            val priceIdx = c.getColumnIndexOrThrow("retail_price")
            val catIdx = c.getColumnIndexOrThrow("category_name")
            val barcodeIdx = c.getColumnIndexOrThrow("barcode")
            val unitIdx = c.getColumnIndexOrThrow("unit")
            val rawJsonIdx = c.getColumnIndexOrThrow("raw_json")

            while (c.moveToNext()) {
                val rawStr = c.getString(rawJsonIdx)
                val rawJson = if (!rawStr.isNullOrEmpty()) {
                    try { JSONObject(rawStr) } catch (_: Exception) { null }
                } else null

                list.add(
                    Product(
                        id = c.getString(idIdx),
                        codeNo = c.getString(codeNoIdx) ?: "",
                        itemName = c.getString(nameIdx) ?: "Unknown Item",
                        qty = c.getInt(qtyIdx),
                        retailPrice = c.getDouble(priceIdx),
                        categoryName = c.getString(catIdx) ?: "General",
                        barcode = c.getString(barcodeIdx) ?: "",
                        unit = c.getString(unitIdx) ?: "Pcs",
                        rawFields = rawJson
                    )
                )
            }
        }
        return list
    }

    fun deductLocalProductStock(productId: String, deductQty: Int) {
        try {
            val db = writableDatabase
            db.execSQL(
                "UPDATE $TABLE_PRODUCTS SET qty = MAX(0, qty - ?) WHERE id = ?",
                arrayOf(deductQty, productId)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error deducting local stock for product $productId", e)
        }
    }

    // ----------------------------------------------------
    // Categories Caching & Offline Retrieval
    // ----------------------------------------------------
    fun replaceCategoriesCache(categories: List<String>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_CATEGORIES, null, null)
            for (cat in categories) {
                if (cat.isNotBlank() && cat != "All") {
                    val cv = ContentValues().apply { put("name", cat) }
                    db.insertWithOnConflict(TABLE_CATEGORIES, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
                }
            }
            db.setTransactionSuccessful()
            Log.d(TAG, "Successfully replaced cached categories with ${categories.size} items")
        } catch (e: Exception) {
            Log.e(TAG, "Error replacing categories cache", e)
        } finally {
            db.endTransaction()
        }
    }

    fun cacheCategories(categories: List<String>) {
        replaceCategoriesCache(categories)
    }

    fun getAllCachedCategories(): List<String> {
        val list = mutableListOf("All")
        val db = readableDatabase
        val cursor = db.query(TABLE_CATEGORIES, arrayOf("name"), null, null, null, null, "rowid ASC")
        cursor.use { c ->
            val nameIdx = c.getColumnIndexOrThrow("name")
            while (c.moveToNext()) {
                list.add(c.getString(nameIdx))
            }
        }
        return list
    }

    // ----------------------------------------------------
    // Customers Caching & Offline Retrieval
    // ----------------------------------------------------
    fun replaceCustomersCache(customers: List<Customer>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_CUSTOMERS, null, null)
            for (cust in customers) {
                val cv = ContentValues().apply {
                    put("id", cust.id)
                    put("name", cust.name)
                    put("phone", cust.phone)
                    put("code", cust.code)
                    put("address", cust.address)
                }
                db.insertWithOnConflict(TABLE_CUSTOMERS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.e(TAG, "Error replacing customers cache", e)
        } finally {
            db.endTransaction()
        }
    }

    fun cacheCustomers(customers: List<Customer>) {
        replaceCustomersCache(customers)
    }

    fun clearAllCachedData() {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_PRODUCTS, null, null)
            db.delete(TABLE_CATEGORIES, null, null)
            db.delete(TABLE_CUSTOMERS, null, null)
            db.setTransactionSuccessful()
            Log.d(TAG, "Cleared all local cached products, categories, and customers")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing cached data", e)
        } finally {
            db.endTransaction()
        }
    }

    fun getAllCachedCustomers(): List<Customer> {
        val list = mutableListOf<Customer>()
        val db = readableDatabase
        val cursor = db.query(TABLE_CUSTOMERS, null, null, null, null, null, "name ASC")
        cursor.use { c ->
            val idIdx = c.getColumnIndexOrThrow("id")
            val nameIdx = c.getColumnIndexOrThrow("name")
            val phoneIdx = c.getColumnIndexOrThrow("phone")
            val codeIdx = c.getColumnIndexOrThrow("code")
            val addrIdx = c.getColumnIndexOrThrow("address")

            while (c.moveToNext()) {
                list.add(
                    Customer(
                        id = c.getString(idIdx),
                        name = c.getString(nameIdx),
                        phone = c.getString(phoneIdx) ?: "",
                        code = c.getString(codeIdx) ?: "",
                        address = c.getString(addrIdx) ?: ""
                    )
                )
            }
        }
        if (list.none { it.code == Customer.DEFAULT.code }) {
            list.add(0, Customer.DEFAULT)
        }
        return list
    }

    // ----------------------------------------------------
    // Offline Sales Queue (Save when Offline, Sync when Online)
    // ----------------------------------------------------
    fun saveOfflineSale(sale: SaleRecord, status: String = STATUS_PENDING): Boolean {
        return try {
            val db = writableDatabase
            val itemsJsonArr = JSONArray()
            sale.items.forEach { item ->
                val obj = JSONObject().apply {
                    put("product_id", item.product.id)
                    put("code_no", item.product.codeNo)
                    put("item_name", item.product.itemName)
                    put("qty", item.qty)
                    put("unit_price", item.unitPrice)
                    put("discount", item.discount)
                    put("final_amount", item.finalAmount)
                    put("unit", item.product.unit)
                    put("category", item.product.categoryName)
                    put("barcode", item.product.barcode)
                    if (item.product.rawFields != null) {
                        put("raw_fields", item.product.rawFields.toString())
                    }
                }
                itemsJsonArr.put(obj)
            }

            val cv = ContentValues().apply {
                put("voucher_no", sale.voucherNo)
                put("sale_date", sale.saleDate)
                put("total_amount", sale.totalAmount)
                put("extra_discount", sale.extraDiscount)
                put("net_amount", sale.netAmount)
                put("paid", sale.paid)
                put("change", sale.change)
                put("credit", sale.credit)
                put("payment_type", sale.paymentType)
                put("sale_type", sale.saleType)
                put("remark", sale.remark)

                put("customer_id", sale.customer.id)
                put("customer_name", sale.customer.name)
                put("customer_phone", sale.customer.phone)
                put("customer_code", sale.customer.code)
                put("customer_address", sale.customer.address)

                put("user_uid", sale.user.uid)
                put("user_email", sale.user.email)
                put("user_display_name", sale.user.displayName)
                put("user_id_token", sale.user.idToken)

                put("items_json", itemsJsonArr.toString())
                put("status", status)
                put("created_at", System.currentTimeMillis())
                put("synced_at", if (status == STATUS_SYNCED) System.currentTimeMillis() else 0L)
            }

            val result = db.insertWithOnConflict(TABLE_OFFLINE_SALES, null, cv, SQLiteDatabase.CONFLICT_REPLACE)

            // Deduct local product stock for each sold item immediately
            sale.items.forEach { item ->
                deductLocalProductStock(item.product.id, item.qty)
            }

            result != -1L
        } catch (e: Exception) {
            Log.e(TAG, "Error saving offline sale to SQLite", e)
            false
        }
    }

    fun getPendingOfflineSales(): List<SaleRecord> {
        val list = mutableListOf<SaleRecord>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_OFFLINE_SALES,
            null,
            "status = ?",
            arrayOf(STATUS_PENDING),
            null,
            null,
            "created_at ASC"
        )

        cursor.use { c ->
            val vNoIdx = c.getColumnIndexOrThrow("voucher_no")
            val dateIdx = c.getColumnIndexOrThrow("sale_date")
            val totalIdx = c.getColumnIndexOrThrow("total_amount")
            val discIdx = c.getColumnIndexOrThrow("extra_discount")
            val netIdx = c.getColumnIndexOrThrow("net_amount")
            val paidIdx = c.getColumnIndexOrThrow("paid")
            val chgIdx = c.getColumnIndexOrThrow("change")
            val credIdx = c.getColumnIndexOrThrow("credit")
            val payTypeIdx = c.getColumnIndexOrThrow("payment_type")
            val saleTypeIdx = c.getColumnIndexOrThrow("sale_type")
            val remarkIdx = c.getColumnIndexOrThrow("remark")

            val cIdIdx = c.getColumnIndexOrThrow("customer_id")
            val cNameIdx = c.getColumnIndexOrThrow("customer_name")
            val cPhoneIdx = c.getColumnIndexOrThrow("customer_phone")
            val cCodeIdx = c.getColumnIndexOrThrow("customer_code")
            val cAddrIdx = c.getColumnIndexOrThrow("customer_address")

            val uUidIdx = c.getColumnIndexOrThrow("user_uid")
            val uEmailIdx = c.getColumnIndexOrThrow("user_email")
            val uNameIdx = c.getColumnIndexOrThrow("user_display_name")
            val uTokenIdx = c.getColumnIndexOrThrow("user_id_token")

            val itemsJsonIdx = c.getColumnIndexOrThrow("items_json")

            while (c.moveToNext()) {
                val customer = Customer(
                    id = c.getString(cIdIdx),
                    name = c.getString(cNameIdx),
                    phone = c.getString(cPhoneIdx) ?: "",
                    code = c.getString(cCodeIdx) ?: "",
                    address = c.getString(cAddrIdx) ?: ""
                )

                val user = UserSession(
                    uid = c.getString(uUidIdx),
                    email = c.getString(uEmailIdx),
                    displayName = c.getString(uNameIdx),
                    idToken = c.getString(uTokenIdx) ?: ""
                )

                val items = mutableListOf<CartItem>()
                val itemsStr = c.getString(itemsJsonIdx)
                if (!itemsStr.isNullOrEmpty()) {
                    try {
                        val arr = JSONArray(itemsStr)
                        for (i in 0 until arr.length()) {
                            val obj = arr.getJSONObject(i)
                            val rawStr = obj.optString("raw_fields", null)
                            val rawJson = if (rawStr != null) {
                                try { JSONObject(rawStr) } catch (_: Exception) { null }
                            } else null

                            val product = Product(
                                id = obj.getString("product_id"),
                                codeNo = obj.optString("code_no", ""),
                                itemName = obj.optString("item_name", "Item"),
                                qty = 100, // Placeholder
                                retailPrice = obj.optDouble("unit_price", 0.0),
                                categoryName = obj.optString("category", "General"),
                                barcode = obj.optString("barcode", ""),
                                unit = obj.optString("unit", "Pcs"),
                                rawFields = rawJson
                            )

                            items.add(
                                CartItem(
                                    product = product,
                                    qty = obj.optInt("qty", 1),
                                    unitPrice = obj.optDouble("unit_price", 0.0),
                                    discount = obj.optDouble("discount", 0.0),
                                    finalAmount = obj.optDouble("final_amount", 0.0)
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error deserializing sale items for ${c.getString(vNoIdx)}", e)
                    }
                }

                list.add(
                    SaleRecord(
                        voucherNo = c.getString(vNoIdx),
                        saleDate = c.getString(dateIdx),
                        totalAmount = c.getDouble(totalIdx),
                        extraDiscount = c.getDouble(discIdx),
                        netAmount = c.getDouble(netIdx),
                        paid = c.getDouble(paidIdx),
                        change = c.getDouble(chgIdx),
                        credit = c.getDouble(credIdx),
                        paymentType = c.getString(payTypeIdx) ?: "Cash",
                        saleType = c.getString(saleTypeIdx) ?: "retail",
                        remark = c.getString(remarkIdx) ?: "",
                        customer = customer,
                        user = user,
                        items = items
                    )
                )
            }
        }
        return list
    }

    fun getPendingOfflineSalesCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_OFFLINE_SALES WHERE status = ?",
            arrayOf(STATUS_PENDING)
        )
        return cursor.use { c ->
            if (c.moveToFirst()) c.getInt(0) else 0
        }
    }

    fun markSaleSynced(voucherNo: String) {
        try {
            val db = writableDatabase
            val cv = ContentValues().apply {
                put("status", STATUS_SYNCED)
                put("synced_at", System.currentTimeMillis())
            }
            db.update(TABLE_OFFLINE_SALES, cv, "voucher_no = ?", arrayOf(voucherNo))
            Log.d(TAG, "Voucher $voucherNo marked as SYNCED")
        } catch (e: Exception) {
            Log.e(TAG, "Error marking voucher $voucherNo as synced", e)
        }
    }
}
