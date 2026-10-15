package com.npos.app.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.npos.app.pos.PrinterConnectionType
import com.npos.app.pos.ReceiptPrintMode
import com.npos.app.pos.ThermalPrinterManager
import com.npos.app.ui.theme.AppStrings
import com.npos.app.ui.theme.LocalStrings
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@Composable
fun PrinterSettingsDialog(
    printerManager: ThermalPrinterManager,
    onTestPrint: () -> Unit,
    onRequestBluetoothPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalStrings.current
    val context = LocalContext.current

    val connectionType by printerManager.connectionType.collectAsState()
    val savedNetworkIp by printerManager.networkIp.collectAsState()
    val savedNetworkPort by printerManager.networkPort.collectAsState()
    val selectedDevice by printerManager.selectedDevice.collectAsState()
    val printerStatus by printerManager.printerStatus.collectAsState()
    val isPrinting by printerManager.isPrinting.collectAsState()
    val paperWidth by printerManager.paperWidth.collectAsState()
    val customMm by printerManager.customMm.collectAsState()
    val customColumns by printerManager.customColumns.collectAsState()

    val currentShopName by printerManager.shopName.collectAsState()
    val currentShopTagline by printerManager.shopTagline.collectAsState()
    val currentShopAddress by printerManager.shopAddress.collectAsState()
    val currentShopPhone by printerManager.shopPhone.collectAsState()
    val currentFooterGreeting by printerManager.footerGreeting.collectAsState()
    val currentFooterNote by printerManager.footerNote.collectAsState()

    val scope = rememberCoroutineScope()

    // Form editing states
    var shopNameInput by remember(currentShopName) { mutableStateOf(currentShopName) }
    var shopTaglineInput by remember(currentShopTagline) { mutableStateOf(currentShopTagline) }
    var shopAddressInput by remember(currentShopAddress) { mutableStateOf(currentShopAddress) }
    var shopPhoneInput by remember(currentShopPhone) { mutableStateOf(currentShopPhone) }
    var footerGreetingInput by remember(currentFooterGreeting) { mutableStateOf(currentFooterGreeting) }
    var footerNoteInput by remember(currentFooterNote) { mutableStateOf(currentFooterNote) }

    // Network printer editing states
    var networkIpInput by remember(savedNetworkIp) { mutableStateOf(savedNetworkIp) }
    var networkPortInput by remember(savedNetworkPort) { mutableStateOf(savedNetworkPort.toString()) }
    var isTestingNetwork by remember { mutableStateOf(false) }

    // Custom paper size input states
    var customMmInput by remember(customMm) { mutableStateOf(customMm.toString()) }
    var customColumnsInput by remember(customColumns) { mutableStateOf(customColumns.toString()) }

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Receipt Info, 1: Printer Hardware (Bluetooth / Network)

    var pairedDevices by remember {
        mutableStateOf(printerManager.getPairedPrinters())
    }

    val currentCols = printerManager.getPaperColumns(paperWidth)
    val displayWidthLabel = if (paperWidth == "custom") "Custom ${customMm}mm (${customColumns} cols)" else "$paperWidth ($currentCols cols)"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .heightIn(max = 680.dp)
                .testTag("printer_settings_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "Thermal Printer",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = s.printerTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${printerManager.getPrinterDisplayName()} • $displayWidthLabel",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_printer_settings_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Switcher: 0: Receipt Info, 1: Bluetooth Printer
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(s.receiptCustomization, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(s.printerTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Contents
                if (selectedTabIndex == 0) {
                    // TAB 0: Receipt Info Customization (Shop Name, Address, Phone, Footer)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Shop Name
                        OutlinedTextField(
                            value = shopNameInput,
                            onValueChange = { shopNameInput = it },
                            label = { Text(s.shopNameLabel) },
                            placeholder = { Text("ဥပမာ - Royal Mart, ABC Store") },
                            leadingIcon = {
                                Icon(Icons.Default.Store, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("receipt_shop_name_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tagline / Category
                        OutlinedTextField(
                            value = shopTaglineInput,
                            onValueChange = { shopTaglineInput = it },
                            label = { Text(s.shopTaglineLabel) },
                            placeholder = { Text("ဥပမာ - Mini Mart & Grocery") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("receipt_shop_tagline_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Address
                        OutlinedTextField(
                            value = shopAddressInput,
                            onValueChange = { shopAddressInput = it },
                            label = { Text(s.shopAddressLabel) },
                            placeholder = { Text("ဥပမာ - Mawlamyine, Mon State") },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("receipt_shop_address_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Phone
                        OutlinedTextField(
                            value = shopPhoneInput,
                            onValueChange = { shopPhoneInput = it },
                            label = { Text(s.shopPhoneLabel) },
                            placeholder = { Text("ဥပမာ - 09-970488345") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("receipt_shop_phone_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Footer Greeting
                        OutlinedTextField(
                            value = footerGreetingInput,
                            onValueChange = { footerGreetingInput = it },
                            label = { Text(s.footerGreetingLabel) },
                            placeholder = { Text("ဥပမာ - Thank you for shopping with us!") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("receipt_footer_greeting_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Footer Note
                        OutlinedTextField(
                            value = footerNoteInput,
                            onValueChange = { footerNoteInput = it },
                            label = { Text(s.footerNoteLabel) },
                            placeholder = { Text("ဥပမာ - ဝယ်ယူပြီးပစ္စည်း ပြန်မလဲပါ / Please come again") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("receipt_footer_note_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Receipt Paper Live Mockup Preview Header with Paper Size selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = s.receiptPreview,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = displayWidthLabel,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        // Paper Size Selector with Custom Input
                        PaperSizeSelectorWithCustom(
                            paperWidth = paperWidth,
                            customMm = customMm,
                            customColumns = customColumns,
                            customMmInput = customMmInput,
                            onCustomMmChange = { customMmInput = it },
                            customColumnsInput = customColumnsInput,
                            onCustomColumnsChange = { customColumnsInput = it },
                            onSelectStandardSize = { id ->
                                if (id == "custom") {
                                    val mm = customMmInput.toIntOrNull() ?: customMm
                                    val cols = customColumnsInput.toIntOrNull() ?: customColumns
                                    printerManager.setCustomPaperWidth(mm, cols)
                                } else {
                                    printerManager.setPaperWidth(id)
                                }
                            },
                            onApplyCustom = { mm, cols ->
                                printerManager.setCustomPaperWidth(mm, cols)
                                Toast.makeText(context, "${s.customPaperSize}: ${mm}mm ($cols cols)", Toast.LENGTH_SHORT).show()
                            },
                            s = s
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val previewDivider = "-".repeat(currentCols.coerceIn(24, 48))

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF9F9F9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = shopNameInput.replace("!", "").trim().ifEmpty { "N-POS" },
                                    fontWeight = FontWeight.Black,
                                    fontSize = 19.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (shopTaglineInput.isNotEmpty()) {
                                    Text(
                                        text = shopTaglineInput.replace("!", "").trim(),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                if (shopAddressInput.isNotEmpty()) {
                                    Text(
                                        text = shopAddressInput.replace("!", "").trim(),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                if (shopPhoneInput.isNotEmpty()) {
                                    Text(
                                        text = "Ph: ${shopPhoneInput.replace("!", "").trim()}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Text(
                                    text = previewDivider,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Gray
                                )

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text("Invoice No : n1-261015-131515", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    Text("Date       : 15-10-2026 13:15", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    Text("Customer   : Default Customer", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    Text("Cashier    : Cashier-1", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }

                                Text(
                                    text = previewDivider,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Gray
                                )

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("#   Item", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        Text("Amount", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                    Text(text = previewDivider, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color.LightGray)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("1   ဇကာ(1200) (1 x 1,200.00)", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        Text("1,200.00", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    }
                                    Text("2. Centella Ampoule", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("   (1 x 47,000.00)", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        Text("47,000.00 MMK", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    }
                                    Text(text = previewDivider, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Color.LightGray)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total:", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        Text("48,200.00 MMK", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Paid:", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        Text("50,000.00 MMK", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Change:", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        Text("1,800.00 MMK", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }

                                Text(
                                    text = previewDivider,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Gray
                                )

                                if (footerGreetingInput.isNotEmpty()) {
                                    Text(
                                        text = footerGreetingInput.replace("!", "").trim(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                if (footerNoteInput.isNotEmpty()) {
                                    Text(
                                        text = footerNoteInput.replace("!", "").trim(),
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Save Receipt Information Button
                        Button(
                            onClick = {
                                printerManager.updateReceiptConfig(
                                    name = shopNameInput,
                                    tagline = shopTaglineInput,
                                    address = shopAddressInput,
                                    phone = shopPhoneInput,
                                    greeting = footerGreetingInput,
                                    note = footerNoteInput
                                )
                                Toast.makeText(context, s.settingsSaved, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("save_receipt_info_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(s.saveSettings, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                } else {
                    // TAB 1: Bluetooth & Printer Hardware Settings
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Paper width selection with Custom option
                        Text(
                            text = "${s.paperWidth}:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        PaperSizeSelectorWithCustom(
                            paperWidth = paperWidth,
                            customMm = customMm,
                            customColumns = customColumns,
                            customMmInput = customMmInput,
                            onCustomMmChange = { customMmInput = it },
                            customColumnsInput = customColumnsInput,
                            onCustomColumnsChange = { customColumnsInput = it },
                            onSelectStandardSize = { id ->
                                if (id == "custom") {
                                    val mm = customMmInput.toIntOrNull() ?: customMm
                                    val cols = customColumnsInput.toIntOrNull() ?: customColumns
                                    printerManager.setCustomPaperWidth(mm, cols)
                                } else {
                                    printerManager.setPaperWidth(id)
                                }
                            },
                            onApplyCustom = { mm, cols ->
                                printerManager.setCustomPaperWidth(mm, cols)
                                Toast.makeText(context, "${s.customPaperSize}: ${mm}mm ($cols cols)", Toast.LENGTH_SHORT).show()
                            },
                            s = s
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Connection Type Selector (Bluetooth vs Network)
                        Text(
                            text = "${s.printerConnectionType}:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = connectionType == PrinterConnectionType.BLUETOOTH,
                                onClick = { printerManager.setConnectionType(PrinterConnectionType.BLUETOOTH) },
                                label = { Text(s.bluetoothPrinter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                                leadingIcon = {
                                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = connectionType == PrinterConnectionType.NETWORK,
                                onClick = { printerManager.setConnectionType(PrinterConnectionType.NETWORK) },
                                label = { Text(s.networkPrinter, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                                leadingIcon = {
                                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (connectionType == PrinterConnectionType.BLUETOOTH) {
                            // Paired Bluetooth Devices Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = s.selectBluetoothPrinter,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                IconButton(
                                    onClick = {
                                        pairedDevices = printerManager.getPairedPrinters()
                                        onRequestBluetoothPermission()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Refresh Devices", modifier = Modifier.size(18.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Device List
                            if (pairedDevices.isEmpty()) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF1F3F5)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "No paired Bluetooth printers found.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.DarkGray
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Please pair your POS printer in Android Bluetooth Settings first.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                ) {
                                    items(pairedDevices) { device ->
                                        val isSelected = selectedDevice?.address == device.address
                                        val deviceName = try {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                device.name ?: device.address
                                            } else {
                                                @Suppress("DEPRECATION")
                                                device.name ?: device.address
                                            }
                                        } catch (_: SecurityException) {
                                            device.address
                                        }

                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable {
                                                    printerManager.saveSelectedPrinter(device)
                                                    printerManager.setConnectionType(PrinterConnectionType.BLUETOOTH)
                                                },
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color(0xFFF8F9FA),
                                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
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
                                                        text = deviceName,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        fontSize = 14.sp
                                                    )
                                                    Text(
                                                        text = device.address,
                                                        fontSize = 11.sp,
                                                        color = Color.Gray
                                                    )
                                                }
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // NETWORK PRINTER (TCP/IP socket port 9100) Configuration Card
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp)
                                ) {
                                    Text(
                                        text = s.networkPrinter,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Direct Raw TCP Socket (Standard Port 9100)",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // IP Address Field
                                    OutlinedTextField(
                                        value = networkIpInput,
                                        onValueChange = { networkIpInput = it.trim() },
                                        label = { Text(s.printerIpAddress) },
                                        placeholder = { Text("192.168.1.100") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("printer_network_ip_input"),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Port Field
                                    OutlinedTextField(
                                        value = networkPortInput,
                                        onValueChange = { networkPortInput = it.filter { ch -> ch.isDigit() } },
                                        label = { Text(s.printerPort) },
                                        placeholder = { Text("9100") },
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("printer_network_port_input"),
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Save Network Config
                                        Button(
                                            onClick = {
                                                val cleanIp = networkIpInput.trim()
                                                if (cleanIp.isEmpty()) {
                                                    Toast.makeText(context, s.invalidIpAddress, Toast.LENGTH_SHORT).show()
                                                } else {
                                                    val port = networkPortInput.toIntOrNull() ?: ThermalPrinterManager.DEFAULT_NETWORK_PORT
                                                    printerManager.saveNetworkPrinter(cleanIp, port)
                                                    Toast.makeText(context, "${s.networkPrinterConfigured} ($cleanIp:$port)", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(s.save, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Test Network Connection
                                        OutlinedButton(
                                            onClick = {
                                                val cleanIp = networkIpInput.trim()
                                                if (cleanIp.isEmpty()) {
                                                    Toast.makeText(context, s.invalidIpAddress, Toast.LENGTH_SHORT).show()
                                                } else {
                                                    val port = networkPortInput.toIntOrNull() ?: ThermalPrinterManager.DEFAULT_NETWORK_PORT
                                                    isTestingNetwork = true
                                                    scope.launch {
                                                        val res = printerManager.testNetworkConnection(cleanIp, port)
                                                        isTestingNetwork = false
                                                        res.onSuccess { msg ->
                                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                        }.onFailure { err ->
                                                            Toast.makeText(context, err.message ?: "Connection failed", Toast.LENGTH_LONG).show()
                                                        }
                                                    }
                                                }
                                            },
                                            enabled = !isTestingNetwork && networkIpInput.trim().isNotEmpty(),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            if (isTestingNetwork) {
                                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                            } else {
                                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(s.testNetworkConnection, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Print Output Mode (Graphic/Myanmar Bitmap vs Raw Text)
                        Text(
                            text = "${s.receiptPrintMode}:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val currentPrintMode by printerManager.printMode.collectAsState()

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (currentPrintMode == ReceiptPrintMode.GRAPHIC_MYANMAR) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { printerManager.setPrintMode(ReceiptPrintMode.GRAPHIC_MYANMAR) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = currentPrintMode == ReceiptPrintMode.GRAPHIC_MYANMAR,
                                        onClick = { printerManager.setPrintMode(ReceiptPrintMode.GRAPHIC_MYANMAR) }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = s.printModeGraphic,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (currentPrintMode == ReceiptPrintMode.GRAPHIC_MYANMAR) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = s.myanmarFontSupported,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = currentPrintMode == ReceiptPrintMode.AUTO,
                                    onClick = { printerManager.setPrintMode(ReceiptPrintMode.AUTO) },
                                    label = { Text(s.printModeAuto, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = currentPrintMode == ReceiptPrintMode.RAW_TEXT,
                                    onClick = { printerManager.setPrintMode(ReceiptPrintMode.RAW_TEXT) },
                                    label = { Text(s.printModeText, fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Status banner
                        Text(
                            text = "Status: $printerStatus",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (printerStatus.contains("error", ignoreCase = true)) Color(0xFFDC3545) else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Bottom Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(s.close)
                    }

                    val isConfigured = printerManager.isPrinterConfigured()
                    Button(
                        onClick = onTestPrint,
                        enabled = !isPrinting && isConfigured,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_print_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (isPrinting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(s.testPrint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaperSizeSelectorWithCustom(
    paperWidth: String,
    customMm: Int,
    customColumns: Int,
    customMmInput: String,
    onCustomMmChange: (String) -> Unit,
    customColumnsInput: String,
    onCustomColumnsChange: (String) -> Unit,
    onSelectStandardSize: (String) -> Unit,
    onApplyCustom: (Int, Int) -> Unit,
    s: AppStrings
) {
    val isCustomSelected = paperWidth == "custom" || paperWidth.startsWith("custom")

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ThermalPrinterManager.PAPER_SIZES.forEach { sizeInfo ->
                FilterChip(
                    selected = paperWidth == sizeInfo.id,
                    onClick = { onSelectStandardSize(sizeInfo.id) },
                    label = {
                        Text(
                            text = "${sizeInfo.id} (${sizeInfo.columns})",
                            fontSize = 11.sp,
                            fontWeight = if (paperWidth == sizeInfo.id) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("paper_${sizeInfo.id}_chip")
                )
            }
            FilterChip(
                selected = isCustomSelected,
                onClick = { onSelectStandardSize("custom") },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCustomSelected) "${customMm}mm ($customColumns)" else s.customPaperSize,
                            fontSize = 11.sp,
                            fontWeight = if (isCustomSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                },
                modifier = Modifier.testTag("paper_custom_chip")
            )
        }

        if (isCustomSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "${s.customPaperSize}:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = customMmInput,
                            onValueChange = { newVal ->
                                val clean = newVal.filter { it.isDigit() }.take(3)
                                onCustomMmChange(clean)
                                val mmVal = clean.toIntOrNull()
                                if (mmVal != null && mmVal in 30..250) {
                                    val suggestedCols = (mmVal * 0.6).toInt().coerceIn(16, 120)
                                    onCustomColumnsChange(suggestedCols.toString())
                                }
                            },
                            label = { Text(s.customWidthMm, fontSize = 10.sp) },
                            placeholder = { Text("58") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_mm_input"),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = customColumnsInput,
                            onValueChange = { newVal ->
                                val clean = newVal.filter { it.isDigit() }.take(3)
                                onCustomColumnsChange(clean)
                            },
                            label = { Text(s.customColumnsLabel, fontSize = 10.sp) },
                            placeholder = { Text("32") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_cols_input"),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            val mm = customMmInput.toIntOrNull() ?: customMm
                            val cols = customColumnsInput.toIntOrNull() ?: customColumns
                            onApplyCustom(mm, cols)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("apply_custom_paper_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(s.applyCustomSize, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
