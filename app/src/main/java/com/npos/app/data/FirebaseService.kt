package com.npos.app.data

import android.util.Log
import com.npos.app.data.model.CartItem
import com.npos.app.data.model.Customer
import com.npos.app.data.model.Product
import com.npos.app.data.model.SaleRecord
import com.npos.app.data.model.UserSalesSummary
import com.npos.app.data.model.UserSession
import com.npos.app.data.offline.NetworkMonitor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class FirebaseService {

    companion object {
        private const val TAG = "FirebaseService"
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var projectId: String = ""
        private set
    var apiKey: String = ""
        private set
    val databaseId: String = "(default)"

    fun updateConfig(newProjectId: String, newApiKey: String) {
        projectId = newProjectId.trim()
        apiKey = newApiKey.trim()
        Log.d(TAG, "Updated Firebase Config: Project=$projectId")
    }

    val isConfigured: Boolean
        get() = projectId.isNotBlank() && apiKey.isNotBlank()

    val authUrl: String
        get() = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$apiKey"

    val firestoreBase: String
        get() = "https://firestore.googleapis.com/v1/projects/$projectId/databases/$databaseId/documents"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            try {
                val response = chain.proceed(chain.request())
                if (response.isSuccessful) {
                    NetworkMonitor.notifySuccess()
                }
                response
            } catch (e: Exception) {
                if (e is java.net.UnknownHostException || e is java.net.SocketTimeoutException ||
                    e is java.net.ConnectException || e is java.net.SocketException
                ) {
                    NetworkMonitor.notifyFailure()
                }
                throw e
            }
        }
        .build()

    private val _authState = MutableStateFlow<UserSession?>(null)
    val authState: StateFlow<UserSession?> = _authState.asStateFlow()

    private val authListeners = mutableListOf<(UserSession?) -> Unit>()

    /**
     * Registers a callback listener for authentication state changes (login, relogin, logout).
     * Automatically dispatches the current session immediately to the new listener.
     * Returns an unsubscribe function to safely detach the listener.
     */
    fun onAuthStateChange(listener: (UserSession?) -> Unit): () -> Unit {
        synchronized(authListeners) {
            authListeners.add(listener)
        }
        listener(_authState.value)
        return {
            synchronized(authListeners) {
                authListeners.remove(listener)
            }
        }
    }

    private fun setAuthState(session: UserSession?) {
        _authState.value = session
        val currentListeners = synchronized(authListeners) { authListeners.toList() }
        currentListeners.forEach { listener ->
            try {
                listener(session)
            } catch (e: Exception) {
                Log.e(TAG, "Error in onAuthStateChange listener", e)
            }
        }
    }

    fun setSession(session: UserSession?) {
        setAuthState(session)
    }

    fun logout() {
        setAuthState(null)
    }

    suspend fun login(email: String, password: String): Result<UserSession> = withContext(Dispatchers.IO) {
        if (!isConfigured) {
            return@withContext Result.failure(Exception("ကျေးဇူးပြု၍ အပေါ်ရှိ Database Setting တွင် Firebase Project ID နှင့် Web API Key အရင်ထည့်သွင်းပေးပါ"))
        }
        try {
            val jsonBody = JSONObject().apply {
                put("email", email)
                put("password", password)
                put("returnSecureToken", true)
            }.toString()

            val request = Request.Builder()
                .url(authUrl)
                .post(jsonBody.toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                val errObj = try { JSONObject(responseBody).optJSONObject("error") } catch (_: Exception) { null }
                val errMsg = errObj?.optString("message", "Authentication failed") ?: "Login failed (${response.code})"
                return@withContext Result.failure(Exception(errMsg))
            }

            val respJson = JSONObject(responseBody)
            val session = UserSession(
                uid = respJson.optString("localId", ""),
                email = respJson.optString("email", email),
                displayName = respJson.optString("displayName", "N1 Cashier"),
                idToken = respJson.optString("idToken", ""),
                refreshToken = respJson.optString("refreshToken", "")
            )
            setAuthState(session)
            Result.success(session)
        } catch (e: Exception) {
            Log.e(TAG, "Login error", e)
            Result.failure(e)
        }
    }

    /**
     * Refreshes the authentication session using Firebase Secure Token Service.
     * This re-authenticates/re-logs in the current user seamlessly without requiring a password.
     */
    suspend fun reloginWithRefreshToken(refreshToken: String, existingUser: UserSession? = null): Result<UserSession> = withContext(Dispatchers.IO) {
        if (!isConfigured) {
            return@withContext Result.failure(Exception("Firebase is not configured"))
        }
        if (refreshToken.isBlank()) {
            return@withContext Result.failure(Exception("No refresh token available for relogin"))
        }

        try {
            val refreshUrl = "https://securetoken.googleapis.com/v1/token?key=$apiKey"
            val formBody = FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build()

            val request = Request.Builder()
                .url(refreshUrl)
                .post(formBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.w(TAG, "Relogin failed: HTTP ${response.code} $responseBody")
                setAuthState(null)
                return@withContext Result.failure(Exception("Session expired, please login again"))
            }

            val json = JSONObject(responseBody)
            val newIdToken = json.optString("id_token", "")
            val newRefreshToken = json.optString("refresh_token", refreshToken)
            val userId = json.optString("user_id", existingUser?.uid ?: "")

            // Attempt to fetch fresh profile details
            val lookupResult = lookupUser(newIdToken)
            val updatedUser = if (lookupResult.isSuccess) {
                val info = lookupResult.getOrNull()
                UserSession(
                    uid = info?.optString("localId", userId) ?: userId,
                    email = info?.optString("email", existingUser?.email ?: "") ?: existingUser?.email ?: "",
                    displayName = info?.optString("displayName", existingUser?.displayName ?: "Cashier") ?: existingUser?.displayName ?: "Cashier",
                    idToken = newIdToken,
                    refreshToken = newRefreshToken
                )
            } else {
                UserSession(
                    uid = userId,
                    email = existingUser?.email ?: "",
                    displayName = existingUser?.displayName ?: "Cashier",
                    idToken = newIdToken,
                    refreshToken = newRefreshToken
                )
            }

            setAuthState(updatedUser)
            Log.d(TAG, "Relogin successful for ${updatedUser.displayName} (${updatedUser.email})")
            Result.success(updatedUser)
        } catch (e: Exception) {
            Log.e(TAG, "Relogin error", e)
            Result.failure(e)
        }
    }

    /**
     * Looks up user profile information using the current idToken.
     */
    suspend fun lookupUser(idToken: String): Result<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val url = "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=$apiKey"
            val jsonBody = JSONObject().apply {
                put("idToken", idToken)
            }.toString()

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("User lookup failed: ${response.code}"))
            }

            val respJson = JSONObject(responseBody)
            val usersArr = respJson.optJSONArray("users")
            if (usersArr != null && usersArr.length() > 0) {
                Result.success(usersArr.getJSONObject(0))
            } else {
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Relogins the current user using their saved session credentials.
     */
    suspend fun reloginCurrentUser(session: UserSession): Result<UserSession> {
        val tokenToUse = session.refreshToken.ifBlank { session.idToken }
        return reloginWithRefreshToken(tokenToUse, session)
    }

    /**
     * Verifies if the session is still active or seamlessly re-authenticates with refresh token.
     */
    suspend fun verifyOrRelogin(session: UserSession?): Result<UserSession?> {
        if (session == null) {
            setAuthState(null)
            return Result.success(null)
        }

        // 1. Try quick profile lookup to see if idToken is currently valid
        val lookup = lookupUser(session.idToken)
        if (lookup.isSuccess) {
            val userObj = lookup.getOrNull()
            val validSession = UserSession(
                uid = userObj?.optString("localId", session.uid) ?: session.uid,
                email = userObj?.optString("email", session.email) ?: session.email,
                displayName = userObj?.optString("displayName", session.displayName) ?: session.displayName,
                idToken = session.idToken,
                refreshToken = session.refreshToken
            )
            setAuthState(validSession)
            return Result.success(validSession)
        }

        // 2. If idToken has expired, attempt silent relogin using refreshToken
        if (session.refreshToken.isNotBlank()) {
            val reloginResult = reloginWithRefreshToken(session.refreshToken, session)
            if (reloginResult.isSuccess) {
                return Result.success(reloginResult.getOrNull())
            }
        }

        // 3. Both failed - session is terminated
        setAuthState(null)
        return Result.failure(Exception("Session expired"))
    }

    private fun parseProductDocument(doc: JSONObject): Product? {
        val fullPath = doc.optString("name", "")
        val docId = fullPath.substringAfterLast("/")
        val fields = doc.optJSONObject("fields") ?: return null

        val itemName = fields.optJSONObject("item_name")?.optString("stringValue") ?: "Item"
        val codeNo = fields.optJSONObject("code_no")?.optString("integerValue")
            ?: fields.optJSONObject("code_no")?.optString("stringValue")
            ?: docId

        val qty = fields.optJSONObject("qty")?.optString("integerValue")?.toIntOrNull() ?: 0

        // Parse retail price
        var price = 0.0
        val retailMap = fields.optJSONObject("retail_price")?.optJSONObject("mapValue")?.optJSONObject("fields")
        if (retailMap != null) {
            val smallest = retailMap.optJSONObject("smallest_unit")?.optString("integerValue")
                ?: retailMap.optJSONObject("smallest_unit")?.optString("stringValue")
            price = smallest?.toDoubleOrNull() ?: 0.0
            if (price <= 0.0) {
                val medium = retailMap.optJSONObject("medium_unit")?.optString("integerValue")
                price = medium?.toDoubleOrNull() ?: 0.0
            }
        }

        if (price <= 0.0) {
            price = fields.optJSONObject("qty_sale_price")?.optString("integerValue")?.toDoubleOrNull()
                ?: fields.optJSONObject("sale_price")?.optString("integerValue")?.toDoubleOrNull()
                ?: 0.0
        }

        // Do not return products with stock <= 0 or no sale price (retail_price <= 0)
        if (qty <= 0 || price <= 0.0) {
            return null
        }

        val catField = fields.optJSONObject("category")
        var catName = catField?.optJSONObject("mapValue")?.optJSONObject("fields")?.optJSONObject("cat_name")?.optString("stringValue")
            ?: catField?.optString("stringValue")
            ?: ""

        if (catName.isBlank() || catName == "Default") {
            val lowerName = itemName.lowercase()
            catName = when {
                lowerName.contains("စပန့်") -> "စပန့်"
                lowerName.contains("ဆေးလိပ်") || lowerName.contains("လိပ်") -> "လိပ်"
                lowerName.contains("cover") -> "Cover"
                lowerName.contains("တီရှပ်") -> "တီရှပ်"
                lowerName.contains("ဖုန်း") || lowerName.contains("phone") -> "Phone"
                lowerName.contains("ပိုလို") || lowerName.contains("ရှပ်") -> "ပိုလိုရှပ်"
                lowerName.contains("ကိုက်") -> "ကိုက်"
                lowerName.contains("ပိုး") -> "ပိုးထည်"
                catName.isNotBlank() -> catName
                else -> "General"
            }
        }

        val packing = fields.optJSONObject("packing_size")?.optJSONObject("mapValue")?.optJSONObject("fields")
        val barcode = packing?.optJSONObject("smallest_barcode")?.optString("stringValue")
            ?: packing?.optJSONObject("medium_barcode")?.optString("stringValue")
            ?: codeNo
        val unit = packing?.optJSONObject("smallest_unit")?.optString("stringValue") ?: "ခု"

        return Product(
            id = docId,
            codeNo = codeNo,
            itemName = itemName,
            qty = qty,
            retailPrice = price,
            categoryName = catName,
            barcode = barcode,
            unit = unit,
            rawFields = fields
        )
    }

    suspend fun fetchTotalProductsCount(idToken: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val url = "$firestoreBase:runAggregationQuery"
            val payload = JSONObject().apply {
                put("structuredAggregationQuery", JSONObject().apply {
                    put("structuredQuery", JSONObject().apply {
                        put("from", JSONArray().put(JSONObject().put("collectionId", "products")))
                    })
                    put("aggregations", JSONArray().put(JSONObject().apply {
                        put("alias", "total_count")
                        put("count", JSONObject())
                    }))
                })
            }
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $idToken")
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Count failed: ${response.code}"))
            }

            val arr = JSONArray(body)
            var count = 0
            if (arr.length() > 0) {
                val resObj = arr.getJSONObject(0).optJSONObject("result")
                val agg = resObj?.optJSONObject("aggregateFields")
                val totalCountObj = agg?.optJSONObject("total_count")
                val countStr = totalCountObj?.optString("integerValue")
                count = countStr?.toIntOrNull() ?: 0
            }
            Result.success(count)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching product count", e)
            Result.failure(e)
        }
    }

    suspend fun fetchCategories(idToken: String): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val url = "$firestoreBase/category?pageSize=50"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $idToken")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Fetch categories failed: ${response.code}"))
            }

            val root = JSONObject(body)
            val docs = root.optJSONArray("documents") ?: JSONArray()
            val categories = mutableListOf<String>()

            for (i in 0 until docs.length()) {
                val doc = docs.getJSONObject(i)
                val fields = doc.optJSONObject("fields") ?: continue
                val catName = fields.optJSONObject("cat_name")?.optString("stringValue")
                if (!catName.isNullOrBlank() && !categories.contains(catName)) {
                    categories.add(catName)
                }
            }
            categories.sort()
            Result.success(categories)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching categories from category collection", e)
            Result.failure(e)
        }
    }

    suspend fun fetchProducts(idToken: String, limit: Int = 100): Result<List<Product>> = withContext(Dispatchers.IO) {
        // Method 1: Firestore runQuery with orderBy("created_at", "DESCENDING")
        try {
            val runQueryUrl = "$firestoreBase:runQuery"
            val structuredQuery = JSONObject().apply {
                put("from", JSONArray().put(JSONObject().put("collectionId", "products")))
                put("orderBy", JSONArray().put(JSONObject().apply {
                    put("field", JSONObject().put("fieldPath", "created_at"))
                    put("direction", "DESCENDING")
                }))
                put("limit", limit)
            }
            val queryPayload = JSONObject().put("structuredQuery", structuredQuery)

            val request = Request.Builder()
                .url(runQueryUrl)
                .addHeader("Authorization", "Bearer $idToken")
                .post(queryPayload.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val response = client.newCall(request).execute()
            val respStr = response.body?.string().orEmpty()

            if (response.isSuccessful && respStr.startsWith("[")) {
                val products = mutableListOf<Product>()
                val arr = JSONArray(respStr)
                for (i in 0 until arr.length()) {
                    val itemObj = arr.getJSONObject(i)
                    val doc = itemObj.optJSONObject("document") ?: continue
                    parseProductDocument(doc)?.let { products.add(it) }
                }
                if (products.isNotEmpty()) {
                    Log.d(TAG, "Successfully fetched ${products.size} products ordered by created_at DESC via runQuery")
                    return@withContext Result.success(products)
                }
            } else {
                if (response.code == 429) {
                    Log.w(TAG, "runQuery returned 429 (Resource Exhausted / Quota Exceeded)")
                    return@withContext Result.failure(Exception("429 Quota Exceeded: Firebase Firestore နေ့စဉ် Read Quota (50,000 reads) ပြည့်သွားပါပြီ"))
                }
                Log.w(TAG, "orderBy created_at runQuery returned code ${response.code}, falling back to documents API")
            }
        } catch (e: Exception) {
            Log.w(TAG, "orderBy created_at runQuery failed, falling back to documents API", e)
        }

        // Method 2 (Fallback): Standard documents paging if runQuery is unavailable
        try {
            val products = mutableListOf<Product>()
            var nextPageToken: String? = null
            val effectivePageSize = limit.coerceAtMost(300)

            do {
                val pageUrl = if (nextPageToken.isNullOrBlank()) {
                    "$firestoreBase/products?pageSize=$effectivePageSize"
                } else {
                    "$firestoreBase/products?pageSize=$effectivePageSize&pageToken=$nextPageToken"
                }

                val request = Request.Builder()
                    .url(pageUrl)
                    .addHeader("Authorization", "Bearer $idToken")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string().orEmpty()

                if (!response.isSuccessful) {
                    if (products.isNotEmpty()) {
                        return@withContext Result.success(products)
                    }
                    val errMsg = if (response.code == 429) {
                        "429 Quota Exceeded: Firebase Firestore နေ့စဉ် Read Quota (50,000 reads) ပြည့်သွားပါပြီ (Local Cache ဖြင့် ဆက်လက်အသုံးပြုနိုင်ပါသည်)"
                    } else {
                        "Failed to fetch products: ${response.code}"
                    }
                    return@withContext Result.failure(Exception(errMsg))
                }

                val root = JSONObject(responseBody)
                val docs = root.optJSONArray("documents") ?: JSONArray()

                for (i in 0 until docs.length()) {
                    val doc = docs.getJSONObject(i)
                    parseProductDocument(doc)?.let { products.add(it) }
                    if (products.size >= limit) break
                }

                nextPageToken = root.optString("nextPageToken", null)
            } while (!nextPageToken.isNullOrBlank() && products.size < limit)

            Result.success(products)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching products", e)
            Result.failure(e)
        }
    }

    /**
     * Real-time Snapshot Listener for products with orderBy("created_at", "DESCENDING").
     * Invokes [onSnapshot] with latest list of products whenever changes or updates occur.
     * Returns an unsubscribe function `() -> Unit` (unsub) to cancel and stop listening immediately.
     */
    fun onProductsSnapshot(
        idToken: String,
        limit: Int = 100,
        intervalMs: Long = 60000L, // Optimized default: 60s to minimize Firestore read quota
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        onError: ((Exception) -> Unit)? = null,
        onSnapshot: (List<Product>) -> Unit
    ): () -> Unit {
        var lastProductsSignature: String = ""
        val job = scope.launch {
            while (isActive) {
                delay(intervalMs) // Wait for next sync cycle
                if (!isActive) break
                try {
                    val result = fetchProducts(idToken, limit)
                    result.onSuccess { freshProducts ->
                        val signature = freshProducts.joinToString("|") { "${it.id}_${it.qty}_${it.retailPrice}_${it.codeNo}" }
                        if (signature != lastProductsSignature) {
                            lastProductsSignature = signature
                            withContext(Dispatchers.Main) {
                                onSnapshot(freshProducts)
                            }
                        }
                    }.onFailure { err ->
                        Log.w(TAG, "onProductsSnapshot check notice: ${err.message}")
                        onError?.let { callback ->
                            withContext(Dispatchers.Main) {
                                callback(err as? Exception ?: Exception(err))
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    Log.w(TAG, "onProductsSnapshot check error", e)
                    onError?.let { callback ->
                        withContext(Dispatchers.Main) {
                            callback(e)
                        }
                    }
                }
            }
        }

        // Return unsubscribe lambda
        return {
            Log.d(TAG, "Unsubscribing from onProductsSnapshot")
            job.cancel()
        }
    }

    suspend fun searchProducts(idToken: String, query: String, category: String? = null, limit: Int = 100): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val trimmed = query.trim()
            val lowerQuery = trimmed.lowercase()
            val hasCategory = !category.isNullOrBlank() && category != "All"

            if (trimmed.isEmpty() && !hasCategory) {
                return@withContext fetchProducts(idToken, limit)
            }

            if (trimmed.isEmpty() && hasCategory) {
                val allResult = fetchProducts(idToken, maxOf(limit * 3, 250))
                val filtered = mutableListOf<Product>()
                allResult.onSuccess { list ->
                    filtered.addAll(
                        list.filter { p ->
                            p.categoryName.equals(category, ignoreCase = true) ||
                            p.itemName.contains(category!!, ignoreCase = true)
                        }.take(limit)
                    )
                }
                return@withContext Result.success(filtered)
            }

            val products = mutableListOf<Product>()
            val seenDocIds = mutableSetOf<String>()
            val runQueryUrl = "$firestoreBase:runQuery"

            // 1. Array-contains query on "keywords" field using Firestore runQuery (Original N-POS Web App Method)
            try {
                val keywordsQueryPayload = JSONObject().apply {
                    val structuredQuery = JSONObject().apply {
                        put("from", JSONArray().put(JSONObject().put("collectionId", "products")))
                        put("where", JSONObject().apply {
                            put("fieldFilter", JSONObject().apply {
                                put("field", JSONObject().put("fieldPath", "keywords"))
                                put("op", "ARRAY_CONTAINS")
                                put("value", JSONObject().put("stringValue", lowerQuery))
                            })
                        })
                        put("orderBy", JSONArray().put(JSONObject().apply {
                            put("field", JSONObject().put("fieldPath", "created_at"))
                            put("direction", "DESCENDING")
                        }))
                        put("limit", limit)
                    }
                    put("structuredQuery", structuredQuery)
                }

                val arrayContainsReq = Request.Builder()
                    .url(runQueryUrl)
                    .addHeader("Authorization", "Bearer $idToken")
                    .post(keywordsQueryPayload.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(arrayContainsReq).execute()
                val respStr = response.body?.string().orEmpty()
                if (response.isSuccessful && respStr.startsWith("[")) {
                    val arr = JSONArray(respStr)
                    for (i in 0 until arr.length()) {
                        val itemObj = arr.getJSONObject(i)
                        val doc = itemObj.optJSONObject("document") ?: continue
                        parseProductDocument(doc)?.let { product ->
                            if (seenDocIds.add(product.id)) {
                                if (category.isNullOrEmpty() || category == "All" || product.categoryName.equals(category, ignoreCase = true)) {
                                    products.add(product)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "ARRAY_CONTAINS keywords query failed", e)
            }

            // 2. Also query by exact code_no using runQuery
            try {
                val codeQueryPayload = JSONObject().apply {
                    val structuredQuery = JSONObject().apply {
                        put("from", JSONArray().put(JSONObject().put("collectionId", "products")))
                        put("where", JSONObject().apply {
                            put("fieldFilter", JSONObject().apply {
                                put("field", JSONObject().put("fieldPath", "code_no"))
                                put("op", "EQUAL")
                                put("value", JSONObject().put("stringValue", trimmed))
                            })
                        })
                        put("limit", 10)
                    }
                    put("structuredQuery", structuredQuery)
                }

                val codeReq = Request.Builder()
                    .url(runQueryUrl)
                    .addHeader("Authorization", "Bearer $idToken")
                    .post(codeQueryPayload.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(codeReq).execute()
                val respStr = response.body?.string().orEmpty()
                if (response.isSuccessful && respStr.startsWith("[")) {
                    val arr = JSONArray(respStr)
                    for (i in 0 until arr.length()) {
                        val itemObj = arr.getJSONObject(i)
                        val doc = itemObj.optJSONObject("document") ?: continue
                        parseProductDocument(doc)?.let { product ->
                            if (seenDocIds.add(product.id)) {
                                if (category.isNullOrEmpty() || category == "All" || product.categoryName.equals(category, ignoreCase = true)) {
                                    products.add(product)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "code_no query failed", e)
            }

            // 3. Fallback: If no products found via array-contains, search fetched collection (handles older docs without keywords)
            if (products.isEmpty()) {
                val fallbackResult = fetchProducts(idToken, limit)
                fallbackResult.onSuccess { allProducts ->
                    val filtered = allProducts.filter { p ->
                        val matchesQ = p.itemName.contains(trimmed, ignoreCase = true) ||
                                p.codeNo.contains(trimmed, ignoreCase = true) ||
                                p.barcode.contains(trimmed, ignoreCase = true)
                        val matchesCat = category.isNullOrEmpty() || category == "All" || p.categoryName.equals(category, ignoreCase = true)
                        matchesQ && matchesCat
                    }
                    products.addAll(filtered)
                }
            }

            Result.success(products)
        } catch (e: Exception) {
            Log.e(TAG, "Error in searchProducts", e)
            Result.failure(e)
        }
    }

    suspend fun fetchCustomers(idToken: String): Result<List<Customer>> = withContext(Dispatchers.IO) {
        try {
            val list = mutableListOf(Customer.DEFAULT)
            val url = "$firestoreBase/customers?pageSize=50"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $idToken")
                .get()
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string().orEmpty()
                val root = JSONObject(body)
                val docs = root.optJSONArray("documents")
                if (docs != null) {
                    for (i in 0 until docs.length()) {
                        val doc = docs.getJSONObject(i)
                        val docId = doc.optString("name", "").substringAfterLast("/")
                        val fields = doc.optJSONObject("fields") ?: continue
                        val isDeleted = fields.optJSONObject("is_deleted")?.optBoolean("booleanValue") ?: false
                        if (isDeleted) continue
                        val name = fields.optJSONObject("name")?.optString("stringValue") ?: continue
                        val phone = fields.optJSONObject("phone")?.optString("stringValue") ?: ""
                        val code = fields.optJSONObject("code")?.optString("integerValue")
                            ?: fields.optJSONObject("code")?.optString("stringValue") ?: ""
                        val address = fields.optJSONObject("address")?.optString("stringValue") ?: ""
                        list.add(Customer(id = docId, name = name, phone = phone, code = code, address = address))
                    }
                }
            }
            Result.success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching customers", e)
            Result.success(listOf(Customer.DEFAULT))
        }
    }

    suspend fun createCustomer(idToken: String, customer: Customer, user: UserSession): Result<Customer> = withContext(Dispatchers.IO) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val nowUtc = sdf.format(Date())
            val codeVal = if (customer.code.isNotBlank()) customer.code else System.currentTimeMillis().toString()

            val fields = JSONObject().apply {
                put("name", JSONObject().apply { put("stringValue", customer.name) })
                put("name_lower_case", JSONObject().apply { put("stringValue", customer.name.lowercase()) })
                put("phone", JSONObject().apply { put("stringValue", customer.phone) })
                if (codeVal.all { it.isDigit() }) {
                    put("code", JSONObject().apply { put("integerValue", codeVal) })
                } else {
                    put("code", JSONObject().apply { put("stringValue", codeVal) })
                }
                put("address", JSONObject().apply { put("stringValue", customer.address) })
                put("is_deleted", JSONObject().apply { put("booleanValue", false) })
                put("created_at", JSONObject().apply { put("timestampValue", nowUtc) })
                put("user", JSONObject().apply {
                    put("mapValue", JSONObject().apply {
                        put("fields", JSONObject().apply {
                            put("uid", JSONObject().apply { put("stringValue", user.uid) })
                            put("displayName", JSONObject().apply { put("stringValue", user.displayName) })
                        })
                    })
                })
                put("edit_user", JSONObject().apply {
                    put("mapValue", JSONObject().apply {
                        put("fields", JSONObject().apply {
                            put("uid", JSONObject().apply { put("stringValue", user.uid) })
                            put("type", JSONObject().apply { put("stringValue", "added") })
                        })
                    })
                })
            }

            val payload = JSONObject().apply { put("fields", fields) }.toString()
            val url = "$firestoreBase/customers"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $idToken")
                .post(payload.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val respBody = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Create customer failed: ${response.code} - $respBody"))
            }

            val respObj = JSONObject(respBody)
            val newId = respObj.optString("name", "").substringAfterLast("/")
            val created = customer.copy(id = newId, code = codeVal)
            Result.success(created)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating customer", e)
            Result.failure(e)
        }
    }

    suspend fun updateCustomer(idToken: String, customer: Customer, user: UserSession): Result<Customer> = withContext(Dispatchers.IO) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val nowUtc = sdf.format(Date())

            val fields = JSONObject().apply {
                put("name", JSONObject().apply { put("stringValue", customer.name) })
                put("name_lower_case", JSONObject().apply { put("stringValue", customer.name.lowercase()) })
                put("phone", JSONObject().apply { put("stringValue", customer.phone) })
                if (customer.code.all { it.isDigit() }) {
                    put("code", JSONObject().apply { put("integerValue", customer.code) })
                } else {
                    put("code", JSONObject().apply { put("stringValue", customer.code) })
                }
                put("address", JSONObject().apply { put("stringValue", customer.address) })
                put("updated_at", JSONObject().apply { put("timestampValue", nowUtc) })
                put("edit_user", JSONObject().apply {
                    put("mapValue", JSONObject().apply {
                        put("fields", JSONObject().apply {
                            put("uid", JSONObject().apply { put("stringValue", user.uid) })
                            put("type", JSONObject().apply { put("stringValue", "updated") })
                        })
                    })
                })
            }

            val payload = JSONObject().apply { put("fields", fields) }.toString()
            val url = "$firestoreBase/customers/${customer.id}?updateMask.fieldPaths=name&updateMask.fieldPaths=name_lower_case&updateMask.fieldPaths=phone&updateMask.fieldPaths=code&updateMask.fieldPaths=address&updateMask.fieldPaths=updated_at&updateMask.fieldPaths=edit_user"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $idToken")
                .patch(payload.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Update customer failed: ${response.code}"))
            }

            Result.success(customer)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating customer", e)
            Result.failure(e)
        }
    }

    suspend fun deleteCustomer(idToken: String, customerId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val url = "$firestoreBase/customers/$customerId"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $idToken")
                .delete()
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Delete customer failed: ${response.code}"))
            }

            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting customer", e)
            Result.failure(e)
        }
    }

    suspend fun saveSaleRecord(idToken: String, sale: SaleRecord): Result<String> = withContext(Dispatchers.IO) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val nowUtc = sdf.format(Date())

            val productsArray = JSONArray()
            sale.items.forEach { item ->
                // Clone original product raw fields if available, otherwise construct standard N-POS schema
                val itemFields = if (item.product.rawFields != null) {
                    JSONObject(item.product.rawFields.toString())
                } else {
                    JSONObject()
                }

                itemFields.put("id", JSONObject().apply { put("stringValue", item.product.id) })
                itemFields.put("item_name", JSONObject().apply { put("stringValue", item.product.itemName) })
                itemFields.put("code_no", JSONObject().apply {
                    if (item.product.codeNo.all { it.isDigit() }) {
                        put("integerValue", item.product.codeNo)
                    } else {
                        put("stringValue", item.product.codeNo)
                    }
                })
                itemFields.put("sale_qty", JSONObject().apply { put("integerValue", item.qty.toLong().toString()) })
                itemFields.put("qty_sale_price", JSONObject().apply { put("integerValue", item.unitPrice.toLong().toString()) })
                itemFields.put("final_amount", JSONObject().apply { put("integerValue", item.finalAmount.toLong().toString()) })
                itemFields.put("amount", JSONObject().apply { put("integerValue", (item.unitPrice * item.qty).toLong().toString()) })
                itemFields.put("discount", JSONObject().apply { put("integerValue", item.discount.toLong().toString()) })
                itemFields.put("qty_sale_price_unit", JSONObject().apply { put("stringValue", item.product.unit) })
                itemFields.put("sale_type", JSONObject().apply { put("stringValue", "retail") })
                itemFields.put("auto_cal", JSONObject().apply { put("booleanValue", true) })
                itemFields.put("is_deleted", JSONObject().apply { put("booleanValue", false) })
                itemFields.put("created_at", JSONObject().apply { put("timestampValue", nowUtc) })

                productsArray.put(JSONObject().apply {
                    put("mapValue", JSONObject().apply { put("fields", itemFields) })
                })
            }

            // Top-level sales document fields matching N-POS Firestore schema
            val docFields = JSONObject().apply {
                put("voucher_no", JSONObject().apply { put("stringValue", sale.voucherNo) })
                put("sale_date", JSONObject().apply { put("timestampValue", nowUtc) })
                put("total_amount", JSONObject().apply { put("integerValue", sale.totalAmount.toLong().toString()) })
                put("extra_discount", JSONObject().apply { put("integerValue", sale.extraDiscount.toLong().toString()) })
                put("net_amount", JSONObject().apply { put("integerValue", sale.netAmount.toLong().toString()) })
                put("paid", JSONObject().apply { put("integerValue", sale.paid.toLong().toString()) })
                put("change", JSONObject().apply { put("integerValue", sale.change.toLong().toString()) })
                put("credit", JSONObject().apply { put("integerValue", sale.credit.toLong().toString()) })
                put("sale_type", JSONObject().apply { put("stringValue", sale.saleType) })
                put("payment_type", JSONObject().apply { put("stringValue", sale.paymentType) })
                put("remark", JSONObject().apply { put("stringValue", sale.remark) })

                // Customer map
                val custFields = JSONObject().apply {
                    if (sale.customer.id.all { it.isDigit() }) {
                        put("id", JSONObject().apply { put("integerValue", sale.customer.id) })
                    } else {
                        put("id", JSONObject().apply { put("stringValue", sale.customer.id) })
                    }
                    put("name", JSONObject().apply { put("stringValue", sale.customer.name) })
                    put("phone", JSONObject().apply { put("stringValue", sale.customer.phone) })
                    if (sale.customer.code.all { it.isDigit() }) {
                        put("code", JSONObject().apply { put("integerValue", sale.customer.code) })
                    } else {
                        put("code", JSONObject().apply { put("stringValue", sale.customer.code) })
                    }
                }
                put("customer", JSONObject().apply { put("mapValue", JSONObject().apply { put("fields", custFields) }) })

                // User map
                val userFields = JSONObject().apply {
                    put("uid", JSONObject().apply { put("stringValue", sale.user.uid) })
                    put("displayName", JSONObject().apply { put("stringValue", sale.user.displayName) })
                }
                put("user", JSONObject().apply { put("mapValue", JSONObject().apply { put("fields", userFields) }) })

                // Products array
                put("products", JSONObject().apply { put("arrayValue", JSONObject().apply { put("values", productsArray) }) })
            }

            val payload = JSONObject().apply {
                put("fields", docFields)
            }.toString()

            val url = "$firestoreBase/sales"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $idToken")
                .post(payload.toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e(TAG, "Save sale failed (${response.code}): $body")
                val detailMsg = try {
                    val errJson = JSONObject(body)
                    errJson.optJSONObject("error")?.optString("message") ?: body
                } catch (_: Exception) { body }
                return@withContext Result.failure(Exception("Failed to save sale (${response.code}): $detailMsg"))
            }

            val respJson = JSONObject(body)
            val createdId = respJson.optString("name", "").substringAfterLast("/")

            // Automatically deduct sold quantities from original products in Firestore asynchronously
            serviceScope.launch {
                try {
                    deductSaleProductsStock(idToken, sale.items)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to deduct product stock after sale", e)
                }
            }

            Result.success(createdId.ifEmpty { sale.voucherNo })
        } catch (e: Exception) {
            Log.e(TAG, "Error saving sale to Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Deducts sold quantity from a single product's stock in Firestore.
     */
    suspend fun deductProductStock(
        idToken: String,
        productId: String,
        soldQty: Int,
        currentKnownQty: Int
    ): Result<Int> = withContext(Dispatchers.IO) {
        if (!isConfigured || productId.isBlank()) {
            return@withContext Result.failure(Exception("Invalid product or config"))
        }

        try {
            var liveQty = currentKnownQty
            val getUrl = "$firestoreBase/products/$productId"
            val getReq = Request.Builder()
                .url(getUrl)
                .addHeader("Authorization", "Bearer $idToken")
                .get()
                .build()

            val getResp = client.newCall(getReq).execute()
            val getBody = getResp.body?.string().orEmpty()
            if (getResp.isSuccessful && getBody.isNotBlank()) {
                try {
                    val docJson = JSONObject(getBody)
                    val fields = docJson.optJSONObject("fields")
                    val firestoreQty = fields?.optJSONObject("qty")?.optString("integerValue")?.toIntOrNull()
                        ?: fields?.optJSONObject("qty")?.optString("stringValue")?.toIntOrNull()
                    if (firestoreQty != null) {
                        liveQty = firestoreQty
                    }
                } catch (_: Exception) {}
            }

            val newQty = maxOf(0, liveQty - soldQty)

            val patchUrl = "$firestoreBase/products/$productId?updateMask.fieldPaths=qty"
            val patchFields = JSONObject().apply {
                put("qty", JSONObject().apply {
                    put("integerValue", newQty.toLong().toString())
                })
            }
            val payload = JSONObject().apply { put("fields", patchFields) }.toString()

            val patchReq = Request.Builder()
                .url(patchUrl)
                .addHeader("Authorization", "Bearer $idToken")
                .patch(payload.toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val patchResp = client.newCall(patchReq).execute()
            val patchBody = patchResp.body?.string().orEmpty()
            if (!patchResp.isSuccessful) {
                Log.w(TAG, "Stock deduction failed for product $productId: code=${patchResp.code} body=$patchBody")
                return@withContext Result.failure(Exception("Failed to update product stock: ${patchResp.code}"))
            }

            Log.d(TAG, "Stock deducted for product $productId: $liveQty - $soldQty = $newQty")
            Result.success(newQty)
        } catch (e: Exception) {
            Log.e(TAG, "Error deducting stock for product $productId", e)
            Result.failure(e)
        }
    }

    /**
     * Deducts sold quantities for all items in a completed sale.
     */
    suspend fun deductSaleProductsStock(
        idToken: String,
        items: List<CartItem>
    ): Map<String, Int> = withContext(Dispatchers.IO) {
        val updatedStockMap = mutableMapOf<String, Int>()
        val aggregatedSold = mutableMapOf<String, Pair<Product, Int>>()

        for (item in items) {
            val prodId = item.product.id
            if (prodId.isBlank()) continue
            val existing = aggregatedSold[prodId]
            if (existing != null) {
                aggregatedSold[prodId] = Pair(item.product, existing.second + item.qty)
            } else {
                aggregatedSold[prodId] = Pair(item.product, item.qty)
            }
        }

        aggregatedSold.forEach { (prodId, pair) ->
            val (product, soldQty) = pair
            val result = deductProductStock(idToken, prodId, soldQty, product.qty)
            if (result.isSuccess) {
                updatedStockMap[prodId] = result.getOrDefault(maxOf(0, product.qty - soldQty))
            } else {
                updatedStockMap[prodId] = maxOf(0, product.qty - soldQty)
            }
        }

        updatedStockMap
    }

    suspend fun fetchUserSalesSummary(idToken: String, user: UserSession): Result<UserSalesSummary> = withContext(Dispatchers.IO) {
        try {
            val todayFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val monthFmt = SimpleDateFormat("yyyy-MM", Locale.US)
            val todayStr = todayFmt.format(Date())
            val monthStr = monthFmt.format(Date())

            val body = JSONObject().apply {
                put("structuredQuery", JSONObject().apply {
                    put("from", JSONArray().put(JSONObject().apply { put("collectionId", "sales") }))
                    put("where", JSONObject().apply {
                        put("fieldFilter", JSONObject().apply {
                            put("field", JSONObject().apply { put("fieldPath", "user.uid") })
                            put("op", "EQUAL")
                            put("value", JSONObject().apply { put("stringValue", user.uid) })
                        })
                    })
                    put("limit", 300)
                })
            }

            val url = "$firestoreBase:runQuery"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $idToken")
                .post(body.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val response = client.newCall(request).execute()
            var todayAmt = 0.0
            var monthAmt = 0.0
            var todayCnt = 0
            var monthCnt = 0

            if (response.isSuccessful) {
                val respStr = response.body?.string().orEmpty()
                val arr = JSONArray(respStr)
                for (i in 0 until arr.length()) {
                    val item = arr.getJSONObject(i)
                    val doc = item.optJSONObject("document") ?: continue
                    val fields = doc.optJSONObject("fields") ?: continue
                    val net = fields.optJSONObject("net_amount")?.optLong("integerValue")?.toDouble()
                        ?: fields.optJSONObject("net_amount")?.optDouble("doubleValue")
                        ?: fields.optJSONObject("net_amount")?.optString("stringValue")?.toDoubleOrNull()
                        ?: 0.0
                    val dateStr = fields.optJSONObject("sale_date")?.optString("timestampValue")
                        ?: fields.optJSONObject("sale_date")?.optString("stringValue")
                        ?: ""

                    if (dateStr.contains(monthStr)) {
                        monthAmt += net
                        monthCnt++
                    }
                    if (dateStr.contains(todayStr)) {
                        todayAmt += net
                        todayCnt++
                    }
                }
            }
            Result.success(
                UserSalesSummary(
                    todayAmount = todayAmt,
                    thisMonthAmount = monthAmt,
                    todayCount = todayCnt,
                    thisMonthCount = monthCnt
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user sales summary", e)
            Result.success(UserSalesSummary())
        }
    }

    private fun parseFirestoreDate(doc: JSONObject, fields: JSONObject): String {
        val saleDateObj = fields.optJSONObject("sale_date")
        val createdAtObj = fields.optJSONObject("created_at")
        val dateObj = fields.optJSONObject("date")

        var rawDate = ""
        val candidateObjects = listOfNotNull(saleDateObj, createdAtObj, dateObj)
        for (obj in candidateObjects) {
            if (obj.has("timestampValue")) {
                val v = obj.optString("timestampValue", "").trim()
                if (v.isNotEmpty()) { rawDate = v; break }
            }
            if (obj.has("stringValue")) {
                val v = obj.optString("stringValue", "").trim()
                if (v.isNotEmpty()) { rawDate = v; break }
            }
            if (obj.has("integerValue")) {
                val millis = obj.optLong("integerValue", 0L)
                if (millis > 0) {
                    val epochMillis = if (millis < 100000000000L) millis * 1000 else millis
                    val outFmt = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                    return outFmt.format(Date(epochMillis))
                }
            }
        }

        // Fallback to Firestore doc metadata createTime or updateTime
        if (rawDate.isEmpty()) {
            val createTime = doc.optString("createTime", "").trim()
            if (createTime.isNotEmpty()) {
                rawDate = createTime
            } else {
                val updateTime = doc.optString("updateTime", "").trim()
                if (updateTime.isNotEmpty()) rawDate = updateTime
            }
        }

        // Fallback to first item's created_at
        if (rawDate.isEmpty()) {
            val productsArr = fields.optJSONObject("products")?.optJSONObject("arrayValue")?.optJSONArray("values")
                ?: fields.optJSONObject("saleItems")?.optJSONObject("arrayValue")?.optJSONArray("values")
            if (productsArr != null && productsArr.length() > 0) {
                val firstItem = productsArr.optJSONObject(0)?.optJSONObject("mapValue")?.optJSONObject("fields")
                val itemCreatedAt = firstItem?.optJSONObject("created_at")?.optString("timestampValue", "")?.trim()
                if (!itemCreatedAt.isNullOrEmpty()) rawDate = itemCreatedAt
            }
        }

        if (rawDate.isEmpty()) {
            return SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
        }

        return try {
            if (rawDate.contains("T")) {
                val clean = rawDate.substringBefore(".").substringBefore("Z")
                val inFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val outFmt = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                inFmt.parse(clean)?.let { outFmt.format(it) } ?: rawDate
            } else if (rawDate.length >= 10 && rawDate.indexOf("-") == 4) {
                val inFmt = if (rawDate.length >= 16) {
                    SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
                } else {
                    SimpleDateFormat("yyyy-MM-dd", Locale.US)
                }
                val outFmt = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                inFmt.parse(rawDate.take(16))?.let { outFmt.format(it) } ?: rawDate
            } else {
                rawDate
            }
        } catch (_: Exception) {
            rawDate
        }
    }

    private fun parseSaleRecordDocument(doc: JSONObject): SaleRecord? {
        val fields = doc.optJSONObject("fields") ?: return null
        val voucherNo = fields.optJSONObject("voucher_no")?.optString("stringValue") ?: "---"
        val formattedDate = parseFirestoreDate(doc, fields)

        fun parseNum(key: String): Double {
            val obj = fields.optJSONObject(key) ?: return 0.0
            return obj.optLong("integerValue").toDouble().takeIf { it != 0.0 }
                ?: obj.optDouble("doubleValue", 0.0).takeIf { it != 0.0 }
                ?: obj.optString("stringValue").toDoubleOrNull()
                ?: 0.0
        }

        val totalAmount = parseNum("total_amount")
        val extraDiscount = parseNum("extra_discount")
        val netAmount = parseNum("net_amount").takeIf { it > 0.0 } ?: (totalAmount - extraDiscount)
        val paid = parseNum("paid")
        val change = parseNum("change")
        val credit = parseNum("credit")
        val paymentType = fields.optJSONObject("payment_type")?.optString("stringValue") ?: "Cash"
        val saleType = fields.optJSONObject("sale_type")?.optString("stringValue") ?: "retail"
        val remark = fields.optJSONObject("remark")?.optString("stringValue") ?: ""

        val custFields = fields.optJSONObject("customer")?.optJSONObject("mapValue")?.optJSONObject("fields")
        val customer = if (custFields != null) {
            Customer(
                id = custFields.optJSONObject("id")?.optString("integerValue")
                    ?: custFields.optJSONObject("id")?.optString("stringValue") ?: "123456789",
                name = custFields.optJSONObject("name")?.optString("stringValue") ?: "General",
                phone = custFields.optJSONObject("phone")?.optString("stringValue") ?: "",
                code = custFields.optJSONObject("code")?.optString("integerValue")
                    ?: custFields.optJSONObject("code")?.optString("stringValue") ?: "",
                address = custFields.optJSONObject("address")?.optString("stringValue") ?: ""
            )
        } else {
            Customer.DEFAULT
        }

        val userFields = fields.optJSONObject("user")?.optJSONObject("mapValue")?.optJSONObject("fields")
        val userSession = UserSession(
            uid = userFields?.optJSONObject("uid")?.optString("stringValue") ?: "",
            email = "",
            displayName = userFields?.optJSONObject("displayName")?.optString("stringValue") ?: "Cashier",
            idToken = ""
        )

        val items = mutableListOf<CartItem>()
        val prodArray = fields.optJSONObject("products")?.optJSONObject("arrayValue")?.optJSONArray("values")
        if (prodArray != null) {
            for (i in 0 until prodArray.length()) {
                val itemObj = prodArray.getJSONObject(i).optJSONObject("mapValue")?.optJSONObject("fields") ?: continue
                val itemName = itemObj.optJSONObject("item_name")?.optString("stringValue") ?: "Item"
                val codeNo = itemObj.optJSONObject("code_no")?.optString("integerValue")
                    ?: itemObj.optJSONObject("code_no")?.optString("stringValue") ?: ""
                val qty = itemObj.optJSONObject("sale_qty")?.optString("integerValue")?.toIntOrNull()
                    ?: itemObj.optJSONObject("sale_qty")?.optInt("integerValue", 1) ?: 1
                val unitPrice = itemObj.optJSONObject("qty_sale_price")?.optString("integerValue")?.toDoubleOrNull()
                    ?: itemObj.optJSONObject("qty_sale_price")?.optDouble("doubleValue", 0.0) ?: 0.0
                val itemDiscount = itemObj.optJSONObject("discount")?.optString("integerValue")?.toDoubleOrNull()
                    ?: itemObj.optJSONObject("discount")?.optDouble("doubleValue", 0.0) ?: 0.0
                val finalAmount = itemObj.optJSONObject("final_amount")?.optString("integerValue")?.toDoubleOrNull()
                    ?: itemObj.optJSONObject("final_amount")?.optDouble("doubleValue", (unitPrice * qty) - itemDiscount)
                    ?: ((unitPrice * qty) - itemDiscount)
                val unit = itemObj.optJSONObject("qty_sale_price_unit")?.optString("stringValue") ?: ""

                val product = Product(
                    id = itemObj.optJSONObject("id")?.optString("stringValue") ?: "",
                    codeNo = codeNo,
                    itemName = itemName,
                    qty = qty,
                    retailPrice = unitPrice,
                    categoryName = "",
                    barcode = "",
                    unit = unit
                )
                items.add(
                    CartItem(
                        product = product,
                        qty = qty,
                        unitPrice = unitPrice,
                        discount = itemDiscount,
                        finalAmount = finalAmount
                    )
                )
            }
        }

        return SaleRecord(
            voucherNo = voucherNo,
            saleDate = formattedDate,
            totalAmount = totalAmount,
            extraDiscount = extraDiscount,
            netAmount = netAmount,
            paid = paid,
            change = change,
            credit = credit,
            paymentType = paymentType,
            saleType = saleType,
            remark = remark,
            customer = customer,
            user = userSession,
            items = items
        )
    }

    suspend fun fetchSalesHistory(idToken: String, limit: Int = 100): Result<List<SaleRecord>> = withContext(Dispatchers.IO) {
        try {
            val list = mutableListOf<SaleRecord>()
            val url = "$firestoreBase/sales?pageSize=$limit"
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $idToken")
                .get()
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string().orEmpty()
            if (response.isSuccessful) {
                val root = JSONObject(body)
                val docs = root.optJSONArray("documents")
                if (docs != null) {
                    for (i in 0 until docs.length()) {
                        val doc = docs.getJSONObject(i)
                        parseSaleRecordDocument(doc)?.let { list.add(it) }
                    }
                }
            } else {
                Log.w(TAG, "fetchSalesHistory unsuccessful: ${response.code} $body")
            }
            val parseSdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            list.sortByDescending {
                try {
                    parseSdf.parse(it.saleDate)?.time ?: 0L
                } catch (_: Exception) {
                    0L
                }
            }
            Result.success(list)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching sales history", e)
            Result.failure(e)
        }
    }
}
