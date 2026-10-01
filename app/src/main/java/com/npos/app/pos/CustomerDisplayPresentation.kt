package com.npos.app.pos

import android.app.Presentation
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.npos.app.ui.CustomerCartItem
import com.npos.app.ui.CustomerParsedData
import com.npos.app.ui.theme.NPOSTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Sunmi Android Dual Screen POS Customer Display Presentation.
 * Designed for Sunmi T2, T2s, D2s, D3, iMin, Telpo, and standard Android Presentation secondary screens.
 */
class CustomerDisplayPresentation(
    outerContext: Context,
    display: Display,
    private val dataFlow: StateFlow<String?>
) : Presentation(outerContext, display) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val composeView = ComposeView(context).apply {
            setContent {
                NPOSTheme {
                    val rawJson by dataFlow.collectAsState()
                    val parsedData = remember(rawJson) { parseCustomerJson(rawJson) }
                    SunmiCustomerPresentationLayout(parsedData = parsedData)
                }
            }
        }
        setContentView(composeView)
    }
}

@Composable
fun SunmiCustomerPresentationLayout(parsedData: CustomerParsedData) {
    val listState = rememberLazyListState()

    // Auto scroll to bottom when items change
    LaunchedEffect(parsedData.items.size) {
        if (parsedData.items.isNotEmpty()) {
            listState.animateScrollToItem(parsedData.items.size - 1)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F3F5))
    ) {
        // LEFT COLUMN (36%): Sunmi Store Branding & Greeting
        Column(
            modifier = Modifier
                .weight(0.36f)
                .fillMaxHeight()
                .background(Color(0xFF0A2540))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.size(80.dp),
                    shadowElevation = 6.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "N-POS",
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = Color(0xFF0D6EFD)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "N-POS Minimart",
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "POS Software for Minimart",
                    fontSize = 14.sp,
                    color = Color(0xFF90CDF4),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                // Burmese Address
                Text(
                    text = "ခြံအမှတ် (၅)၊ သဲပုံစေတီအရှေ့လမ်းကြား (Rocket သောက်ရေသန့်စက်ရုံလမ်း)၊ တက္ကသိုလ်ပတ်လမ်း၊ ရှိုးကန့်ရပ်ကွက်၊ စျေးကြို ၊ မော်လမြိုင်မြို့။",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "PH: 09970488345",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD166),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "ကြွရောက်လာခြင်းအတွက် ကျေးဇူးတင်ပါသည်\nThank You For Shopping With Us!",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        lineHeight = 18.sp
                    )
                }
            }

            // Lower Brand signature
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "NEW TECH GROUP",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = "YOUR TRUSTED PARTNER IN TECHNOLOGY",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        // RIGHT COLUMN (64%): Sunmi POS Order Items & High-Visibility Total Panel
        Column(
            modifier = Modifier
                .weight(0.64f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Table Header Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0D6EFD)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "No.", modifier = Modifier.weight(0.08f), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Text(text = "Item Name (ပစ္စည်း)", modifier = Modifier.weight(0.50f), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Text(text = "Qty", modifier = Modifier.weight(0.12f), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp, textAlign = TextAlign.Center)
                    Text(text = "Price", modifier = Modifier.weight(0.15f), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp, textAlign = TextAlign.End)
                    Text(text = "Amount", modifier = Modifier.weight(0.15f), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp, textAlign = TextAlign.End)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Cart Items Body
            if (parsedData.items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Waiting for customer order...",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "ပစ္စည်းများ စတင်ရိုက်ထည့်ချိန်တွင် ဤနေရာ၌ အလိုအလျောက် ပြသမည်ဖြစ်ပါသည်",
                            fontSize = 14.sp,
                            color = Color.LightGray
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp)
                ) {
                    itemsIndexed(parsedData.items) { index, item ->
                        val isEven = index % 2 == 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isEven) Color.White else Color(0xFFF8F9FA))
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "${index + 1}", modifier = Modifier.weight(0.08f), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(
                                text = item.name,
                                modifier = Modifier.weight(0.50f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${item.qty}",
                                modifier = Modifier.weight(0.12f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = formatMoney(item.price),
                                modifier = Modifier.weight(0.15f),
                                fontSize = 15.sp,
                                textAlign = TextAlign.End,
                                color = Color.DarkGray
                            )
                            Text(
                                text = formatMoney(item.amount),
                                modifier = Modifier.weight(0.15f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                color = Color(0xFF0D6EFD)
                            )
                        }
                        HorizontalDivider(color = Color(0xFFE9ECEF))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // High Visibility POS Total Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF212529),
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Net Total Headline
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOTAL AMOUNT",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(0xFFFFD166)
                            )
                            Text(
                                text = "စုစုပေါင်း ကျသင့်ငွေ",
                                fontSize = 13.sp,
                                color = Color.LightGray
                            )
                        }

                        Text(
                            text = "${formatMoney(parsedData.netAmount)} MMK",
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp,
                            color = Color(0xFFFFD166)
                        )
                    }

                    if (parsedData.discount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Discount (လျှော့ဈေး):", fontSize = 14.sp, color = Color.LightGray)
                            Text(text = "-${formatMoney(parsedData.discount)} MMK", fontSize = 14.sp, color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(6.dp))

                    // Paid and Change
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "PAID (ပေးငွေ): ", fontSize = 16.sp, color = Color.White)
                            Text(text = "${formatMoney(parsedData.paid)} MMK", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "CHANGE (ပြန်အမ်းငွေ): ", fontSize = 16.sp, color = Color.White)
                            Text(
                                text = "${formatMoney(parsedData.change)} MMK",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF06D6A0)
                            )
                        }
                    }
                }
            }
        }
    }
}

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

/**
 * Manages dual display detection and lifecycle for Android POS devices.
 */
class CustomerDisplayManager(private val context: Context) {

    companion object {
        private const val TAG = "CustomerDisplayManager"
    }

    private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    private var currentPresentation: CustomerDisplayPresentation? = null

    private val _isSecondaryDisplayConnected = MutableStateFlow(false)
    val isSecondaryDisplayConnected: StateFlow<Boolean> = _isSecondaryDisplayConnected.asStateFlow()

    private val _lastData = MutableStateFlow<String?>(null)
    val lastData: StateFlow<String?> = _lastData.asStateFlow()

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) {
            Log.d(TAG, "Display added: $displayId")
            checkSecondaryDisplays()
        }

        override fun onDisplayRemoved(displayId: Int) {
            Log.d(TAG, "Display removed: $displayId")
            checkSecondaryDisplays()
        }

        override fun onDisplayChanged(displayId: Int) {
            checkSecondaryDisplays()
        }
    }

    init {
        displayManager.registerDisplayListener(displayListener, Handler(Looper.getMainLooper()))
        checkSecondaryDisplays()
    }

    fun checkSecondaryDisplays() {
        val presentationDisplays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
        val secondaryDisplay = if (presentationDisplays.isNotEmpty()) {
            presentationDisplays[0]
        } else {
            // Some Sunmi POS devices register secondary screens as standard displays (id != DEFAULT_DISPLAY)
            displayManager.displays.firstOrNull { it.displayId != Display.DEFAULT_DISPLAY }
        }

        if (secondaryDisplay != null) {
            _isSecondaryDisplayConnected.value = true
            if (currentPresentation == null || currentPresentation?.display != secondaryDisplay) {
                currentPresentation?.dismiss()
                try {
                    val presentation = CustomerDisplayPresentation(context, secondaryDisplay, _lastData)
                    presentation.show()
                    currentPresentation = presentation
                    Log.i(TAG, "Secondary display presentation attached successfully: ${secondaryDisplay.name}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to show presentation on secondary display", e)
                }
            }
        } else {
            _isSecondaryDisplayConnected.value = false
            currentPresentation?.dismiss()
            currentPresentation = null
            Log.d(TAG, "No secondary display available")
        }
    }

    fun updateCustomerData(jsonString: String) {
        _lastData.value = jsonString
    }

    fun showCustomerDisplayManually() {
        checkSecondaryDisplays()
    }

    fun release() {
        displayManager.unregisterDisplayListener(displayListener)
        currentPresentation?.dismiss()
        currentPresentation = null
    }
}
