package com.npos.app.ui.barcode

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.npos.app.data.model.DeviceViewMode
import com.npos.app.data.model.Product
import com.npos.app.pos.BarcodeLabelConfig
import com.npos.app.pos.BarcodeLabelData
import com.npos.app.pos.BarcodeLabelGenerator
import com.npos.app.pos.BarcodePriceType
import com.npos.app.pos.BarcodePrintProtocol
import com.npos.app.pos.ThermalPrinterManager
import com.npos.app.ui.theme.LocalAppLanguage
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

data class BarcodeQueueItem(
    val id: String,
    val data: BarcodeLabelData,
    val copies: Int = 1
)

@Composable
fun BarcodePrintScreen(
    products: List<Product>,
    categories: List<String>,
    isLoadingProducts: Boolean,
    printerManager: ThermalPrinterManager,
    currentViewMode: DeviceViewMode = DeviceViewMode.AUTO,
    autoScannedBarcodeFlow: SharedFlow<String>? = null,
    onRefreshProducts: () -> Unit,
    onSearchCloud: (String, String?, Int) -> Unit,
    onOpenPrinterSettings: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isMm = LocalAppLanguage.current.isBurmese

    val shopName by printerManager.shopName.collectAsState()
    val isPrinting by printerManager.isPrinting.collectAsState()
    val printerStatus by printerManager.printerStatus.collectAsState()
    val isPrinterReady = printerManager.isPrinterConfigured()

    // Persistent Barcode Label Configuration (Manage what to include: item_name, retail_price, code_no, etc.)
    var labelConfig by remember { mutableStateOf(BarcodeLabelGenerator.loadConfig(context)) }

    fun updateConfig(newConfig: BarcodeLabelConfig) {
        labelConfig = newConfig
        BarcodeLabelGenerator.saveConfig(context, newConfig)
    }

    // Selected / Custom Editable Label Data
    var editItemName by remember { mutableStateOf("ဇကာ(1200)") }
    var editCodeNo by remember { mutableStateOf("10001") }
    var editBarcodeValue by remember { mutableStateOf("10001") }
    var editRetailPrice by remember { mutableStateOf("1200") }
    var editWholesalePrice by remember { mutableStateOf("1000") }
    var editUnit by remember { mutableStateOf("ခု") }
    var selectedProductId by remember { mutableStateOf<String?>(null) }
    var copiesCount by remember { mutableIntStateOf(1) }

    fun formatEditablePrice(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }

    // Initialize from first product when products load
    LaunchedEffect(products) {
        if (selectedProductId == null && products.isNotEmpty()) {
            val first = products.first()
            selectedProductId = first.id
            editItemName = first.itemName
            editCodeNo = first.codeNo
            editBarcodeValue = first.barcode.ifBlank { first.codeNo }
            editRetailPrice = formatEditablePrice(first.retailPrice)
            editWholesalePrice = formatEditablePrice(first.wholesalePrice)
            editUnit = first.unit.ifBlank { "ခု" }
        }
    }

    fun selectProductForLabel(product: Product) {
        selectedProductId = product.id
        editItemName = product.itemName
        editCodeNo = product.codeNo
        editBarcodeValue = product.barcode.ifBlank { product.codeNo }
        editRetailPrice = formatEditablePrice(product.retailPrice)
        editWholesalePrice = formatEditablePrice(product.wholesalePrice)
        editUnit = product.unit.ifBlank { "ခု" }
    }

    // Search & Category filter state
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // Batch Print Queue
    val printQueue = remember { mutableStateListOf<BarcodeQueueItem>() }

    fun addToQueue(data: BarcodeLabelData, count: Int = 1, key: String = data.codeNo) {
        val idx = printQueue.indexOfFirst { it.id == key && it.data.itemName == data.itemName }
        if (idx >= 0) {
            val existing = printQueue[idx]
            printQueue[idx] = existing.copy(copies = (existing.copies + count).coerceAtMost(200))
        } else {
            printQueue.add(BarcodeQueueItem(id = key, data = data, copies = count.coerceIn(1, 200)))
        }
    }

    // Listen to hardware barcode scanner
    LaunchedEffect(autoScannedBarcodeFlow, products) {
        autoScannedBarcodeFlow?.collect { scannedCode ->
            val clean = scannedCode.trim()
            if (clean.isNotEmpty()) {
                val match = products.find {
                    it.barcode.equals(clean, ignoreCase = true) || it.codeNo.equals(clean, ignoreCase = true)
                }
                if (match != null) {
                    selectProductForLabel(match)
                    Toast.makeText(
                        context,
                        "Scanned: ${match.itemName} (${match.codeNo})",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    editCodeNo = clean
                    editBarcodeValue = clean
                    searchQuery = clean
                }
            }
        }
    }

    val currentLabelData = remember(
        editItemName,
        editCodeNo,
        editBarcodeValue,
        editRetailPrice,
        editWholesalePrice,
        editUnit,
        shopName
    ) {
        BarcodeLabelData(
            itemName = editItemName.ifBlank { "Sample Item" },
            codeNo = editCodeNo.ifBlank { "00000" },
            barcodeValue = editBarcodeValue.ifBlank { editCodeNo.ifBlank { "00000" } },
            retailPrice = editRetailPrice.toDoubleOrNull() ?: 0.0,
            wholesalePrice = editWholesalePrice.toDoubleOrNull() ?: 0.0,
            unit = editUnit.ifBlank { "ခု" },
            shopName = shopName.ifBlank { "N-POS" }
        )
    }

    val previewBitmap = remember(currentLabelData, labelConfig) {
        BarcodeLabelGenerator.renderLabelBitmap(context, currentLabelData, labelConfig)
    }

    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        products.filter { prod ->
            val matchesCat = selectedCategory == "All" || prod.categoryName.equals(selectedCategory, ignoreCase = true)
            val q = searchQuery.trim()
            val matchesSearch = q.isEmpty() ||
                prod.itemName.contains(q, ignoreCase = true) ||
                prod.codeNo.contains(q, ignoreCase = true) ||
                prod.barcode.contains(q, ignoreCase = true)
            matchesCat && matchesSearch
        }
    }

    fun triggerPrintSingle(data: BarcodeLabelData, copies: Int) {
        if (!printerManager.isPrinterConfigured()) {
            Toast.makeText(
                context,
                if (isMm) "ကျေးဇူးပြု၍ Printer အရင်ချိတ်ဆက်ပေးပါ" else "Please configure printer first",
                Toast.LENGTH_SHORT
            ).show()
            onOpenPrinterSettings()
            return
        }
        scope.launch {
            val res = printerManager.printBarcodeLabel(data, labelConfig, copies)
            res.onSuccess {
                Toast.makeText(
                    context,
                    if (isMm) "✅ ဘားကုဒ် ($copies) ခု ပရင့်ထုတ်ပြီးပါပြီ" else "✅ Printed $copies barcode label(s)",
                    Toast.LENGTH_SHORT
                ).show()
            }.onFailure { err ->
                Toast.makeText(
                    context,
                    "Print error: ${err.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun triggerPrintQueue() {
        if (printQueue.isEmpty()) return
        if (!printerManager.isPrinterConfigured()) {
            Toast.makeText(
                context,
                if (isMm) "ကျေးဇူးပြု၍ Printer အရင်ချိတ်ဆက်ပေးပါ" else "Please configure printer first",
                Toast.LENGTH_SHORT
            ).show()
            onOpenPrinterSettings()
            return
        }
        scope.launch {
            val batch = printQueue.map { it.data to it.copies }
            val totalLabels = printQueue.sumOf { it.copies }
            val res = printerManager.printBarcodeLabelsBatch(batch, labelConfig)
            res.onSuccess {
                Toast.makeText(
                    context,
                    if (isMm) "✅ Queue ထဲမှ ဘားကုဒ်စုစုပေါင်း ($totalLabels) ခု ပရင့်ထုတ်ပြီးပါပြီ" else "✅ Printed all $totalLabels queued labels",
                    Toast.LENGTH_SHORT
                ).show()
            }.onFailure { err ->
                Toast.makeText(
                    context,
                    "Batch print error: ${err.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Mobile tab state: 0 = Label Designer & Manage Fields, 1 = Select Products & Queue
    var mobileTabIndex by remember { mutableIntStateOf(0) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWideLayout = when (currentViewMode) {
            DeviceViewMode.TABLET -> true
            DeviceViewMode.PHONE -> false
            DeviceViewMode.AUTO -> maxWidth >= 720.dp
        }

        if (isWideLayout) {
            // Dual-Pane Tablet / Desktop POS Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Pane: Product Catalog + Batch Queue (48% width)
                Column(
                    modifier = Modifier
                        .weight(0.48f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProductSelectorPane(
                        isMm = isMm,
                        products = filteredProducts,
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = it },
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onTriggerCloudSearch = {
                            onSearchCloud(searchQuery, selectedCategory, 100)
                        },
                        isLoadingProducts = isLoadingProducts,
                        selectedProductId = selectedProductId,
                        shopName = shopName,
                        onSelectProduct = { prod -> selectProductForLabel(prod) },
                        onQuickPrintProduct = { prod ->
                            val data = BarcodeLabelData(
                                itemName = prod.itemName,
                                codeNo = prod.codeNo,
                                barcodeValue = prod.barcode.ifBlank { prod.codeNo },
                                retailPrice = prod.retailPrice,
                                wholesalePrice = prod.wholesalePrice,
                                unit = prod.unit.ifBlank { "ခု" },
                                shopName = shopName
                            )
                            triggerPrintSingle(data, 1)
                        },
                        onAddProductToQueue = { prod ->
                            val data = BarcodeLabelData(
                                itemName = prod.itemName,
                                codeNo = prod.codeNo,
                                barcodeValue = prod.barcode.ifBlank { prod.codeNo },
                                retailPrice = prod.retailPrice,
                                wholesalePrice = prod.wholesalePrice,
                                unit = prod.unit.ifBlank { "ခု" },
                                shopName = shopName
                            )
                            addToQueue(data, 1, prod.id.ifBlank { prod.codeNo })
                            Toast.makeText(
                                context,
                                if (isMm) "${prod.itemName} ကို Queue ထဲထည့်ပြီးပါပြီ" else "Added ${prod.itemName} to queue",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onRefresh = onRefreshProducts,
                        modifier = Modifier.weight(1f)
                    )

                    if (printQueue.isNotEmpty()) {
                        BatchQueueCard(
                            isMm = isMm,
                            queue = printQueue,
                            labelConfig = labelConfig,
                            isPrinting = isPrinting,
                            onIncrement = { idx ->
                                val item = printQueue[idx]
                                printQueue[idx] = item.copy(copies = (item.copies + 1).coerceAtMost(200))
                            },
                            onDecrement = { idx ->
                                val item = printQueue[idx]
                                if (item.copies > 1) {
                                    printQueue[idx] = item.copy(copies = item.copies - 1)
                                } else {
                                    printQueue.removeAt(idx)
                                }
                            },
                            onRemove = { idx -> printQueue.removeAt(idx) },
                            onClearAll = { printQueue.clear() },
                            onPrintAll = { triggerPrintQueue() }
                        )
                    }
                }

                // Right Pane: Live Barcode Preview + Manage What to Include (`item_name`, `retail_price`, `whole_sale_price`, `code_no`, etc.)
                Column(
                    modifier = Modifier
                        .weight(0.52f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BarcodeDesignerAndConfigColumn(
                        isMm = isMm,
                        previewBitmap = previewBitmap,
                        labelConfig = labelConfig,
                        onUpdateConfig = { updateConfig(it) },
                        editItemName = editItemName,
                        onEditItemName = { editItemName = it },
                        editCodeNo = editCodeNo,
                        onEditCodeNo = {
                            editCodeNo = it
                            editBarcodeValue = it
                        },
                        editRetailPrice = editRetailPrice,
                        onEditRetailPrice = { editRetailPrice = it },
                        editWholesalePrice = editWholesalePrice,
                        onEditWholesalePrice = { editWholesalePrice = it },
                        editUnit = editUnit,
                        onEditUnit = { editUnit = it },
                        copiesCount = copiesCount,
                        onCopiesChange = { copiesCount = it.coerceIn(1, 200) },
                        isPrinterReady = isPrinterReady,
                        printerName = printerManager.getPrinterDisplayName(),
                        printerStatus = printerStatus,
                        isPrinting = isPrinting,
                        onOpenPrinterSettings = onOpenPrinterSettings,
                        onPrintCurrent = { triggerPrintSingle(currentLabelData, copiesCount) },
                        onAddCurrentToQueue = {
                            addToQueue(
                                currentLabelData,
                                copiesCount,
                                selectedProductId ?: currentLabelData.codeNo
                            )
                            Toast.makeText(
                                context,
                                if (isMm) "Queue ထဲသို့ ထည့်သွင်းပြီးပါပြီ" else "Added to print queue",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        } else {
            // Handheld Phone Layout with clean tabs
            Column(modifier = Modifier.fillMaxSize()) {
                val queueTotal = printQueue.sumOf { it.copies }
                TabRow(
                    selectedTabIndex = mobileTabIndex,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = mobileTabIndex == 0,
                        onClick = { mobileTabIndex = 0 },
                        modifier = Modifier.testTag("barcode_tab_designer"),
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isMm) "ဘားကုဒ် ဒီဇိုင်းနှင့် ပရင့်" else "Label Designer & Print",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    )
                    Tab(
                        selected = mobileTabIndex == 1,
                        onClick = { mobileTabIndex = 1 },
                        modifier = Modifier.testTag("barcode_tab_products"),
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Inventory2,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isMm) "ပစ္စည်းရွေးရန် (${products.size})" else "Products (${products.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                if (queueTotal > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                        Text("$queueTotal", color = Color.White, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    )
                }

                if (mobileTabIndex == 0) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (printQueue.isNotEmpty()) {
                            BatchQueueCard(
                                isMm = isMm,
                                queue = printQueue,
                                labelConfig = labelConfig,
                                isPrinting = isPrinting,
                                onIncrement = { idx ->
                                    val item = printQueue[idx]
                                    printQueue[idx] = item.copy(copies = (item.copies + 1).coerceAtMost(200))
                                },
                                onDecrement = { idx ->
                                    val item = printQueue[idx]
                                    if (item.copies > 1) {
                                        printQueue[idx] = item.copy(copies = item.copies - 1)
                                    } else {
                                        printQueue.removeAt(idx)
                                    }
                                },
                                onRemove = { idx -> printQueue.removeAt(idx) },
                                onClearAll = { printQueue.clear() },
                                onPrintAll = { triggerPrintQueue() }
                            )
                        }

                        BarcodeDesignerAndConfigColumn(
                            isMm = isMm,
                            previewBitmap = previewBitmap,
                            labelConfig = labelConfig,
                            onUpdateConfig = { updateConfig(it) },
                            editItemName = editItemName,
                            onEditItemName = { editItemName = it },
                            editCodeNo = editCodeNo,
                            onEditCodeNo = {
                                editCodeNo = it
                                editBarcodeValue = it
                            },
                            editRetailPrice = editRetailPrice,
                            onEditRetailPrice = { editRetailPrice = it },
                            editWholesalePrice = editWholesalePrice,
                            onEditWholesalePrice = { editWholesalePrice = it },
                            editUnit = editUnit,
                            onEditUnit = { editUnit = it },
                            copiesCount = copiesCount,
                            onCopiesChange = { copiesCount = it.coerceIn(1, 200) },
                            isPrinterReady = isPrinterReady,
                            printerName = printerManager.getPrinterDisplayName(),
                            printerStatus = printerStatus,
                            isPrinting = isPrinting,
                            onOpenPrinterSettings = onOpenPrinterSettings,
                            onPrintCurrent = { triggerPrintSingle(currentLabelData, copiesCount) },
                            onAddCurrentToQueue = {
                                addToQueue(
                                    currentLabelData,
                                    copiesCount,
                                    selectedProductId ?: currentLabelData.codeNo
                                )
                                Toast.makeText(
                                    context,
                                    if (isMm) "Queue ထဲသို့ ထည့်သွင်းပြီးပါပြီ" else "Added to print queue",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProductSelectorPane(
                            isMm = isMm,
                            products = filteredProducts,
                            categories = categories,
                            selectedCategory = selectedCategory,
                            onSelectCategory = { selectedCategory = it },
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            onTriggerCloudSearch = {
                                onSearchCloud(searchQuery, selectedCategory, 100)
                            },
                            isLoadingProducts = isLoadingProducts,
                            selectedProductId = selectedProductId,
                            shopName = shopName,
                            onSelectProduct = { prod ->
                                selectProductForLabel(prod)
                                mobileTabIndex = 0
                            },
                            onQuickPrintProduct = { prod ->
                                val data = BarcodeLabelData(
                                    itemName = prod.itemName,
                                    codeNo = prod.codeNo,
                                    barcodeValue = prod.barcode.ifBlank { prod.codeNo },
                                    retailPrice = prod.retailPrice,
                                    wholesalePrice = prod.wholesalePrice,
                                    unit = prod.unit.ifBlank { "ခု" },
                                    shopName = shopName
                                )
                                triggerPrintSingle(data, 1)
                            },
                            onAddProductToQueue = { prod ->
                                val data = BarcodeLabelData(
                                    itemName = prod.itemName,
                                    codeNo = prod.codeNo,
                                    barcodeValue = prod.barcode.ifBlank { prod.codeNo },
                                    retailPrice = prod.retailPrice,
                                    wholesalePrice = prod.wholesalePrice,
                                    unit = prod.unit.ifBlank { "ခု" },
                                    shopName = shopName
                                )
                                addToQueue(data, 1, prod.id.ifBlank { prod.codeNo })
                                Toast.makeText(
                                    context,
                                    if (isMm) "${prod.itemName} ကို Queue ထဲထည့်ပြီးပါပြီ" else "Added ${prod.itemName} to queue",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onRefresh = onRefreshProducts,
                            modifier = Modifier.weight(1f)
                        )

                        if (printQueue.isNotEmpty()) {
                            BatchQueueCard(
                                isMm = isMm,
                                queue = printQueue,
                                labelConfig = labelConfig,
                                isPrinting = isPrinting,
                                onIncrement = { idx ->
                                    val item = printQueue[idx]
                                    printQueue[idx] = item.copy(copies = (item.copies + 1).coerceAtMost(200))
                                },
                                onDecrement = { idx ->
                                    val item = printQueue[idx]
                                    if (item.copies > 1) {
                                        printQueue[idx] = item.copy(copies = item.copies - 1)
                                    } else {
                                        printQueue.removeAt(idx)
                                    }
                                },
                                onRemove = { idx -> printQueue.removeAt(idx) },
                                onClearAll = { printQueue.clear() },
                                onPrintAll = { triggerPrintQueue() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BarcodeDesignerAndConfigColumn(
    isMm: Boolean,
    previewBitmap: android.graphics.Bitmap,
    labelConfig: BarcodeLabelConfig,
    onUpdateConfig: (BarcodeLabelConfig) -> Unit,
    editItemName: String,
    onEditItemName: (String) -> Unit,
    editCodeNo: String,
    onEditCodeNo: (String) -> Unit,
    editRetailPrice: String,
    onEditRetailPrice: (String) -> Unit,
    editWholesalePrice: String,
    onEditWholesalePrice: (String) -> Unit,
    editUnit: String,
    onEditUnit: (String) -> Unit,
    copiesCount: Int,
    onCopiesChange: (Int) -> Unit,
    isPrinterReady: Boolean,
    printerName: String,
    printerStatus: String,
    isPrinting: Boolean,
    onOpenPrinterSettings: () -> Unit,
    onPrintCurrent: () -> Unit,
    onAddCurrentToQueue: () -> Unit
) {
    val selectedSize = BarcodeLabelGenerator.getSizeById(labelConfig.labelSizeId)

    // 1. Live Label Preview Card + Print Action Bar
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isMm) "ဘားကုဒ် စတစ်ကာ နမူနာ (Live Preview)" else "Live Barcode Label Preview",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${selectedSize.widthMm} × ${selectedSize.heightMm} mm • ${
                                if (isMm) labelConfig.protocol.titleMm else labelConfig.protocol.titleEn
                            }",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Printer Status Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isPrinterReady) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    border = BorderStroke(
                        1.dp,
                        if (isPrinterReady) Color(0xFF4CAF50).copy(alpha = 0.5f) else Color(0xFFFF9800).copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onOpenPrinterSettings() }
                        .testTag("barcode_printer_status_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Printer",
                            tint = if (isPrinterReady) Color(0xFF2E7D32) else Color(0xFFE65100),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isPrinterReady) printerName else if (isMm) "Printer ချိတ်ရန်" else "Setup Printer",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isPrinterReady) Color(0xFF2E7D32) else Color(0xFFE65100),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Thermal Sticker Paper Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.dp, Color(0xFFD0D5DD))
                ) {
                    Image(
                        bitmap = previewBitmap.asImageBitmap(),
                        contentDescription = "Barcode Label Preview",
                        modifier = Modifier
                            .width((selectedSize.widthMm * 5.2f).dp.coerceIn(160.dp, 310.dp))
                            .height((selectedSize.heightMm * 5.2f).dp.coerceIn(110.dp, 230.dp))
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Active Included Fields Summary Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isMm) "ပါဝင်သောအချက်များ:" else "Included:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IncludedFieldBadge("item_name", labelConfig.includeItemName) {
                    onUpdateConfig(labelConfig.copy(includeItemName = !labelConfig.includeItemName))
                }
                IncludedFieldBadge(
                    fieldKey = "retail_price",
                    included = labelConfig.includeRetailPrice && labelConfig.priceType == BarcodePriceType.RETAIL
                ) {
                    if (labelConfig.includeRetailPrice && labelConfig.priceType == BarcodePriceType.RETAIL) {
                        onUpdateConfig(labelConfig.copy(includeRetailPrice = false))
                    } else {
                        onUpdateConfig(
                            labelConfig.copy(
                                includeRetailPrice = true,
                                priceType = BarcodePriceType.RETAIL
                            )
                        )
                    }
                }
                IncludedFieldBadge(
                    fieldKey = "whole_sale_price",
                    included = labelConfig.includeRetailPrice && labelConfig.priceType == BarcodePriceType.WHOLESALE
                ) {
                    if (labelConfig.includeRetailPrice && labelConfig.priceType == BarcodePriceType.WHOLESALE) {
                        onUpdateConfig(labelConfig.copy(includeRetailPrice = false))
                    } else {
                        onUpdateConfig(
                            labelConfig.copy(
                                includeRetailPrice = true,
                                priceType = BarcodePriceType.WHOLESALE
                            )
                        )
                    }
                }
                IncludedFieldBadge("code_no", labelConfig.includeCodeNo) {
                    onUpdateConfig(labelConfig.copy(includeCodeNo = !labelConfig.includeCodeNo))
                }
                IncludedFieldBadge("barcode", labelConfig.includeBarcodeGraphic) {
                    onUpdateConfig(labelConfig.copy(includeBarcodeGraphic = !labelConfig.includeBarcodeGraphic))
                }
                IncludedFieldBadge("shop_name", labelConfig.includeShopName) {
                    onUpdateConfig(labelConfig.copy(includeShopName = !labelConfig.includeShopName))
                }
                IncludedFieldBadge("unit", labelConfig.includeUnit) {
                    onUpdateConfig(labelConfig.copy(includeUnit = !labelConfig.includeUnit))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            // Copies Counter (- / +) + Quick Preset Copies (1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isMm) "ထုတ်မည့် အရေအတွက် (Copies):" else "Number of Copies:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { onCopiesChange(copiesCount - 1) },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("barcode_copies_decrease_button")
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(18.dp))
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "$copiesCount",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }
                    IconButton(
                        onClick = { onCopiesChange(copiesCount + 1) },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("barcode_copies_increase_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 1 to 10 Quick-Select Number Buttons (1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..10).forEach { preset ->
                    val isSelected = copiesCount == preset
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onCopiesChange(preset) }
                            .testTag("barcode_copies_preset_$preset")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$preset",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) {
                                    Color.White
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onAddCurrentToQueue,
                    modifier = Modifier
                        .weight(0.42f)
                        .height(48.dp)
                        .testTag("add_barcode_queue_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PlaylistAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isMm) "+ Queue ထည့်" else "+ Queue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Button(
                    onClick = onPrintCurrent,
                    enabled = !isPrinting,
                    modifier = Modifier
                        .weight(0.58f)
                        .height(48.dp)
                        .testTag("print_single_barcode_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isPrinting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = printerStatus, fontSize = 12.sp, maxLines = 1)
                    } else {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isMm) "ဘားကုဒ် ပရင့်ထုတ်မည် ($copiesCount)" else "Print Barcode ($copiesCount)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // 2. Manage What to Include Card (item_name, retail_price, code_no, etc.)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isMm) {
                            "ဘားကုဒ်တွင် ပါဝင်မည့် အချက်အလက်များ စီမံရန်"
                        } else {
                            "Manage What to Include on Barcode"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "item_name, retail_price, whole_sale_price, code_no",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle Rows for item_name, price (retail_price / whole_sale_price), code_no, shop_name, unit, barcode
            IncludeToggleRow(
                fieldKey = "item_name",
                title = if (isMm) "ပစ္စည်းအမည် (Item Name)" else "Product Name (item_name)",
                subtitle = editItemName,
                checked = labelConfig.includeItemName,
                onCheckedChange = { onUpdateConfig(labelConfig.copy(includeItemName = it)) },
                testTag = "toggle_include_item_name"
            )

            val activePriceNumber = if (labelConfig.priceType == BarcodePriceType.WHOLESALE) {
                editWholesalePrice.toDoubleOrNull() ?: 0.0
            } else {
                editRetailPrice.toDoubleOrNull() ?: 0.0
            }
            IncludeToggleRow(
                fieldKey = labelConfig.priceType.fieldKey,
                title = if (isMm) {
                    if (labelConfig.priceType == BarcodePriceType.WHOLESALE) {
                        "လက်ကားဈေးနှုန်း (Wholesale Price)"
                    } else {
                        "လက်လီဈေးနှုန်း (Retail Price)"
                    }
                } else {
                    if (labelConfig.priceType == BarcodePriceType.WHOLESALE) {
                        "Wholesale Price (whole_sale_price)"
                    } else {
                        "Retail Price (retail_price)"
                    }
                },
                subtitle = "${BarcodeLabelGenerator.formatPrice(activePriceNumber)} ${labelConfig.currencySuffix}".trim(),
                checked = labelConfig.includeRetailPrice,
                onCheckedChange = { onUpdateConfig(labelConfig.copy(includeRetailPrice = it)) },
                testTag = "toggle_include_retail_price"
            )

            // Single Segmented Switch between Retail Price (retail_price) and Wholesale Price (whole_sale_price)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val retailFormatted = "${BarcodeLabelGenerator.formatPrice(editRetailPrice.toDoubleOrNull() ?: 0.0)} ${labelConfig.currencySuffix}".trim()
                val wholesaleFormatted = "${BarcodeLabelGenerator.formatPrice(editWholesalePrice.toDoubleOrNull() ?: 0.0)} ${labelConfig.currencySuffix}".trim()

                listOf(
                    Triple(
                        BarcodePriceType.RETAIL,
                        if (isMm) "လက်လီဈေး (retail_price)" else "Retail (retail_price)",
                        retailFormatted
                    ),
                    Triple(
                        BarcodePriceType.WHOLESALE,
                        if (isMm) "လက်ကားဈေး (whole_sale_price)" else "Wholesale (whole_sale_price)",
                        wholesaleFormatted
                    )
                ).forEach { (type, labelTitle, pricePreview) ->
                    val isSelected = labelConfig.includeRetailPrice && labelConfig.priceType == type
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                        },
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onUpdateConfig(
                                    labelConfig.copy(
                                        includeRetailPrice = true,
                                        priceType = type
                                    )
                                )
                            }
                            .testTag("select_price_type_${type.name.lowercase()}")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = labelTitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = pricePreview,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            IncludeToggleRow(
                fieldKey = "code_no",
                title = if (isMm) "ကုဒ်နံပါတ် (Code No)" else "Code Number (code_no)",
                subtitle = editCodeNo,
                checked = labelConfig.includeCodeNo,
                onCheckedChange = { onUpdateConfig(labelConfig.copy(includeCodeNo = it)) },
                testTag = "toggle_include_code_no"
            )

            IncludeToggleRow(
                fieldKey = "barcode",
                title = if (isMm) "ဘားကုဒ်လိုင်း (Barcode Graphic)" else "Barcode Bars (Code 128)",
                subtitle = "Code 128 Scannable Bars",
                checked = labelConfig.includeBarcodeGraphic,
                onCheckedChange = { onUpdateConfig(labelConfig.copy(includeBarcodeGraphic = it)) },
                testTag = "toggle_include_barcode_graphic"
            )

            IncludeToggleRow(
                fieldKey = "shop_name",
                title = if (isMm) "ဆိုင်အမည် (Shop Name)" else "Shop Name (shop_name)",
                subtitle = if (isMm) "ဘားကုဒ်အပေါ်ဆုံးတွင် ဆိုင်အမည်ထည့်ရန်" else "Display store header on top of label",
                checked = labelConfig.includeShopName,
                onCheckedChange = { onUpdateConfig(labelConfig.copy(includeShopName = it)) },
                testTag = "toggle_include_shop_name"
            )

            IncludeToggleRow(
                fieldKey = "unit",
                title = if (isMm) "ယူနစ် (Unit - ဥပမာ ခု/ဘူး)" else "Product Unit (unit)",
                subtitle = if (isMm) "ဈေးနှုန်းနောက်တွင် ယူနစ်တွဲပြရန် ($editUnit)" else "Append unit after price ($editUnit)",
                checked = labelConfig.includeUnit,
                onCheckedChange = { onUpdateConfig(labelConfig.copy(includeUnit = it)) },
                testTag = "toggle_include_unit"
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            // Label Size Selector
            Text(
                text = if (isMm) "စတစ်ကာ အရွယ်အစား (Label Size):" else "Label Paper Size:",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BarcodeLabelGenerator.LABEL_SIZES.forEach { size ->
                    val isSelected = labelConfig.labelSizeId == size.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdateConfig(labelConfig.copy(labelSizeId = size.id)) },
                        label = {
                            Text(
                                text = "${size.widthMm}×${size.heightMm}mm",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Font Size & Currency Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isMm) "စာလုံး အရွယ်အစား (Font Size):" else "Font Size:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        0.85f to (if (isMm) "အသေး" else "Small"),
                        1.0f to (if (isMm) "ပုံမှန်" else "Normal"),
                        1.18f to (if (isMm) "အကြီး" else "Large")
                    ).forEach { (scaleVal, label) ->
                        val selected = kotlin.math.abs(labelConfig.fontSizeScale - scaleVal) < 0.05f
                        FilterChip(
                            selected = selected,
                            onClick = { onUpdateConfig(labelConfig.copy(fontSizeScale = scaleVal)) },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Printer Protocol Selector (Node Server Text vs Real Printer Graphic vs TSPL)
            Text(
                text = if (isMm) "ပရင်တာ စနစ် (Printer Mode):" else "Printer Command Protocol:",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BarcodePrintProtocol.values().forEach { proto ->
                    val isSelected = labelConfig.protocol == proto
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onUpdateConfig(labelConfig.copy(protocol = proto)) }
                            .testTag("barcode_protocol_${proto.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isMm) proto.titleMm else proto.titleEn,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isMm) proto.descMm else proto.descEn,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 3. Custom / Manual Field Values Editor Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isMm) "လက်ရှိ ဘားကုဒ်စာသားများ ပြင်ဆင်ရန်" else "Edit Label Content Values",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = editItemName,
                onValueChange = onEditItemName,
                label = { Text(if (isMm) "item_name (ပစ္စည်းအမည်)" else "item_name (Product Name)") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("barcode_input_item_name")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = editRetailPrice,
                    onValueChange = onEditRetailPrice,
                    label = { Text(if (isMm) "retail_price (လက်လီဈေး)" else "retail_price (Retail)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("barcode_input_retail_price")
                )

                OutlinedTextField(
                    value = editWholesalePrice,
                    onValueChange = onEditWholesalePrice,
                    label = { Text(if (isMm) "whole_sale_price (လက်ကားဈေး)" else "whole_sale_price (Wholesale)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("barcode_input_wholesale_price")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = editCodeNo,
                    onValueChange = onEditCodeNo,
                    label = { Text(if (isMm) "code_no (ကုဒ်နံပါတ်)" else "code_no (Code No)") },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("barcode_input_code_no")
                )

                OutlinedTextField(
                    value = editUnit,
                    onValueChange = onEditUnit,
                    label = { Text(if (isMm) "unit (ယူနစ်)" else "unit (Unit)") },
                    singleLine = true,
                    modifier = Modifier.weight(0.6f)
                )

                OutlinedTextField(
                    value = labelConfig.currencySuffix,
                    onValueChange = { onUpdateConfig(labelConfig.copy(currencySuffix = it)) },
                    label = { Text(if (isMm) "ငွေကြေး (Ks)" else "Currency") },
                    singleLine = true,
                    modifier = Modifier.weight(0.6f)
                )
            }
        }
    }
}

@Composable
private fun IncludedFieldBadge(
    fieldKey: String,
    included: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (included) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            1.dp,
            if (included) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (included) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = fieldKey,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = if (included) FontWeight.Bold else FontWeight.Normal,
                color = if (included) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IncludeToggleRow(
    fieldKey: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) },
        color = if (checked) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        },
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(5.dp),
                        color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    ) {
                        Text(
                            text = fieldKey,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}

@Composable
private fun ProductSelectorPane(
    isMm: Boolean,
    products: List<Product>,
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onTriggerCloudSearch: () -> Unit,
    isLoadingProducts: Boolean,
    selectedProductId: String?,
    shopName: String,
    onSelectProduct: (Product) -> Unit,
    onQuickPrintProduct: (Product) -> Unit,
    onAddProductToQueue: (Product) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Header + Refresh
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isMm) "ကုန်ပစ္စည်း စာရင်းမှ ရွေးချယ်ရန်" else "Select Product to Print Barcode",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = if (isMm) "စုစုပေါင်း (${products.size}) မျိုး" else "${products.size} products available",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.testTag("barcode_refresh_products_button")
                ) {
                    if (isLoadingProducts) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = if (isMm) "item_name / code_no / ဘားကုဒ်ဖြင့် ရှာပါ..." else "Search by item_name, code_no, barcode...",
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onTriggerCloudSearch() }
                        ) {
                            Text(
                                text = if (isMm) "Cloud ရှာ" else "Cloud",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("barcode_product_search_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(cat) },
                        label = {
                            Text(
                                text = if (cat == "All" && isMm) "အားလုံး" else cat,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Product List
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isMm) "ပစ္စည်း ရှာမတွေ့ပါ" else "No products found",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(products, key = { it.id.ifBlank { it.codeNo } }) { product ->
                        val isSelected = selectedProductId == product.id
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            },
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSelectProduct(product) }
                                .testTag("barcode_product_item_${product.codeNo}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.itemName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = MaterialTheme.colorScheme.secondaryContainer
                                        ) {
                                            Text(
                                                text = product.codeNo,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(
                                            text = if (isMm) {
                                                "လက်လီ: ${BarcodeLabelGenerator.formatPrice(product.retailPrice)} • လက်ကား: ${BarcodeLabelGenerator.formatPrice(product.wholesalePrice)} Ks"
                                            } else {
                                                "R: ${BarcodeLabelGenerator.formatPrice(product.retailPrice)} • W: ${BarcodeLabelGenerator.formatPrice(product.wholesalePrice)} Ks"
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Add to Queue Button
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onAddProductToQueue(product) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlaylistAdd,
                                                contentDescription = "Add to Queue",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "+Queue",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    // Instant 1x Print Button
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { onQuickPrintProduct(product) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Print,
                                                contentDescription = "Print",
                                                tint = Color.White,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Print",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
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
}

@Composable
private fun BatchQueueCard(
    isMm: Boolean,
    queue: List<BarcodeQueueItem>,
    labelConfig: BarcodeLabelConfig,
    isPrinting: Boolean,
    onIncrement: (Int) -> Unit,
    onDecrement: (Int) -> Unit,
    onRemove: (Int) -> Unit,
    onClearAll: () -> Unit,
    onPrintAll: () -> Unit
) {
    val totalLabels = queue.sumOf { it.copies }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlaylistAddCheck,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isMm) {
                            "အစုလိုက်ထုတ်မည့် စာရင်း (${queue.size} မျိုး / $totalLabels ခု)"
                        } else {
                            "Batch Print Queue (${queue.size} items / $totalLabels labels)"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = if (isMm) "အားလုံးဖျက်" else "Clear",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onClearAll() }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 140.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(queue.size) { idx ->
                    val qItem = queue[idx]
                    val activeQueuePrice = BarcodeLabelGenerator.resolvePrice(qItem.data, labelConfig)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = qItem.data.itemName,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${qItem.data.codeNo} • ${BarcodeLabelGenerator.formatPrice(activeQueuePrice)} ${labelConfig.currencySuffix}".trim(),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onDecrement(idx) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Dec", modifier = Modifier.size(15.dp))
                                }
                                Text(
                                    text = "${qItem.copies}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                                IconButton(
                                    onClick = { onIncrement(idx) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Inc", modifier = Modifier.size(15.dp))
                                }
                                IconButton(
                                    onClick = { onRemove(idx) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(
                                        Icons.Default.DeleteOutline,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onPrintAll,
                enabled = !isPrinting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("print_batch_barcode_button"),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isMm) {
                        "Queue ထဲရှိ ဘားကုဒ်အားလုံး ထုတ်မည် ($totalLabels ခု)"
                    } else {
                        "Print All Queued Labels ($totalLabels)"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
