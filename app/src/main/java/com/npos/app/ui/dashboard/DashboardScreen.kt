package com.npos.app.ui.dashboard

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.npos.app.data.model.DeviceViewMode
import com.npos.app.data.model.UserSalesSummary
import com.npos.app.data.model.UserSession
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun DashboardScreen(
    user: UserSession,
    salesSummary: UserSalesSummary,
    isLoadingSummary: Boolean = false,
    isSecondaryDisplayActive: Boolean = false,
    isPrinterConnected: Boolean = false,
    printerName: String = "",
    currentViewMode: DeviceViewMode = DeviceViewMode.AUTO,
    onRefreshSummary: () -> Unit = {},
    onGoToSales: () -> Unit = {},
    onOpenCustomerDisplay: () -> Unit = {},
    onOpenPrinterSettings: () -> Unit = {},
    onOpenViewModeSettings: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "မင်္ဂလာပါ, ${user.displayName}",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.email,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onRefreshSummary,
                        modifier = Modifier.testTag("dashboard_refresh_summary_button")
                    ) {
                        if (isLoadingSummary) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Logged-in User's Sales Amount
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SALES AMOUNT (${user.displayName.uppercase()})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                if (isLoadingSummary) {
                    Text(
                        text = "Updating...",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Today's Sales & This Month's Sales Amount Cards
        if (currentViewMode == DeviceViewMode.PHONE) {
            item {
                TodaySalesCard(salesSummary = salesSummary)
            }
            item {
                MonthSalesCard(salesSummary = salesSummary)
            }
        } else {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TodaySalesCard(salesSummary = salesSummary, modifier = Modifier.weight(1f))
                    MonthSalesCard(salesSummary = salesSummary, modifier = Modifier.weight(1f))
                }
            }
        }

        // Section Title: Hardware Settings
        item {
            Text(
                text = "HARDWARE TOOLS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        // Customer Display View Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCustomerDisplay() }
                    .testTag("dashboard_customer_display_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE3F2FD),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tv,
                                    contentDescription = "Customer Display View",
                                    tint = Color(0xFF0D6EFD),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Customer Display View",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isSecondaryDisplayActive) "Secondary Display: Active" else "Preview Customer Display Window",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSecondaryDisplayActive) Color(0xFFE8F5E9) else Color(0xFFE3F2FD)
                    ) {
                        Text(
                            text = if (isSecondaryDisplayActive) "Active" else "Preview Mode",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSecondaryDisplayActive) Color(0xFF2E7D32) else Color(0xFF0D6EFD),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // Printer Setting Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenPrinterSettings() }
                    .testTag("dashboard_printer_setting_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isPrinterConnected) Color(0xFFE8F5E9) else Color(0xFFFFF3CD),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = "Printer Setting",
                                    tint = if (isPrinterConnected) Color(0xFF198754) else Color(0xFFFD7E14),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Printer Setting",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isPrinterConnected) "Connected: $printerName" else "Configure Bluetooth Thermal Printer",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPrinterConnected) Color(0xFFE8F5E9) else Color(0xFFFFF3CD)
                    ) {
                        Text(
                            text = if (isPrinterConnected) "Connected" else "Settings",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPrinterConnected) Color(0xFF198754) else Color(0xFF664D03),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatMoney(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val formatter = DecimalFormat("#,##0.00", symbols)
    return formatter.format(amount)
}

@Composable
private fun TodaySalesCard(salesSummary: UserSalesSummary, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_today_sales_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE8F5E9),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Today Sales",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = "TODAY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "${formatMoney(salesSummary.todayAmount)} MMK",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1B5E20)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Today's Sales (ယနေ့ ရောင်းရငွေ)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            Text(
                text = "${salesSummary.todayCount} Vouchers Completed",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun MonthSalesCard(salesSummary: UserSalesSummary, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_month_sales_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFE3F2FD),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "This Month Sales",
                            tint = Color(0xFF0D6EFD),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE3F2FD)
                ) {
                    Text(
                        text = "THIS MONTH",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D6EFD),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "${formatMoney(salesSummary.thisMonthAmount)} MMK",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0A58CA)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "This Month (ဒီလ ရောင်းရငွေ)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            Text(
                text = "${salesSummary.thisMonthCount} Vouchers Completed",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}
