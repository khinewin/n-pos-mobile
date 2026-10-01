package com.npos.app

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.npos.app.data.FirebaseConfigManager
import com.npos.app.data.FirebaseService
import com.npos.app.data.model.Customer
import com.npos.app.data.model.DeviceViewMode
import com.npos.app.data.model.Product
import com.npos.app.data.model.SaleRecord
import com.npos.app.data.model.UserSalesSummary
import com.npos.app.data.model.UserSession
import com.npos.app.pos.BarcodeScannerManager
import com.npos.app.pos.CustomerDisplayManager
import com.npos.app.pos.ThermalPrinterManager
import com.npos.app.ui.CustomerDisplayPreviewDialog
import com.npos.app.ui.PrinterSettingsDialog
import com.npos.app.ui.customers.CustomersScreen
import com.npos.app.ui.dashboard.DashboardScreen
import com.npos.app.ui.login.LoginScreen
import com.npos.app.ui.navigation.PosScreen
import com.npos.app.ui.navigation.PosSidebarContent
import com.npos.app.ui.sales.SalesScreen
import com.npos.app.ui.settings.ViewModeDialog
import com.npos.app.ui.splash.SplashScreen
import com.npos.app.ui.theme.NPOSTheme
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "NPosMainActivity"
        private const val PREFS_AUTH = "npos_auth_prefs"
        private const val KEY_UID = "auth_uid"
        private const val KEY_EMAIL = "auth_email"
        private const val KEY_NAME = "auth_name"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_VIEW_MODE = "pref_device_view_mode"
    }

    private lateinit var firebaseService: FirebaseService
    private lateinit var printerManager: ThermalPrinterManager
    private lateinit var customerDisplayManager: CustomerDisplayManager
    private lateinit var barcodeScannerManager: BarcodeScannerManager
    private lateinit var authPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val configManager = FirebaseConfigManager(this)
        val initialConfig = configManager.getConfig()
        firebaseService = FirebaseService().apply {
            updateConfig(initialConfig.projectId, initialConfig.apiKey)
        }
        printerManager = ThermalPrinterManager(this)
        customerDisplayManager = CustomerDisplayManager(this)
        barcodeScannerManager = BarcodeScannerManager(this)
        authPrefs = getSharedPreferences(PREFS_AUTH, Context.MODE_PRIVATE)

        val savedSession = getSavedUserSession()
        val savedViewMode = getSavedViewMode()

        setContent {
            NPOSTheme {
                MainAppHost(
                    initialSession = savedSession,
                    initialViewMode = savedViewMode,
                    firebaseService = firebaseService,
                    printerManager = printerManager,
                    customerDisplayManager = customerDisplayManager,
                    barcodeScannerManager = barcodeScannerManager,
                    onSaveSession = { session -> saveUserSession(session) },
                    onClearSession = { clearUserSession() },
                    onSaveViewMode = { mode -> saveViewMode(mode) }
                )
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (::barcodeScannerManager.isInitialized && barcodeScannerManager.onDispatchKeyEvent(event)) {
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    private fun getSavedViewMode(): DeviceViewMode {
        val modeStr = authPrefs.getString(KEY_VIEW_MODE, DeviceViewMode.AUTO.name) ?: DeviceViewMode.AUTO.name
        return try {
            DeviceViewMode.valueOf(modeStr)
        } catch (e: Exception) {
            DeviceViewMode.AUTO
        }
    }

    private fun saveViewMode(mode: DeviceViewMode) {
        authPrefs.edit().putString(KEY_VIEW_MODE, mode.name).apply()
    }

    private fun getSavedUserSession(): UserSession? {
        val uid = authPrefs.getString(KEY_UID, null) ?: return null
        val email = authPrefs.getString(KEY_EMAIL, "n1@web.app") ?: "n1@web.app"
        val name = authPrefs.getString(KEY_NAME, "N1 Cashier") ?: "N1 Cashier"
        val token = authPrefs.getString(KEY_TOKEN, null) ?: return null
        return UserSession(uid, email, name, token)
    }

    private fun saveUserSession(session: UserSession) {
        authPrefs.edit()
            .putString(KEY_UID, session.uid)
            .putString(KEY_EMAIL, session.email)
            .putString(KEY_NAME, session.displayName)
            .putString(KEY_TOKEN, session.idToken)
            .apply()
    }

    private fun clearUserSession() {
        authPrefs.edit().clear().apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        customerDisplayManager.release()
        barcodeScannerManager.release()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppHost(
    initialSession: UserSession?,
    initialViewMode: DeviceViewMode = DeviceViewMode.AUTO,
    firebaseService: FirebaseService,
    printerManager: ThermalPrinterManager,
    customerDisplayManager: CustomerDisplayManager,
    barcodeScannerManager: BarcodeScannerManager,
    onSaveSession: (UserSession) -> Unit,
    onClearSession: () -> Unit,
    onSaveViewMode: (DeviceViewMode) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val configManager = remember { FirebaseConfigManager(context) }
    var firebaseConfig by remember { mutableStateOf(configManager.getConfig()) }

    var showSplash by remember { mutableStateOf(true) }
    var currentUser by remember { mutableStateOf(initialSession) }
    var currentScreen by remember { mutableStateOf(PosScreen.SALES) }
    var currentViewMode by remember { mutableStateOf(initialViewMode) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var isLoggingIn by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var customers by remember { mutableStateOf<List<Customer>>(emptyList()) }
    var categories by remember { mutableStateOf<List<String>>(listOf("All")) }
    var totalProductsCount by remember { mutableIntStateOf(0) }
    var displayLimit by remember { mutableIntStateOf(50) }
    var isLoadingData by remember { mutableStateOf(false) }
    var isSavingSale by remember { mutableStateOf(false) }
    var isSavingCustomer by remember { mutableStateOf(false) }
    var userSalesSummary by remember { mutableStateOf(UserSalesSummary()) }
    var isLoadingSummary by remember { mutableStateOf(false) }

    var showPrinterDialog by remember { mutableStateOf(false) }
    var showCustomerDialog by remember { mutableStateOf(false) }
    var showViewModeDialog by remember { mutableStateOf(false) }

    val isSecondaryScreenActive by customerDisplayManager.isSecondaryDisplayConnected.collectAsState()
    val lastCustomerData by customerDisplayManager.lastData.collectAsState()
    val selectedPrinter by printerManager.selectedDevice.collectAsState()

    // Request Bluetooth permissions on launch
    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    fun checkAndRequestBluetooth() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            }
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
        if (permissions.isNotEmpty()) {
            bluetoothPermissionLauncher.launch(permissions.toTypedArray())
        }
    }

    LaunchedEffect(Unit) {
        checkAndRequestBluetooth()
    }

    fun loadFirebaseData(token: String, limit: Int = displayLimit) {
        isLoadingData = true
        scope.launch {
            // 1. Fetch total products count with aggregation
            firebaseService.fetchTotalProductsCount(token).onSuccess {
                totalProductsCount = it
            }

            // 2. Fetch categories from Firestore 'category' collection
            firebaseService.fetchCategories(token).onSuccess {
                categories = listOf("All") + it
            }

            // 3. Fetch products with limit
            val prodResult = firebaseService.fetchProducts(token, limit)
            prodResult.onSuccess {
                products = it
                Log.d("NPOS", "Loaded ${it.size} products from Firebase Firestore")
            }.onFailure { err ->
                Log.e("NPOS", "Failed to load products: ${err.message}")
            }

            val custResult = firebaseService.fetchCustomers(token)
            custResult.onSuccess {
                customers = it
            }

            isLoadingData = false
        }
    }

    fun loadUserSalesSummary(token: String, user: UserSession) {
        isLoadingSummary = true
        scope.launch {
            val res = firebaseService.fetchUserSalesSummary(token, user)
            isLoadingSummary = false
            res.onSuccess {
                userSalesSummary = it
            }
        }
    }

    // Load initial data if logged in
    LaunchedEffect(currentUser) {
        currentUser?.let {
            loadFirebaseData(it.idToken, displayLimit)
            loadUserSalesSummary(it.idToken, it)
        }
    }

    // Direct Silent Print helper
    fun executeSilentPrint(jsonData: String) {
        if (!printerManager.isPrinterConfigured()) {
            Toast.makeText(context, "Please select Bluetooth printer in settings", Toast.LENGTH_SHORT).show()
            showPrinterDialog = true
            return
        }
        Toast.makeText(context, "Printing silently...", Toast.LENGTH_SHORT).show()
        scope.launch {
            val result = printerManager.printReceipt(jsonData)
            result.onSuccess {
                Toast.makeText(context, "Printed successfully", Toast.LENGTH_SHORT).show()
            }.onFailure { err ->
                Toast.makeText(context, "Print error: ${err.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Show animated Splash Screen first
    if (showSplash) {
        SplashScreen(
            onSplashFinished = { showSplash = false }
        )
    } else if (currentUser == null) {
        LoginScreen(
            isLoading = isLoggingIn,
            errorMessage = loginError,
            currentFirebaseConfig = firebaseConfig,
            onSaveFirebaseConfig = { newProj, newKey ->
                val saved = configManager.saveConfig(newProj, newKey)
                firebaseService.updateConfig(saved.projectId, saved.apiKey)
                firebaseConfig = saved
                Toast.makeText(context, if (saved.isConfigured) "Connected: ${saved.projectId}" else "Database settings saved", Toast.LENGTH_SHORT).show()
            },
            onLoginSubmit = { email, password ->
                isLoggingIn = true
                loginError = null
                scope.launch {
                    val result = firebaseService.login(email, password)
                    result.onSuccess { session ->
                        isLoggingIn = false
                        currentUser = session
                        onSaveSession(session)
                        Toast.makeText(context, "Welcome, ${session.displayName}!", Toast.LENGTH_SHORT).show()
                    }.onFailure { err ->
                        isLoggingIn = false
                        loginError = err.message ?: "Authentication failed"
                    }
                }
            }
        )
    } else {
        // Authenticated: Wrap with Sidebar Drawer and Top App Bar
        val user = currentUser!!

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    PosSidebarContent(
                        currentScreen = currentScreen,
                        user = user,
                        isSecondaryDisplayActive = isSecondaryScreenActive,
                        isPrinterConnected = selectedPrinter != null,
                        printerName = printerManager.getPrinterDisplayName(),
                        currentViewMode = currentViewMode,
                        onSelectScreen = { screen ->
                            currentScreen = screen
                            scope.launch { drawerState.close() }
                        },
                        onOpenPrinterSettings = {
                            showPrinterDialog = true
                            scope.launch { drawerState.close() }
                        },
                        onOpenCustomerDisplay = {
                            showCustomerDialog = true
                            scope.launch { drawerState.close() }
                        },
                        onOpenViewModeSettings = {
                            showViewModeDialog = true
                            scope.launch { drawerState.close() }
                        },
                        onLogout = {
                            onClearSession()
                            currentUser = null
                            products = emptyList()
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                            navigationIcon = {
                                IconButton(
                                    onClick = { scope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("sidebar_menu_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Menu,
                                        contentDescription = "Menu Sidebar"
                                    )
                                }
                            },
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_pos_logo),
                                                contentDescription = "Logo",
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = currentScreen.title,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            actions = {
                                // Clean TopAppBar - Actions accessible via Sidebar and Dashboard
                            }
                        )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        PosScreen.SALES -> {
                            SalesScreen(
                                user = user,
                                products = products,
                                customers = customers,
                                categories = categories,
                                totalProductsCount = totalProductsCount,
                                currentLimit = displayLimit,
                                onLimitChanged = { newLimit ->
                                    displayLimit = newLimit
                                    loadFirebaseData(user.idToken, newLimit)
                                },
                                isLoadingProducts = isLoadingData,
                                isSavingSale = isSavingSale,
                                autoPrintDefault = true,
                                autoScannedBarcodeFlow = barcodeScannerManager.scannedCodeFlow,
                                onSearchCloud = { query, category, limit ->
                                    isLoadingData = true
                                    scope.launch {
                                        val result = firebaseService.searchProducts(user.idToken, query, category, limit)
                                        isLoadingData = false
                                        result.onSuccess {
                                            products = it
                                        }.onFailure { err ->
                                            Toast.makeText(context, "Search failed: ${err.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onCustomerDataUpdated = { jsonPayload ->
                                    customerDisplayManager.updateCustomerData(jsonPayload)
                                },
                                onSilentPrintDirect = { jsonReceipt ->
                                    executeSilentPrint(jsonReceipt)
                                },
                                onCheckout = { saleRecord, autoPrint ->
                                    isSavingSale = true
                                    scope.launch {
                                        val result = firebaseService.saveSaleRecord(user.idToken, saleRecord)
                                        isSavingSale = false
                                        result.onSuccess { voucherId ->
                                            Toast.makeText(context, "Sale saved: $voucherId", Toast.LENGTH_SHORT).show()
                                            userSalesSummary = userSalesSummary.copy(
                                                todayAmount = userSalesSummary.todayAmount + saleRecord.netAmount,
                                                thisMonthAmount = userSalesSummary.thisMonthAmount + saleRecord.netAmount,
                                                todayCount = userSalesSummary.todayCount + 1,
                                                thisMonthCount = userSalesSummary.thisMonthCount + 1
                                            )
                                            if (autoPrint) {
                                                val receiptPayload = buildSaleReceiptJson(saleRecord)
                                                executeSilentPrint(receiptPayload)
                                            }
                                        }.onFailure { err ->
                                            Toast.makeText(context, "Save sale error: ${err.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                currentViewMode = currentViewMode
                            )
                        }

                        PosScreen.DASHBOARD -> {
                            DashboardScreen(
                                user = user,
                                salesSummary = userSalesSummary,
                                isLoadingSummary = isLoadingSummary,
                                isSecondaryDisplayActive = isSecondaryScreenActive,
                                isPrinterConnected = selectedPrinter != null,
                                printerName = selectedPrinter?.name ?: "Thermal Printer",
                                currentViewMode = currentViewMode,
                                onRefreshSummary = { loadUserSalesSummary(user.idToken, user) },
                                onGoToSales = { currentScreen = PosScreen.SALES },
                                onOpenPrinterSettings = { showPrinterDialog = true },
                                onOpenCustomerDisplay = { showCustomerDialog = true },
                                onOpenViewModeSettings = { showViewModeDialog = true }
                            )
                        }

                        PosScreen.CUSTOMERS -> {
                            CustomersScreen(
                                customers = customers,
                                isSaving = isSavingCustomer,
                                onAddCustomer = { newCust ->
                                    isSavingCustomer = true
                                    scope.launch {
                                        val res = firebaseService.createCustomer(user.idToken, newCust, user)
                                        isSavingCustomer = false
                                        res.onSuccess { created ->
                                            customers = customers + created
                                            Toast.makeText(context, "Added: ${created.name}", Toast.LENGTH_SHORT).show()
                                        }.onFailure { err ->
                                            Toast.makeText(context, "Failed to add customer: ${err.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onUpdateCustomer = { updatedCust ->
                                    isSavingCustomer = true
                                    scope.launch {
                                        val res = firebaseService.updateCustomer(user.idToken, updatedCust, user)
                                        isSavingCustomer = false
                                        res.onSuccess {
                                            customers = customers.map { if (it.id == updatedCust.id) updatedCust else it }
                                            Toast.makeText(context, "Updated: ${updatedCust.name}", Toast.LENGTH_SHORT).show()
                                        }.onFailure { err ->
                                            Toast.makeText(context, "Failed to update: ${err.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onDeleteCustomer = { custToDelete ->
                                    isSavingCustomer = true
                                    scope.launch {
                                        val res = firebaseService.deleteCustomer(user.idToken, custToDelete.id)
                                        isSavingCustomer = false
                                        res.onSuccess {
                                            customers = customers.filter { it.id != custToDelete.id }
                                            Toast.makeText(context, "Deleted: ${custToDelete.name}", Toast.LENGTH_SHORT).show()
                                        }.onFailure { err ->
                                            Toast.makeText(context, "Failed to delete: ${err.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                onRefresh = {
                                    scope.launch {
                                        val res = firebaseService.fetchCustomers(user.idToken)
                                        res.onSuccess {
                                            customers = it
                                            Toast.makeText(context, "Customers refreshed (${it.size})", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Printer settings dialog
    if (showPrinterDialog) {
        PrinterSettingsDialog(
            printerManager = printerManager,
            onTestPrint = {
                scope.launch {
                    val result = printerManager.printTestReceipt()
                    result.onSuccess {
                        Toast.makeText(context, "Test print successful", Toast.LENGTH_SHORT).show()
                    }.onFailure { e ->
                        Toast.makeText(context, "Test print error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            onRequestBluetoothPermission = {
                checkAndRequestBluetooth()
            },
            onDismiss = { showPrinterDialog = false }
        )
    }

    // Customer display preview dialog
    if (showCustomerDialog) {
        CustomerDisplayPreviewDialog(
            jsonData = lastCustomerData,
            onDismiss = { showCustomerDialog = false }
        )
    }

    // Device View Mode Dialog (Phone / Tablet / Auto)
    if (showViewModeDialog) {
        ViewModeDialog(
            currentMode = currentViewMode,
            onSelectMode = { newMode ->
                currentViewMode = newMode
                onSaveViewMode(newMode)
                showViewModeDialog = false
                Toast.makeText(context, "${newMode.titleEn} (${newMode.titleMm}) ရွေးချယ်ပြီးပါပြီ", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showViewModeDialog = false }
        )
    }
}

private fun buildSaleReceiptJson(sale: SaleRecord): String {
    val root = JSONObject().apply {
        put("voucher_no", sale.voucherNo)
        put("sale_date", sale.saleDate)
        put("total_amount", sale.totalAmount)
        put("extra_discount", sale.extraDiscount)
        put("net_amount", sale.netAmount)
        put("paid", sale.paid)
        put("change", sale.change)
        put("customer", JSONObject().apply {
            put("name", sale.customer.name)
            put("phone", sale.customer.phone)
        })
        put("user", JSONObject().apply {
            put("displayName", sale.user.displayName)
        })
        val itemsArr = JSONArray()
        sale.items.forEach { item ->
            itemsArr.put(JSONObject().apply {
                put("item_name", item.product.itemName)
                put("sale_qty", item.qty)
                put("qty_sale_price", item.unitPrice)
                put("discount", item.discount)
                put("final_amount", item.finalAmount)
            })
        }
        put("saleItems", itemsArr)
    }
    return root.toString()
}
