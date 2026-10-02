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
import com.npos.app.pos.ThermalPrinterManager
import com.npos.app.ui.theme.AppStrings
import com.npos.app.ui.theme.LocalStrings

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

    // Form editing states
    var shopNameInput by remember(currentShopName) { mutableStateOf(currentShopName) }
    var shopTaglineInput by remember(currentShopTagline) { mutableStateOf(currentShopTagline) }
    var shopAddressInput by remember(currentShopAddress) { mutableStateOf(currentShopAddress) }
    var shopPhoneInput by remember(currentShopPhone) { mutableStateOf(currentShopPhone) }
    var footerGreetingInput by remember(currentFooterGreeting) { mutableStateOf(currentFooterGreeting) }
    var footerNoteInput by remember(currentFooterNote) { mutableStateOf(currentFooterNote) }

    // Custom paper size input states
    var customMmInput by remember(customMm) { mutableStateOf(customMm.toString()) }
    var customColumnsInput by remember(customColumns) { mutableStateOf(customColumns.toString()) }

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Receipt Info, 1: Bluetooth Printer

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
                                Text(s.selectBluetoothPrinter, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                                    text = shopNameInput.ifEmpty { "N-POS" },
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Black
                                )
                                if (shopTaglineInput.isNotEmpty()) {
                                    Text(
                                        text = shopTaglineInput,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.DarkGray
                                    )
                                }
                                if (shopAddressInput.isNotEmpty()) {
                                    Text(
                                        text = shopAddressInput,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.DarkGray
                                    )
                                }
                                if (shopPhoneInput.isNotEmpty()) {
                                    Text(
                                        text = "Ph: $shopPhoneInput",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color.DarkGray
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
                                    Text("Invoice No : V-1001", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    Text("Date       : 01-10-2026 14:30", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    Text("Customer   : General", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                    Text("Cashier    : Admin", fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }

                                Text(
                                    text = previewDivider,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Gray
                                )

                                Text(
                                    text = "[ Sample Items & Totals ]",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Gray
                                )

                                Text(
                                    text = previewDivider,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.Gray
                                )

                                if (footerGreetingInput.isNotEmpty()) {
                                    Text(
                                        text = footerGreetingInput,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                if (footerNoteInput.isNotEmpty()) {
                                    Text(
                                        text = footerNoteInput,
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

                    Button(
                        onClick = onTestPrint,
                        enabled = !isPrinting && selectedDevice != null,
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
