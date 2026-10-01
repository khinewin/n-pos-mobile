package com.npos.app.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.npos.app.R
import com.npos.app.data.model.DeviceViewMode
import com.npos.app.data.model.UserSession

enum class PosScreen(val title: String, val icon: ImageVector) {
    SALES("Sales (အရောင်း)", Icons.Default.PointOfSale),
    DASHBOARD("Dashboard (ပင်မ)", Icons.Default.Dashboard),
    CUSTOMERS("Customers (ဖောက်သည်များ)", Icons.Default.People)
}

@Composable
fun PosSidebarContent(
    currentScreen: PosScreen,
    user: UserSession,
    isSecondaryDisplayActive: Boolean,
    isPrinterConnected: Boolean,
    printerName: String,
    currentViewMode: DeviceViewMode = DeviceViewMode.AUTO,
    onSelectScreen: (PosScreen) -> Unit,
    onOpenPrinterSettings: () -> Unit,
    onOpenCustomerDisplay: () -> Unit,
    onOpenViewModeSettings: () -> Unit = {},
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // App / Store Brand Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_pos_logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "N-POS",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Cashier: ${user.displayName}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = user.email,
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(12.dp))

        // Main Navigation Items
        Text(
            text = "MAIN MENU",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        PosScreen.values().forEach { screen ->
            val isSelected = currentScreen == screen
            NavigationDrawerItem(
                label = {
                    Text(
                        text = screen.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 14.sp
                    )
                },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF495057)
                    )
                },
                selected = isSelected,
                onClick = { onSelectScreen(screen) },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .testTag("nav_item_${screen.name}")
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(12.dp))

        // POS Hardware Actions
        Text(
            text = "POS HARDWARE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )

        // Customer Screen action
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { onOpenCustomerDisplay() }
                .padding(vertical = 4.dp),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tv,
                    contentDescription = "Customer Display",
                    tint = if (isSecondaryDisplayActive) Color(0xFF198754) else Color(0xFF0D6EFD),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Customer Display Screen",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (isSecondaryDisplayActive) "Dual Screen Active" else "Preview Display Mode",
                        fontSize = 11.sp,
                        color = if (isSecondaryDisplayActive) Color(0xFF198754) else Color.Gray
                    )
                }
            }
        }

        // Bluetooth Printer settings action
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { onOpenPrinterSettings() }
                .padding(vertical = 4.dp),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = "Thermal Printer",
                    tint = if (isPrinterConnected) Color(0xFF198754) else Color(0xFFFD7E14),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Thermal Printer",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (isPrinterConnected) printerName else "Select Bluetooth Printer",
                        fontSize = 11.sp,
                        color = if (isPrinterConnected) Color(0xFF198754) else Color(0xFFDC3545)
                    )
                }
            }
        }

        // Device View Mode (Phone / Tablet / Auto)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { onOpenViewModeSettings() }
                .padding(vertical = 4.dp),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (currentViewMode) {
                        DeviceViewMode.PHONE -> Icons.Default.PhoneAndroid
                        DeviceViewMode.TABLET -> Icons.Default.TabletAndroid
                        DeviceViewMode.AUTO -> Icons.Default.Devices
                    },
                    contentDescription = "Device View Mode",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Device View (မျက်နှာပြင် မုဒ်)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${currentViewMode.titleEn} (${currentViewMode.titleMm})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(color = Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(12.dp))

        // Logout
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { onLogout() },
            color = Color(0xFFFDF2F2)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = Color(0xFFDC3545),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Logout (ထွက်ရန်)",
                    color = Color(0xFFDC3545),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "N-POS v1.0 • NTG",
            fontSize = 11.sp,
            color = Color.LightGray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
