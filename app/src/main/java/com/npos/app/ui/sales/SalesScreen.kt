package com.npos.app.ui.sales

import android.widget.Toast
import com.npos.app.data.AppSettingsManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.npos.app.data.model.CartItem
import com.npos.app.data.model.Customer
import com.npos.app.data.model.DeviceViewMode
import com.npos.app.data.model.Product
import com.npos.app.data.model.SaleRecord
import com.npos.app.data.model.UserSession
import com.npos.app.ui.theme.LocalStrings
import kotlinx.coroutines.flow.SharedFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SalesScreen(
    user: UserSession,
    products: List<Product>,
    customers: List<Customer>,
    categories: List<String>,
    totalProductsCount: Int,
    currentLimit: Int,
    onLimitChanged: (Int) -> Unit,
    isLoadingProducts: Boolean,
    isSavingSale: Boolean,
    autoPrintDefault: Boolean,
    onAutoPrintToggle: ((Boolean) -> Unit)? = null,
    onRefreshData: (() -> Unit)? = null,
    autoScannedBarcodeFlow: SharedFlow<String>? = null,
    onSearchCloud: (String, String, Int) -> Unit,
    onCheckout: (SaleRecord, Boolean) -> Unit,
    onSilentPrintDirect: (String) -> Unit,
    onCustomerDataUpdated: (String) -> Unit,
    currentViewMode: DeviceViewMode = DeviceViewMode.AUTO,
    isScannerConnected: Boolean = false,
    scannerDeviceName: String? = null,
    onRefreshScannerCheck: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val s = LocalStrings.current
    val settingsManager = remember { AppSettingsManager(context) }
    val cartItems = remember { mutableStateListOf<CartItem>() }
    var selectedCustomer by remember { mutableStateOf(customers.firstOrNull { it.code == "123456789" } ?: Customer.DEFAULT) }
    var searchQuery by remember { mutableStateOf("") }
    var barcodeInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val extraDiscount = 0.0
    var paidAmount by remember { mutableDoubleStateOf(0.0) }
    var autoPrint by remember { mutableStateOf(autoPrintDefault) }
    var voucherNo by remember { mutableStateOf(generateVoucherNo(user.email)) }

    // Update customer if list loaded
    LaunchedEffect(customers) {
        if (selectedCustomer.code == "123456789" && customers.isNotEmpty()) {
            selectedCustomer = customers.firstOrNull { it.code == "123456789" } ?: Customer.DEFAULT
        }
    }

    // Dynamic instant filtering by selectedCategory, excluding stock <= 0 and no sale price
    val displayedProducts by remember(products, selectedCategory) {
        derivedStateOf {
            val validProducts = products.filter { it.qty > 0 && it.retailPrice > 0.0 }
            if (selectedCategory.isBlank() || selectedCategory == "All") {
                validProducts
            } else {
                validProducts.filter { p ->
                    p.categoryName.equals(selectedCategory, ignoreCase = true) ||
                    p.itemName.contains(selectedCategory, ignoreCase = true)
                }
            }
        }
    }

    // Calculations
    val subtotal by remember {
        derivedStateOf { cartItems.sumOf { it.finalAmount } }
    }
    val netAmount by remember {
        derivedStateOf { subtotal }
    }
    val changeAmount by remember {
        derivedStateOf { if (paidAmount > netAmount) paidAmount - netAmount else 0.0 }
    }
    val creditAmount by remember {
        derivedStateOf { if (netAmount > paidAmount) netAmount - paidAmount else 0.0 }
    }

    // Sync to Customer Display whenever cart changes
    LaunchedEffect(cartItems.toList(), netAmount, extraDiscount, paidAmount, changeAmount) {
        val jsonPayload = buildCustomerDisplayJson(
            cartItems = cartItems,
            netAmount = netAmount,
            extraDiscount = extraDiscount,
            paid = paidAmount,
            change = changeAmount
        )
        onCustomerDataUpdated(jsonPayload)
    }

    fun addItemToCart(product: Product) {
        if (product.qty <= 0) {
            Toast.makeText(context, "\"${product.itemName}\" ပစ္စည်းလက်ကျန်မရှိပါ (Out of stock)", Toast.LENGTH_SHORT).show()
            return
        }

        val existingIndex = cartItems.indexOfFirst { it.product.id == product.id }
        if (existingIndex >= 0) {
            val current = cartItems[existingIndex]
            if (current.qty >= product.qty) {
                Toast.makeText(context, "\"${product.itemName}\" လက်ကျန် (${product.qty} ${product.unit}) ထက် ပို၍ မထည့်နိုင်ပါ", Toast.LENGTH_SHORT).show()
                return
            }
            val newQty = current.qty + 1
            val maxDisc = current.unitPrice * newQty
            val cappedDisc = current.discount.coerceAtMost(maxDisc)
            cartItems[existingIndex] = current.copy(
                qty = newQty,
                discount = cappedDisc,
                finalAmount = (current.unitPrice * newQty) - cappedDisc
            )
        } else {
            cartItems.add(
                CartItem(
                    product = product,
                    qty = 1,
                    unitPrice = product.retailPrice,
                    discount = 0.0,
                    finalAmount = product.retailPrice
                )
            )
        }
        if (paidAmount < netAmount) {
            paidAmount = netAmount
        }
        Toast.makeText(context, "Added: ${product.itemName}", Toast.LENGTH_SHORT).show()
    }

    fun handleBarcodeScan(barcode: String) {
        val cleanBarcode = barcode.trim()
        if (cleanBarcode.isEmpty()) return
        keyboardController?.hide()
        focusManager.clearFocus()
        val match = products.find {
            (it.barcode.equals(cleanBarcode, ignoreCase = true) ||
                    it.codeNo.equals(cleanBarcode, ignoreCase = true)) &&
                    it.qty > 0 && it.retailPrice > 0.0
        }
        if (match != null) {
            addItemToCart(match)
            barcodeInput = ""
        } else {
            val invalidMatch = products.find {
                it.barcode.equals(cleanBarcode, ignoreCase = true) ||
                        it.codeNo.equals(cleanBarcode, ignoreCase = true)
            }
            if (invalidMatch != null) {
                if (invalidMatch.qty <= 0) {
                    Toast.makeText(context, "⚠️ \"${invalidMatch.itemName}\" ပစ္စည်းလက်ကျန် (Stock) မရှိပါ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "⚠️ \"${invalidMatch.itemName}\" ရောင်းစျေးနှုန်း မသတ်မှတ်ရသေးပါ", Toast.LENGTH_SHORT).show()
                }
                barcodeInput = ""
            } else {
                // Trigger Cloud direct search for barcode
                onSearchCloud(cleanBarcode, "All", currentLimit)
                Toast.makeText(context, "Searching Cloud for barcode: $cleanBarcode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Auto-detect barcode from hardware scanner or Sunmi broadcast flow
    LaunchedEffect(autoScannedBarcodeFlow) {
        autoScannedBarcodeFlow?.collect { scannedCode ->
            handleBarcodeScan(scannedCode)
        }
    }

    // If new products load after barcode search, auto-add if exact match
    LaunchedEffect(products) {
        if (barcodeInput.isNotEmpty()) {
            val match = products.find {
                (it.barcode.equals(barcodeInput, ignoreCase = true) ||
                        it.codeNo.equals(barcodeInput, ignoreCase = true)) &&
                        it.qty > 0 && it.retailPrice > 0.0
            }
            if (match != null) {
                addItemToCart(match)
                barcodeInput = ""
            }
        }
    }

    fun triggerCloudSearch() {
        keyboardController?.hide()
        focusManager.clearFocus()
        onSearchCloud(searchQuery.trim(), selectedCategory, currentLimit)
    }

    fun triggerFullRefresh() {
        keyboardController?.hide()
        focusManager.clearFocus()
        searchQuery = ""
        selectedCategory = "All"
        if (onRefreshData != null) {
            onRefreshData()
        } else {
            onSearchCloud("", "All", currentLimit)
        }
    }

    fun clearCart() {
        cartItems.clear()
        paidAmount = 0.0
        voucherNo = generateVoucherNo(user.email)
    }

    fun processCheckout(): Boolean {
        if (cartItems.isEmpty()) {
            Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedCustomer.code == "123456789" && creditAmount > 0) {
            Toast.makeText(context, "Default customer cannot sale with credit (အကြွေးမရပါ)", Toast.LENGTH_LONG).show()
            return false
        }

        val saleDateStr = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
        val record = SaleRecord(
            voucherNo = voucherNo,
            saleDate = saleDateStr,
            totalAmount = subtotal,
            extraDiscount = extraDiscount,
            netAmount = netAmount,
            paid = paidAmount,
            change = changeAmount,
            credit = creditAmount,
            paymentType = if (creditAmount > 0) "Credit" else "Paid",
            saleType = "retail",
            remark = "",
            customer = selectedCustomer,
            user = user,
            items = cartItems.toList()
        )

        onCheckout(record, autoPrint)
        clearCart()
        return true
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = when (currentViewMode) {
            DeviceViewMode.PHONE -> false
            DeviceViewMode.TABLET -> true
            DeviceViewMode.AUTO -> maxWidth > 840.dp
        }

        if (isWideScreen) {
            // Dual Pane Layout (Products Catalog on Left, Cart on Right)
            Row(modifier = Modifier.fillMaxSize()) {
                // Products Catalog Pane
                Box(
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight()
                        .padding(12.dp)
                ) {
                    ProductsCatalogPane(
                        products = displayedProducts,
                        isLoading = isLoadingProducts,
                        categories = categories,
                        selectedCategory = selectedCategory,
                        totalProductsCount = totalProductsCount,
                        currentLimit = currentLimit,
                        onLimitChanged = onLimitChanged,
                        searchQuery = searchQuery,
                        barcodeInput = barcodeInput,
                        onSearchChange = { searchQuery = it },
                        onBarcodeChange = { barcodeInput = it },
                        onSearchSubmit = { triggerCloudSearch() },
                        onBarcodeSubmit = { handleBarcodeScan(it) },
                        onSelectCategory = { cat ->
                            selectedCategory = cat
                            onSearchCloud(searchQuery.trim(), cat, currentLimit)
                        },
                        onProductClick = { addItemToCart(it) },
                        onRefreshAll = { triggerFullRefresh() },
                        isScannerConnected = isScannerConnected,
                        scannerDeviceName = scannerDeviceName,
                        onRefreshScannerCheck = onRefreshScannerCheck
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )

                // Cart Pane
                Box(
                    modifier = Modifier
                        .weight(0.42f)
                        .fillMaxHeight()
                        .padding(12.dp)
                ) {
                    CartPane(
                        voucherNo = voucherNo,
                        cashierName = user.displayName,
                        customers = customers,
                        selectedCustomer = selectedCustomer,
                        cartItems = cartItems,
                        subtotal = subtotal,
                        extraDiscount = extraDiscount,
                        netAmount = netAmount,
                        paidAmount = paidAmount,
                        changeAmount = changeAmount,
                        creditAmount = creditAmount,
                        autoPrint = autoPrint,
                        isSaving = isSavingSale,
                        onCustomerChange = { selectedCustomer = it },
                        onDiscountChange = {},
                        onPaidChange = { paidAmount = it },
                        onAutoPrintToggle = { enabled ->
                            autoPrint = enabled
                            settingsManager.setAutoPrint(enabled)
                            onAutoPrintToggle?.invoke(enabled)
                        },
                        onIncreaseQty = { item ->
                            val idx = cartItems.indexOf(item)
                            if (idx >= 0) {
                                val currentProd = products.find { it.id == item.product.id } ?: item.product
                                if (item.qty >= currentProd.qty) {
                                    Toast.makeText(context, "\"${item.product.itemName}\" လက်ကျန် (${currentProd.qty} ${currentProd.unit}) ထက် ပိုမရောင်းနိုင်ပါ", Toast.LENGTH_SHORT).show()
                                } else {
                                    val nq = item.qty + 1
                                    val maxDisc = item.unitPrice * nq
                                    val cappedDisc = item.discount.coerceAtMost(maxDisc)
                                    cartItems[idx] = item.copy(
                                        qty = nq,
                                        discount = cappedDisc,
                                        finalAmount = (item.unitPrice * nq) - cappedDisc
                                    )
                                }
                            }
                        },
                        onDecreaseQty = { item ->
                            val idx = cartItems.indexOf(item)
                            if (idx >= 0) {
                                if (item.qty > 1) {
                                    val nq = item.qty - 1
                                    val maxDisc = item.unitPrice * nq
                                    val cappedDisc = item.discount.coerceAtMost(maxDisc)
                                    cartItems[idx] = item.copy(
                                        qty = nq,
                                        discount = cappedDisc,
                                        finalAmount = (item.unitPrice * nq) - cappedDisc
                                    )
                                } else {
                                    cartItems.removeAt(idx)
                                }
                            }
                        },
                        onItemDiscountChange = { item, newDisc ->
                            val idx = cartItems.indexOf(item)
                            if (idx >= 0) {
                                val maxDisc = item.unitPrice * item.qty
                                val cappedDisc = newDisc.coerceIn(0.0, maxDisc)
                                cartItems[idx] = item.copy(
                                    discount = cappedDisc,
                                    finalAmount = (item.unitPrice * item.qty) - cappedDisc
                                )
                            }
                        },
                        onRemoveItem = { item -> cartItems.remove(item) },
                        onClearCart = { clearCart() },
                        onPrintDirect = {
                            val receiptJson = buildReceiptPayloadJson(
                                voucherNo = voucherNo,
                                cashier = user.displayName,
                                customer = selectedCustomer,
                                cartItems = cartItems,
                                totalAmount = subtotal,
                                discount = extraDiscount,
                                netAmount = netAmount,
                                paid = paidAmount,
                                change = changeAmount
                            )
                            onSilentPrintDirect(receiptJson)
                        },
                        onCheckout = { processCheckout() }
                    )
                }
            }
        } else {
            // Single Pane / Mobile Tabbed Layout
            var selectedTab by remember { mutableIntStateOf(0) }

            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(s.sales) },
                        icon = { Icon(Icons.Default.PointOfSale, contentDescription = s.sales) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            BadgedBox(
                                badge = {
                                    if (cartItems.isNotEmpty()) {
                                        Badge { Text("${cartItems.sumOf { it.qty }}") }
                                    }
                                }
                            ) {
                                Text("${s.cart} (${formatMoney(netAmount)} MMK)")
                            }
                        },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = s.cart) }
                    )
                }

                if (selectedTab == 0) {
                    ProductsCatalogPane(
                        products = displayedProducts,
                        isLoading = isLoadingProducts,
                        categories = categories,
                        selectedCategory = selectedCategory,
                        totalProductsCount = totalProductsCount,
                        currentLimit = currentLimit,
                        onLimitChanged = onLimitChanged,
                        searchQuery = searchQuery,
                        barcodeInput = barcodeInput,
                        onSearchChange = { searchQuery = it },
                        onBarcodeChange = { barcodeInput = it },
                        onSearchSubmit = { triggerCloudSearch() },
                        onBarcodeSubmit = { handleBarcodeScan(it) },
                        onSelectCategory = { cat ->
                            selectedCategory = cat
                            onSearchCloud(searchQuery.trim(), cat, currentLimit)
                        },
                        onProductClick = { addItemToCart(it) },
                        onRefreshAll = { triggerFullRefresh() },
                        isScannerConnected = isScannerConnected,
                        scannerDeviceName = scannerDeviceName,
                        onRefreshScannerCheck = onRefreshScannerCheck
                    )
                } else {
                    CartPane(
                        voucherNo = voucherNo,
                        cashierName = user.displayName,
                        customers = customers,
                        selectedCustomer = selectedCustomer,
                        cartItems = cartItems,
                        subtotal = subtotal,
                        extraDiscount = extraDiscount,
                        netAmount = netAmount,
                        paidAmount = paidAmount,
                        changeAmount = changeAmount,
                        creditAmount = creditAmount,
                        autoPrint = autoPrint,
                        isSaving = isSavingSale,
                        onCustomerChange = { selectedCustomer = it },
                        onDiscountChange = {},
                        onPaidChange = { paidAmount = it },
                        onAutoPrintToggle = { enabled ->
                            autoPrint = enabled
                            settingsManager.setAutoPrint(enabled)
                            onAutoPrintToggle?.invoke(enabled)
                        },
                        onIncreaseQty = { item ->
                            val idx = cartItems.indexOf(item)
                            if (idx >= 0) {
                                val currentProd = products.find { it.id == item.product.id } ?: item.product
                                if (item.qty >= currentProd.qty) {
                                    Toast.makeText(context, "\"${item.product.itemName}\" လက်ကျန် (${currentProd.qty} ${currentProd.unit}) ထက် ပိုမရောင်းနိုင်ပါ", Toast.LENGTH_SHORT).show()
                                } else {
                                    val nq = item.qty + 1
                                    val maxDisc = item.unitPrice * nq
                                    val cappedDisc = item.discount.coerceAtMost(maxDisc)
                                    cartItems[idx] = item.copy(
                                        qty = nq,
                                        discount = cappedDisc,
                                        finalAmount = (item.unitPrice * nq) - cappedDisc
                                    )
                                }
                            }
                        },
                        onDecreaseQty = { item ->
                            val idx = cartItems.indexOf(item)
                            if (idx >= 0) {
                                if (item.qty > 1) {
                                    val nq = item.qty - 1
                                    val maxDisc = item.unitPrice * nq
                                    val cappedDisc = item.discount.coerceAtMost(maxDisc)
                                    cartItems[idx] = item.copy(
                                        qty = nq,
                                        discount = cappedDisc,
                                        finalAmount = (item.unitPrice * nq) - cappedDisc
                                    )
                                } else {
                                    cartItems.removeAt(idx)
                                }
                            }
                        },
                        onItemDiscountChange = { item, newDisc ->
                            val idx = cartItems.indexOf(item)
                            if (idx >= 0) {
                                val maxDisc = item.unitPrice * item.qty
                                val cappedDisc = newDisc.coerceIn(0.0, maxDisc)
                                cartItems[idx] = item.copy(
                                    discount = cappedDisc,
                                    finalAmount = (item.unitPrice * item.qty) - cappedDisc
                                )
                            }
                        },
                        onRemoveItem = { item -> cartItems.remove(item) },
                        onClearCart = { clearCart() },
                        onPrintDirect = {
                            val receiptJson = buildReceiptPayloadJson(
                                voucherNo = voucherNo,
                                cashier = user.displayName,
                                customer = selectedCustomer,
                                cartItems = cartItems,
                                totalAmount = subtotal,
                                discount = extraDiscount,
                                netAmount = netAmount,
                                paid = paidAmount,
                                change = changeAmount
                            )
                            onSilentPrintDirect(receiptJson)
                        },
                        onCheckout = {
                            val success = processCheckout()
                            // Only return to products tab if checkout actually succeeded
                            if (success) {
                                selectedTab = 0
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ProductsCatalogPane(
    products: List<Product>,
    isLoading: Boolean,
    categories: List<String>,
    selectedCategory: String,
    totalProductsCount: Int,
    currentLimit: Int,
    onLimitChanged: (Int) -> Unit,
    searchQuery: String,
    barcodeInput: String,
    onSearchChange: (String) -> Unit,
    onBarcodeChange: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onBarcodeSubmit: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onRefreshAll: () -> Unit,
    isScannerConnected: Boolean = false,
    scannerDeviceName: String? = null,
    onRefreshScannerCheck: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
    ) {
        // High-End Search & Barcode Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Total Count & Display Limit Selector Row (Scrollable with 20, 50, 100, 200, 300, 400, 500, All)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cloud Live Count Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CloudDone,
                                contentDescription = "Cloud Count",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cloud စုစုပေါင်း: ${if (totalProductsCount > 0) "$totalProductsCount ခု" else "ရေတွက်နေသည်..."}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Display Limit Selector (ပြသလိုသော ပမာဏ: 20, 50, 100, 200, 300, 400, 500, All)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "ပြသရန်:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.DarkGray
                        )
                        val limitOptions = listOf(
                            20 to "20",
                            50 to "50",
                            100 to "100",
                            200 to "200",
                            300 to "300",
                            400 to "400",
                            500 to "500",
                            10000 to "All"
                        )
                        limitOptions.forEach { (limitOption, label) ->
                            val isSelected = if (limitOption >= 10000) {
                                currentLimit >= 10000 || (totalProductsCount > 0 && currentLimit >= totalProductsCount)
                            } else {
                                currentLimit == limitOption
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clickable { onLimitChanged(limitOption) }
                                    .testTag("limit_option_$label")
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Top Row: Search Input + "Search (ရှာမည်)" Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = { Text("Search product name") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    onSearchChange("")
                                    onSearchSubmit()
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("product_search_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            onSearchSubmit()
                        }),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            onSearchSubmit()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        modifier = Modifier
                            .size(56.dp)
                            .testTag("cloud_search_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(22.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Barcode Auto Scanner Bar with Live Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = barcodeInput,
                        onValueChange = { input ->
                            onBarcodeChange(input)
                            if (input.endsWith("\n") || input.endsWith("\r")) {
                                onBarcodeSubmit(input.trim())
                            }
                        },
                        placeholder = { Text("Scan / Enter Barcode") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = "Barcode",
                                tint = Color(0xFF198754)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("barcode_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            onBarcodeSubmit(barcodeInput)
                        }),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isScannerConnected) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isScannerConnected) Color(0xFF4CAF50) else Color(0xFFEF5350)
                        ),
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onRefreshScannerCheck?.invoke()
                                val msg = if (isScannerConnected) {
                                    val dev = if (!scannerDeviceName.isNullOrBlank()) " ($scannerDeviceName)" else ""
                                    "🟢 Barcode Scanner ချိတ်ဆက်ထားပါသည်$dev"
                                } else {
                                    "🔴 Scanner မချိတ်ထားပါ (USB သို့မဟုတ် Bluetooth ဖြင့် ချိတ်ဆက်နိုင်ပါသည်)"
                                }
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                            .testTag("barcode_scanner_status_indicator")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = if (isScannerConnected) "Scanner Connected" else "Scanner Disconnected",
                                    tint = if (isScannerConnected) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            if (isScannerConnected) Color(0xFF2E7D32) else Color(0xFFC62828),
                                            CircleShape
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips (Loaded directly from Firestore 'category' collection!)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = cat == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onSelectCategory(cat)
                    },
                    label = {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Results Status Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (searchQuery.isNotEmpty()) "Search results for \"$searchQuery\": ${products.size} items"
                else if (selectedCategory != "All") "Category \"$selectedCategory\": ${products.size} items"
                else if (currentLimit >= 10000) "Displaying: All ${products.size} of $totalProductsCount items"
                else "Displaying: ${products.size} of $totalProductsCount items (Limit: $currentLimit)",
                fontSize = 12.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onRefreshAll()
                    }
                    .padding(4.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Show All", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Product List with Pull to Refresh
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = {
                keyboardController?.hide()
                focusManager.clearFocus()
                onRefreshAll()
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (isLoading && products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Cloud မှ ပစ္စည်းများ ရှာဖွေနေပါသည်...", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            } else if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Inventory, contentDescription = "Empty", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No products found in Cloud",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "အမည် သို့မဟုတ် ဘားကုဒ် ရိုက်ထည့်၍ 'ရှာမည်' ခလုတ်ကို နှိပ်ပါ",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    onRefreshAll()
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("ပစ္စည်းအားလုံး ပြန်လည်ပြသမည်")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductItemRow(
                            product = product,
                            onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onProductClick(product)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductItemRow(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("product_item_${product.codeNo}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.itemName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Code: ${product.codeNo}",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Stock: ${product.qty} ${product.unit}",
                        fontSize = 11.sp,
                        color = if (product.qty > 5) Color(0xFF198754) else Color(0xFFDC3545),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${formatMoney(product.retailPrice)} MMK",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartPane(
    voucherNo: String,
    cashierName: String,
    customers: List<Customer>,
    selectedCustomer: Customer,
    cartItems: List<CartItem>,
    subtotal: Double,
    extraDiscount: Double,
    netAmount: Double,
    paidAmount: Double,
    changeAmount: Double,
    creditAmount: Double,
    autoPrint: Boolean,
    isSaving: Boolean,
    onCustomerChange: (Customer) -> Unit,
    onDiscountChange: (Double) -> Unit,
    onPaidChange: (Double) -> Unit,
    onAutoPrintToggle: (Boolean) -> Unit,
    onIncreaseQty: (CartItem) -> Unit,
    onDecreaseQty: (CartItem) -> Unit,
    onItemDiscountChange: (CartItem, Double) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onClearCart: () -> Unit,
    onPrintDirect: () -> Unit,
    onCheckout: () -> Unit
) {
    var showCustomerModal by remember { mutableStateOf(false) }
    var discountDialogItem by remember { mutableStateOf<CartItem?>(null) }

    // Modal dialog to search and pick customer
    if (showCustomerModal) {
        CustomerSelectionModal(
            customers = customers,
            selectedCustomer = selectedCustomer,
            onDismiss = { showCustomerModal = false },
            onSelectCustomer = { customer ->
                onCustomerChange(customer)
                showCustomerModal = false
            }
        )
    }

    // Dialog for individual item discount
    discountDialogItem?.let { item ->
        ItemDiscountDialog(
            item = item,
            onDismiss = { discountDialogItem = null },
            onApplyDiscount = { discAmount ->
                onItemDiscountChange(item, discAmount)
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Voucher header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Invoice: $voucherNo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "Cashier: $cashierName", fontSize = 11.sp, color = Color.Gray)
                }

                // Auto Print Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Auto Print", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = autoPrint,
                        onCheckedChange = onAutoPrintToggle,
                        modifier = Modifier.testTag("auto_print_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Customer selector chip (Click to open Customer Search Modal)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCustomerModal = true }
                    .testTag("select_customer_button"),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Customer",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = selectedCustomer.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (selectedCustomer.phone.isNotBlank() && selectedCustomer.code != "123456789") {
                                Text(
                                    text = selectedCustomer.phone,
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ရွေးရန်",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Warning if default customer and credit sale attempted
            if (selectedCustomer.code == "123456789" && creditAmount > 0) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFEBEE)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Default customer cannot sale with credit (အကြွေးမရပါ)",
                            fontSize = 11.sp,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Cart Items List
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Empty Cart",
                            tint = Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Cart is empty", color = Color.Gray, fontSize = 14.sp)
                        Text("Scan or select products to add", color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(cartItems) { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${index + 1}.", fontSize = 12.sp, modifier = Modifier.width(20.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.product.itemName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
                                Text(
                                    text = "${formatMoney(item.unitPrice)} MMK x ${item.qty}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )

                                // Item Individual Discount Button / Badge
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    if (item.discount > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFFFEBEE),
                                            modifier = Modifier.clickable { discountDialogItem = item }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.LocalOffer,
                                                    contentDescription = "Discount",
                                                    tint = Color(0xFFDC3545),
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "Disc: -${formatMoney(item.discount)}",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFDC3545)
                                                )
                                            }
                                        }
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFE9ECEF),
                                            modifier = Modifier.clickable { discountDialogItem = item }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.LocalOffer,
                                                    contentDescription = "Add Discount",
                                                    tint = Color.DarkGray,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "+ Disc",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color.DarkGray
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Qty controls
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onDecreaseQty(item) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = "${item.qty}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                IconButton(
                                    onClick = { onIncreaseQty(item) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "${formatMoney(item.finalAmount)} MMK",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            IconButton(
                                onClick = { onRemoveItem(item) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC3545), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Totals and Payment
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = "${formatMoney(netAmount)} MMK",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Paid and Change inputs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = if (paidAmount == 0.0) "" else paidAmount.toInt().toString(),
                            onValueChange = { onPaidChange(it.toDoubleOrNull() ?: 0.0) },
                            label = { Text("Paid MMK") },
                            modifier = Modifier.weight(0.5f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Column(modifier = Modifier.weight(0.5f)) {
                            if (creditAmount > 0) {
                                Text(text = "Credit (ကျန်ငွေ):", fontSize = 12.sp, color = Color(0xFFDC3545), fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${formatMoney(creditAmount)} MMK",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFFDC3545)
                                )
                            } else {
                                Text(text = "Change (ပြန်အမ်း):", fontSize = 12.sp)
                                Text(
                                    text = "${formatMoney(changeAmount)} MMK",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF198754)
                                )
                            }
                        }
                    }

                    // Quick cash presets
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(netAmount, 5000.0, 10000.0, 20000.0, 50000.0, 100000.0).distinct().forEach { amt ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onPaidChange(amt) },
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = if (amt == netAmount) "Exact" else "${amt.toInt() / 1000}K",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons (Print, Clear, Checkout)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClearCart,
                    enabled = cartItems.isNotEmpty(),
                    modifier = Modifier.weight(0.25f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Clear", fontSize = 12.sp)
                }

                Button(
                    onClick = onPrintDirect,
                    enabled = cartItems.isNotEmpty(),
                    modifier = Modifier
                        .weight(0.35f)
                        .testTag("direct_print_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF495057))
                ) {
                    Icon(Icons.Default.Print, contentDescription = "Print", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Print", fontSize = 13.sp)
                }

                Button(
                    onClick = onCheckout,
                    enabled = cartItems.isNotEmpty() && !isSaving,
                    modifier = Modifier
                        .weight(0.4f)
                        .testTag("checkout_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                    } else {
                        Text("Pay & Save", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

/**
 * Modal Dialog for searching and selecting customers in the Sales Cart.
 */
@Composable
fun CustomerSelectionModal(
    customers: List<Customer>,
    selectedCustomer: Customer,
    onDismiss: () -> Unit,
    onSelectCustomer: (Customer) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCustomers = remember(searchQuery, customers) {
        if (searchQuery.isBlank()) {
            customers
        } else {
            val q = searchQuery.trim().lowercase()
            customers.filter {
                it.name.lowercase().contains(q) ||
                it.phone.lowercase().contains(q) ||
                it.code.lowercase().contains(q) ||
                it.address.lowercase().contains(q)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 600.dp)
                .padding(vertical = 16.dp)
                .testTag("customer_selection_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ဖောက်သည် ရွေးချယ်ပါ (Select Customer)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "ဖောက်သည် အမည် (သို့) ဖုန်းနံပါတ်ဖြင့် အလွယ်တကူ ရှာဖွေနိုင်ပါသည်",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Box with Auto-Focus feel & Instant Filtering
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("customer_search_input"),
                    placeholder = {
                        Text(
                            text = "ဖောက်သည် အမည် (သို့) ဖုန်းနံပါတ် ရိုက်ထည့်ရှာပါ...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Search Result Count
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "ဖောက်သည် စုစုပေါင်း: ${customers.size} ဦး" else "တွေ့ရှိသော ဖောက်သည်: ${filteredCustomers.size} ဦး",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    if (searchQuery.isNotEmpty()) {
                        Text(
                            text = "ရှာဖွေမှု ရှင်းရန်",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { searchQuery = "" }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Customer List or Empty Search State
                if (filteredCustomers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "ရှာဖွေမှုနှင့် ကိုက်ညီသော ဖောက်သည် မတွေ့ပါ",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "အမည် (သို့) ဖုန်းနံပါတ် မှန်ကန်မှုရှိမရှိ ပြန်လည်စစ်ဆေးပါ",
                                fontSize = 12.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(filteredCustomers) { customer ->
                            val isSelected = customer.id == selectedCustomer.id
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        onSelectCustomer(customer)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF6C757D),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = customer.name.take(1).uppercase(),
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = customer.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Phone,
                                                    contentDescription = null,
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (customer.phone.isNotBlank()) customer.phone else "ဖုန်းနံပါတ် မရှိပါ",
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                                if (customer.code.isNotBlank() && customer.code != "123456789") {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "• Code: ${customer.code}",
                                                        fontSize = 11.sp,
                                                        color = Color.Gray
                                                    )
                                                }
                                            }
                                            if (customer.address.isNotBlank()) {
                                                Text(
                                                    text = customer.address,
                                                    fontSize = 11.sp,
                                                    color = Color.Gray,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog to set or edit individual Cart Item discount
 */
@Composable
fun ItemDiscountDialog(
    item: CartItem,
    onDismiss: () -> Unit,
    onApplyDiscount: (Double) -> Unit
) {
    val totalOriginal = item.unitPrice * item.qty
    var discountText by remember { mutableStateOf(if (item.discount > 0) item.discount.toInt().toString() else "") }
    val currentDiscount = discountText.toDoubleOrNull() ?: 0.0
    val netItemAmount = (totalOriginal - currentDiscount).coerceAtLeast(0.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(text = "Item Discount (လျှော့စျေး)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = item.product.itemName,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8F9FA),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Original (${item.qty} ${item.product.unit}):", fontSize = 12.sp)
                            Text("${formatMoney(totalOriginal)} MMK", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Discount:", fontSize = 12.sp, color = Color(0xFFDC3545))
                            Text("-${formatMoney(currentDiscount)} MMK", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC3545))
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Item Net Total:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${formatMoney(netItemAmount)} MMK",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Quick percentage discount presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(5, 10, 15, 20).forEach { pct ->
                        val discVal = (totalOriginal * pct / 100).toInt()
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { discountText = discVal.toString() },
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "$pct%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = discountText,
                    onValueChange = { input ->
                        val filtered = input.filter { it.isDigit() }
                        discountText = filtered
                    },
                    label = { Text("Discount Amount (MMK)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyDiscount(currentDiscount.coerceIn(0.0, totalOriginal))
                    onDismiss()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            if (item.discount > 0) {
                OutlinedButton(
                    onClick = {
                        onApplyDiscount(0.0)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Clear Disc", color = Color(0xFFDC3545))
                }
            } else {
                OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                    Text("Cancel")
                }
            }
        }
    )
}

/**
 * N-POS original voucher generator format:
 * `${username || 'N'}-${YYMMDD}-${HHMMSS}`
 */
private fun generateVoucherNo(email: String): String {
    val username = email.substringBefore("@").ifEmpty { "N" }
    val s = Date()
    val r = SimpleDateFormat("yyMMdd", Locale.getDefault()).format(s)
    val i = SimpleDateFormat("HHmmss", Locale.getDefault()).format(s)
    return "$username-$r-$i"
}

private fun formatMoney(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val formatter = DecimalFormat("#,##0.00", symbols)
    return formatter.format(amount)
}

private fun buildCustomerDisplayJson(
    cartItems: List<CartItem>,
    netAmount: Double,
    extraDiscount: Double,
    paid: Double,
    change: Double
): String {
    val root = JSONObject()
    val itemsArr = JSONArray()
    cartItems.forEach { item ->
        val obj = JSONObject().apply {
            put("item_name", item.product.itemName)
            put("sale_qty", item.qty)
            put("qty_sale_price", item.unitPrice)
            put("final_amount", item.finalAmount)
            put("discount", item.discount)
        }
        itemsArr.put(obj)
    }
    root.put("saleItems", itemsArr)
    root.put("net_amount", netAmount)
    root.put("extra_discount", extraDiscount)
    root.put("paid", paid)
    root.put("change", change)
    return root.toString()
}

private fun buildReceiptPayloadJson(
    voucherNo: String,
    cashier: String,
    customer: Customer,
    cartItems: List<CartItem>,
    totalAmount: Double,
    discount: Double,
    netAmount: Double,
    paid: Double,
    change: Double
): String {
    val root = JSONObject().apply {
        put("voucher_no", voucherNo)
        put("sale_date", SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()))
        put("total_amount", totalAmount)
        put("extra_discount", discount)
        put("net_amount", netAmount)
        put("paid", paid)
        put("change", change)
        put("customer", JSONObject().apply {
            put("name", customer.name)
            put("phone", customer.phone)
        })
        put("user", JSONObject().apply {
            put("displayName", cashier)
        })
        val items = JSONArray()
        cartItems.forEach { item ->
            items.put(JSONObject().apply {
                put("item_name", item.product.itemName)
                put("sale_qty", item.qty)
                put("qty_sale_price", item.unitPrice)
                put("discount", item.discount)
                put("final_amount", item.finalAmount)
            })
        }
        put("saleItems", items)
    }
    return root.toString()
}
