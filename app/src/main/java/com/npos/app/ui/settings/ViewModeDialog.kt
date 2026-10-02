package com.npos.app.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.npos.app.data.model.DeviceViewMode
import com.npos.app.ui.theme.LocalStrings

@Composable
fun ViewModeDialog(
    currentMode: DeviceViewMode,
    onSelectMode: (DeviceViewMode) -> Unit,
    onDismiss: () -> Unit
) {
    val s = LocalStrings.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("view_mode_dialog"),
            shape = RoundedCornerShape(18.dp),
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
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Devices,
                                    contentDescription = s.deviceViewMode,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = s.deviceViewMode,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Screen Layout Setting",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = s.close)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options
                ViewModeOptionCard(
                    mode = DeviceViewMode.PHONE,
                    title = "Phone / Mobile Mode",
                    subtitleBurmese = "မိုဘိုင်းလ်ဖုန်း မုဒ် (လက်ကိုင်ဖုန်းများအတွက်)",
                    description = "Products နှင့် Cart ကို သီးခြား Tab များဖြင့် အလွယ်တကူ ရောင်းချနိုင်သော မုဒ်ဖြစ်ပါသည်။",
                    icon = Icons.Default.PhoneAndroid,
                    isSelected = currentMode == DeviceViewMode.PHONE,
                    onSelect = { onSelectMode(DeviceViewMode.PHONE) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                ViewModeOptionCard(
                    mode = DeviceViewMode.TABLET,
                    title = "Tablet / POS Mode",
                    subtitleBurmese = "တက်ဘလက် / POS စက်ကြီးများ မုဒ်",
                    description = "မျက်နှာပြင်ကျယ်တွင် Products စာရင်းနှင့် Cart ဇယားကို ဘေးချင်းယှဉ်၍ အပြည့်အစုံ ပြသပေးပါသည်။",
                    icon = Icons.Default.TabletAndroid,
                    isSelected = currentMode == DeviceViewMode.TABLET,
                    onSelect = { onSelectMode(DeviceViewMode.TABLET) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                ViewModeOptionCard(
                    mode = DeviceViewMode.AUTO,
                    title = "Auto Detect",
                    subtitleBurmese = "အလိုအလျောက် ရွေးချယ်မှု",
                    description = "စက်၏ မျက်နှာပြင် အရွယ်အစားအလိုက် အသင့်တော်ဆုံး ပုံစံကို အလိုအလျောက် သတ်မှတ်ပေးပါသည်။",
                    icon = Icons.Default.Devices,
                    isSelected = currentMode == DeviceViewMode.AUTO,
                    onSelect = { onSelectMode(DeviceViewMode.AUTO) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(s.close, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ViewModeOptionCard(
    mode: DeviceViewMode,
    title: String,
    subtitleBurmese: String,
    description: String,
    icon: ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("view_mode_option_${mode.name.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitleBurmese,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
        }
    }
}
