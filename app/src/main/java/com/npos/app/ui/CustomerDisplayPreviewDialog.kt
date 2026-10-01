package com.npos.app.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class CustomerCartItem(
    val name: String,
    val qty: Int,
    val price: Double,
    val amount: Double
)

@Composable
fun CustomerDisplayPreviewDialog(
    jsonData: String?,
    onDismiss: () -> Unit
) {
    val parsedData = remember(jsonData) {
        parseCustomerJson(jsonData)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("customer_display_preview_dialog"),
            shape = RoundedCornerShape(16.dp),
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
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "Customer Screen",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Customer Display Preview",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_customer_display_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Welcome / Store banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "N-POS Minimart",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Welcome! Thank you for shopping with us",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE9ECEF), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "No.", modifier = Modifier.weight(0.12f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = "Item", modifier = Modifier.weight(0.48f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = "Qty", modifier = Modifier.weight(0.15f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.Center)
                    Text(text = "Amount", modifier = Modifier.weight(0.25f), fontWeight = FontWeight.Bold, fontSize = 12.sp, textAlign = TextAlign.End)
                }

                // Table Items
                if (parsedData.items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Waiting for cashier to scan items...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        itemsIndexed(parsedData.items) { index, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${index + 1}", modifier = Modifier.weight(0.12f), fontSize = 13.sp)
                                Text(text = item.name, modifier = Modifier.weight(0.48f), fontSize = 13.sp, maxLines = 1)
                                Text(text = "${item.qty}", modifier = Modifier.weight(0.15f), fontSize = 13.sp, textAlign = TextAlign.Center)
                                Text(text = formatMoney(item.amount), modifier = Modifier.weight(0.25f), fontSize = 13.sp, textAlign = TextAlign.End, fontWeight = FontWeight.SemiBold)
                            }
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Summary Totals
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF8F9FA),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Total:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "${formatMoney(parsedData.netAmount)} MMK", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        if (parsedData.discount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Discount:", fontSize = 13.sp, color = Color.Gray)
                                Text(text = "-${formatMoney(parsedData.discount)} MMK", fontSize = 13.sp, color = Color(0xFFDC3545))
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Paid:", fontSize = 14.sp)
                            Text(text = "${formatMoney(parsedData.paid)} MMK", fontSize = 14.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Change:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(text = "${formatMoney(parsedData.change)} MMK", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF198754))
                        }
                    }
                }
            }
        }
    }
}

data class CustomerParsedData(
    val items: List<CustomerCartItem>,
    val netAmount: Double,
    val discount: Double,
    val paid: Double,
    val change: Double
)

private fun parseCustomerJson(jsonStr: String?): CustomerParsedData {
    if (jsonStr.isNullOrEmpty()) {
        return CustomerParsedData(emptyList(), 0.0, 0.0, 0.0, 0.0)
    }
    return try {
        val root = JSONObject(jsonStr)
        val items = mutableListOf<CustomerCartItem>()
        val arr = root.optJSONArray("saleItems")
        if (arr != null) {
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                items.add(
                    CustomerCartItem(
                        name = obj.optString("item_name", "Item"),
                        qty = obj.optInt("sale_qty", 1),
                        price = obj.optDouble("qty_sale_price", 0.0),
                        amount = obj.optDouble("final_amount", 0.0)
                    )
                )
            }
        }
        CustomerParsedData(
            items = items,
            netAmount = root.optDouble("net_amount", 0.0),
            discount = root.optDouble("extra_discount", 0.0),
            paid = root.optDouble("paid", 0.0),
            change = root.optDouble("change", 0.0)
        )
    } catch (_: Exception) {
        CustomerParsedData(emptyList(), 0.0, 0.0, 0.0, 0.0)
    }
}

private fun formatMoney(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    val formatter = DecimalFormat("#,##0.00", symbols)
    return formatter.format(amount)
}
