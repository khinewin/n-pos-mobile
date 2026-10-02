package com.npos.app.ui.vouchers

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.npos.app.data.model.SaleRecord
import com.npos.app.ui.theme.LocalStrings
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun VouchersScreen(
    vouchers: List<SaleRecord>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onPrintVoucher: (SaleRecord) -> Unit
) {
    val s = LocalStrings.current
    val context = LocalContext.current

    // Default to current date in d-m-y (dd-MM-yyyy) format
    val todayDateStr = remember {
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    }

    var fromDate by remember { mutableStateOf(todayDateStr) }
    var toDate by remember { mutableStateOf(todayDateStr) }
    var isDateFilterEnabled by remember { mutableStateOf(true) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedVoucherForDetail by remember { mutableStateOf<SaleRecord?>(null) }

    fun showDatePicker(initialDate: String, onDatePicked: (String) -> Unit) {
        val cal = Calendar.getInstance()
        try {
            val parts = initialDate.split("-")
            if (parts.size == 3) {
                // In dd-MM-yyyy: parts[0] = day, parts[1] = month, parts[2] = year
                val d = parts[0].toInt()
                val m = parts[1].toInt() - 1
                val y = parts[2].toInt()
                cal.set(Calendar.YEAR, y)
                cal.set(Calendar.MONTH, m)
                cal.set(Calendar.DAY_OF_MONTH, d)
            }
        } catch (_: Exception) {}

        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formatted = String.format(Locale.US, "%02d-%02d-%04d", dayOfMonth, month + 1, year)
                onDatePicked(formatted)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Filter by Date Range (From Date ~ To Date in dd-MM-yyyy) and Search Query
    val filteredVouchers = remember(vouchers, searchQuery, fromDate, toDate, isDateFilterEnabled) {
        val fromCal = Calendar.getInstance().apply {
            val parsed = parseDateRobust(fromDate)
            if (parsed != null) time = parsed
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val toCal = Calendar.getInstance().apply {
            val parsed = parseDateRobust(toDate)
            if (parsed != null) time = parsed
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val fromMillis = fromCal.timeInMillis
        val toMillis = toCal.timeInMillis

        vouchers.filter { v ->
            val matchesDate = if (!isDateFilterEnabled) {
                true
            } else {
                val vDate = parseDateRobust(v.saleDate)
                if (vDate != null) {
                    vDate.time in fromMillis..toMillis
                } else {
                    true
                }
            }

            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()
                v.voucherNo.lowercase().contains(q) ||
                        v.customer.name.lowercase().contains(q) ||
                        v.customer.phone.contains(q) ||
                        v.items.any { it.product.itemName.lowercase().contains(q) }
            }

            matchesDate && matchesSearch
        }
    }

    val totalAmount = remember(filteredVouchers) {
        filteredVouchers.sumOf { it.netAmount }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Top Search & Refresh bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(s.searchVoucherPlaceholder) },
                placeholder = { Text("V-..., Customer...") },
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
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("voucher_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Refresh Button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                IconButton(
                    onClick = onRefresh,
                    enabled = !isLoading,
                    modifier = Modifier.testTag("vouchers_refresh_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // From Date & To Date Range Picker Row (d-m-y format)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // From Date Card
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showDatePicker(fromDate) { picked ->
                                    fromDate = picked
                                    isDateFilterEnabled = true
                                    val pDate = parseDateRobust(picked)?.time ?: 0L
                                    val tDate = parseDateRobust(toDate)?.time ?: 0L
                                    if (tDate < pDate) toDate = picked
                                }
                            }
                            .testTag("from_date_picker_button"),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isDateFilterEnabled) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = s.fromDateTitle,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = s.fromDateTitle,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = fromDate,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Text(
                        text = "➔",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // To Date Card
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showDatePicker(toDate) { picked ->
                                    toDate = picked
                                    isDateFilterEnabled = true
                                    val pDate = parseDateRobust(picked)?.time ?: 0L
                                    val fDate = parseDateRobust(fromDate)?.time ?: 0L
                                    if (fDate > pDate) fromDate = picked
                                }
                            }
                            .testTag("to_date_picker_button"),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isDateFilterEnabled) BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = s.toDateTitle,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = s.toDateTitle,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = toDate,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Date Quick Presets (Today, Yesterday, This Month, All Dates)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isTodayActive = isDateFilterEnabled && fromDate == todayDateStr && toDate == todayDateStr
                    FilterChip(
                        selected = isTodayActive,
                        onClick = {
                            fromDate = todayDateStr
                            toDate = todayDateStr
                            isDateFilterEnabled = true
                        },
                        label = { Text(s.filterToday, fontSize = 11.sp, fontWeight = if (isTodayActive) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )

                    // Yesterday preset (d-m-y)
                    val yesterdayStr = remember {
                        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(cal.time)
                    }
                    val isYesterdayActive = isDateFilterEnabled && fromDate == yesterdayStr && toDate == yesterdayStr
                    FilterChip(
                        selected = isYesterdayActive,
                        onClick = {
                            fromDate = yesterdayStr
                            toDate = yesterdayStr
                            isDateFilterEnabled = true
                        },
                        label = { Text(s.filterYesterday, fontSize = 11.sp, fontWeight = if (isYesterdayActive) FontWeight.Bold else FontWeight.Normal) }
                    )

                    // This Month preset (01-MM-yyyy ~ today)
                    val firstDayOfMonth = remember {
                        val cal = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
                        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(cal.time)
                    }
                    val isThisMonthActive = isDateFilterEnabled && fromDate == firstDayOfMonth && toDate == todayDateStr
                    FilterChip(
                        selected = isThisMonthActive,
                        onClick = {
                            fromDate = firstDayOfMonth
                            toDate = todayDateStr
                            isDateFilterEnabled = true
                        },
                        label = { Text(s.filterThisMonth, fontSize = 11.sp, fontWeight = if (isThisMonthActive) FontWeight.Bold else FontWeight.Normal) }
                    )

                    // All Dates preset
                    FilterChip(
                        selected = !isDateFilterEnabled,
                        onClick = {
                            isDateFilterEnabled = false
                        },
                        label = { Text(s.filterAllDates, fontSize = 11.sp, fontWeight = if (!isDateFilterEnabled) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stats summary strip
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "${filteredVouchers.size} ${s.vouchersTitle}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (isDateFilterEnabled) {
                            Text(
                                text = if (fromDate == toDate) fromDate else "$fromDate ~ $toDate",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Text(
                                text = s.filterAllDates,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Text(
                    text = "${formatMoney(totalAmount)} MMK",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Vouchers List
        if (filteredVouchers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = "Empty",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isLoading) {
                            s.loading
                        } else if (isDateFilterEnabled) {
                            "${s.noVouchersFound} ($fromDate ~ $toDate)"
                        } else {
                            s.noVouchersFound
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    if (isDateFilterEnabled && !isLoading && vouchers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { isDateFilterEnabled = false },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "View ${s.filterAllDates} (${vouchers.size})", fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredVouchers, key = { it.voucherNo }) { voucher ->
                    VoucherCard(
                        voucher = voucher,
                        onViewDetails = { selectedVoucherForDetail = voucher },
                        onPrint = {
                            onPrintVoucher(voucher)
                            Toast.makeText(context, "${s.reprintReceipt}: ${voucher.voucherNo}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // Detail & Reprint Dialog
    selectedVoucherForDetail?.let { voucher ->
        VoucherDetailDialog(
            voucher = voucher,
            onDismiss = { selectedVoucherForDetail = null },
            onPrint = {
                onPrintVoucher(voucher)
                Toast.makeText(context, "${s.reprintReceipt}: ${voucher.voucherNo}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun VoucherCard(
    voucher: SaleRecord,
    onViewDetails: () -> Unit,
    onPrint: () -> Unit
) {
    val s = LocalStrings.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewDetails() }
            .testTag("voucher_item_${voucher.voucherNo}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Voucher No + Payment Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = "Receipt",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = voucher.voucherNo,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = formatDmyDisplay(voucher.saleDate),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (voucher.credit > 0) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = if (voucher.credit > 0) "Credit (${formatMoney(voucher.credit)})" else voucher.paymentType,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (voucher.credit > 0) Color(0xFFD32F2F) else Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            // Customer & Items info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Customer",
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = voucher.customer.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${voucher.items.size} items • Cashier: ${voucher.user.displayName}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Text(
                    text = "${formatMoney(voucher.netAmount)} MMK",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: View details and Reprint
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onViewDetails,
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Details",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = s.voucherDetails, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onPrint,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("reprint_button_${voucher.voucherNo}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Print",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = s.reprintReceipt, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun VoucherDetailDialog(
    voucher: SaleRecord,
    onDismiss: () -> Unit,
    onPrint: () -> Unit
) {
    val s = LocalStrings.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "Receipt",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = s.voucherDetails, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    // Invoice metadata box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Voucher No:", fontSize = 12.sp, color = Color.Gray)
                                Text(voucher.voucherNo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Date:", fontSize = 12.sp, color = Color.Gray)
                                Text(formatDmyDisplay(voucher.saleDate), fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Customer:", fontSize = 12.sp, color = Color.Gray)
                                Text(voucher.customer.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Cashier:", fontSize = 12.sp, color = Color.Gray)
                                Text(voucher.user.displayName, fontSize = 12.sp)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Items (${voucher.items.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                items(voucher.items) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.product.itemName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${formatMoney(item.unitPrice)} MMK x ${item.qty}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                        Text(
                            text = "${formatMoney(item.finalAmount)} MMK",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Amount:", fontSize = 13.sp)
                            Text("${formatMoney(voucher.totalAmount)} MMK", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Net Amount:", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "${formatMoney(voucher.netAmount)} MMK",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Paid:", fontSize = 12.sp, color = Color.Gray)
                            Text("${formatMoney(voucher.paid)} MMK", fontSize = 12.sp)
                        }

                        if (voucher.change > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Change (ပြန်အမ်း):", fontSize = 12.sp, color = Color(0xFF2E7D32))
                                Text("${formatMoney(voucher.change)} MMK", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                            }
                        }

                        if (voucher.credit > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Credit (ကျန်ငွေ):", fontSize = 12.sp, color = Color(0xFFD32F2F))
                                Text("${formatMoney(voucher.credit)} MMK", fontSize = 12.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onPrint()
                    onDismiss()
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("dialog_print_voucher_button")
            ) {
                Icon(imageVector = Icons.Default.Print, contentDescription = "Print", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = s.reprintReceipt, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = s.close)
            }
        }
    )
}

private fun parseDateRobust(dateStr: String): Date? {
    if (dateStr.isBlank()) return null
    val trimmed = dateStr.trim()
    trimmed.toLongOrNull()?.let { num ->
        if (num > 1000000000L) {
            val millis = if (num < 100000000000L) num * 1000 else num
            return Date(millis)
        }
    }
    val patterns = listOf(
        "dd-MM-yyyy HH:mm:ss",
        "dd-MM-yyyy HH:mm",
        "dd-MM-yyyy",
        "d-M-yyyy HH:mm",
        "d-M-yyyy",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm",
        "yyyy-MM-dd",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss",
        "dd/MM/yyyy HH:mm",
        "dd/MM/yyyy",
        "yyyy/MM/dd HH:mm",
        "yyyy/MM/dd"
    )
    for (pat in patterns) {
        try {
            val sdf = SimpleDateFormat(pat, Locale.US)
            val d = sdf.parse(trimmed)
            if (d != null) return d
        } catch (_: Exception) {}
    }
    return null
}

private fun formatDmyDisplay(dateStr: String): String {
    val trimmed = dateStr.trim()
    if (trimmed.isEmpty()) return "---"
    val date = parseDateRobust(trimmed) ?: return trimmed
    val out = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
    return out.format(date)
}

private fun formatMoney(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val formatter = DecimalFormat("#,##0.00", symbols)
    return formatter.format(amount)
}
