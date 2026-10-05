package com.npos.app.pos

import android.app.Presentation
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.npos.app.data.AppLanguage
import com.npos.app.data.AppSettingsManager
import com.npos.app.data.AppThemeMode
import com.npos.app.ui.CustomerCartItem
import com.npos.app.ui.CustomerParsedData
import com.npos.app.ui.theme.LocalAppLanguage
import com.npos.app.ui.theme.LocalThemeMode
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
    private val dataFlow: StateFlow<String?>,
    private val printerManager: ThermalPrinterManager? = null
) : Presentation(outerContext, display) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val composeView = ComposeView(context).apply {
            setContent {
                val themeMode by AppSettingsManager.globalThemeMode.collectAsState()
                val language by AppSettingsManager.globalLanguage.collectAsState()

                NPOSTheme(themeMode = themeMode, language = language) {
                    val rawJson by dataFlow.collectAsState()
                    val parsedData = remember(rawJson) { parseCustomerJson(rawJson) }

                    // Collect live store info from Printer Settings or fallback to SharedPreferences
                    val prefs = remember { context.getSharedPreferences("pos_printer_prefs", Context.MODE_PRIVATE) }
                    val shopName = printerManager?.shopName?.collectAsState()?.value
                        ?: prefs.getString("shop_name", "N-POS")?.ifEmpty { "N-POS" } ?: "N-POS"
                    val shopTagline = printerManager?.shopTagline?.collectAsState()?.value
                        ?: prefs.getString("shop_tagline", "POS Software for Minimart")?.ifEmpty { "POS Software for Minimart" } ?: "POS Software for Minimart"
                    val shopAddress = printerManager?.shopAddress?.collectAsState()?.value
                        ?: prefs.getString("shop_address", "Mawlamyine, Mon State")?.ifEmpty { "Mawlamyine, Mon State" } ?: "Mawlamyine, Mon State"
                    val shopPhone = printerManager?.shopPhone?.collectAsState()?.value
                        ?: prefs.getString("shop_phone", "09-970488345")?.ifEmpty { "09-970488345" } ?: "09-970488345"
                    val footerGreeting = printerManager?.footerGreeting?.collectAsState()?.value
                        ?: prefs.getString("footer_greeting", "ကြွရောက်လာခြင်းအတွက် ကျေးဇူးတင်ပါသည်")?.ifEmpty { "ကြွရောက်လာခြင်းအတွက် ကျေးဇူးတင်ပါသည်" } ?: "ကြွရောက်လာခြင်းအတွက် ကျေးဇူးတင်ပါသည်"
                    val footerNote = printerManager?.footerNote?.collectAsState()?.value
                        ?: prefs.getString("footer_note", "Thank you for shopping with us!")?.ifEmpty { "Thank you for shopping with us!" } ?: "Thank you for shopping with us!"

                    SunmiCustomerPresentationLayout(
                        parsedData = parsedData,
                        shopName = shopName,
                        shopTagline = shopTagline,
                        shopAddress = shopAddress,
                        shopPhone = shopPhone,
                        footerGreeting = footerGreeting,
                        footerNote = footerNote
                    )
                }
            }
        }
        setContentView(composeView)
    }
}

@Composable
fun SunmiCustomerPresentationLayout(
    parsedData: CustomerParsedData,
    shopName: String = "N-POS",
    shopTagline: String = "POS Software for Minimart",
    shopAddress: String = "Mawlamyine, Mon State",
    shopPhone: String = "09-970488345",
    footerGreeting: String = "ကြွရောက်လာခြင်းအတွက် ကျေးဇူးတင်ပါသည်",
    footerNote: String = "Thank you for shopping with us!"
) {
    val listState = rememberLazyListState()

    // Determine current theme mode (Light vs Dark)
    val currentTheme = LocalThemeMode.current
    val isDark = when (currentTheme) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    // Determine current language (Myanmar vs English)
    val currentLanguage = LocalAppLanguage.current
    val isMm = currentLanguage == AppLanguage.MM

    // Right Side Localized Strings according to selected Language
    val labelNo = if (isMm) "စဉ်" else "No."
    val labelItem = if (isMm) "ပစ္စည်းအမည်" else "Item Name"
    val labelQty = if (isMm) "အရေအတွက်" else "Qty"
    val labelPrice = if (isMm) "နှုန်း" else "Price"
    val labelAmount = if (isMm) "ကျသင့်ငွေ" else "Amount"

    val emptyTitle = if (isMm) "ဖောက်သည်အတွက် အမှာစာ စောင့်ဆိုင်းနေပါသည်" else "Waiting for customer order..."
    val emptySubtitle = if (isMm) "ပစ္စည်းများ စတင်ရိုက်ထည့်ချိန်တွင် ဤနေရာ၌ အချိန်နှင့်တပြေးညီ တိုက်ရိုက်ပြသမည်ဖြစ်ပါသည်" else "Items scanned by cashier will appear here in real-time"

    val labelTotalMain = if (isMm) "စုစုပေါင်း ကျသင့်ငွေ" else "TOTAL AMOUNT"
    val labelTotalSub = if (isMm) "TOTAL" else "Total Amount"
    val labelDiscount = if (isMm) "လျှော့ဈေး" else "Discount"
    val labelPaid = if (isMm) "ပေးငွေ" else "PAID"
    val labelChange = if (isMm) "ပြန်အမ်းငွေ" else "CHANGE"

    // Auto scroll to bottom when items change
    LaunchedEffect(parsedData.items.size) {
        if (parsedData.items.isNotEmpty()) {
            listState.animateScrollToItem(parsedData.items.size - 1)
        }
    }

    // Animation transition for floating cart and greeting glow
    val infiniteTransition = rememberInfiniteTransition(label = "CustomerScreenAnimations")
    val cartOffsetY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cartOffsetY"
    )
    val greetingGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "greetingGlow"
    )

    // Dynamic Color Palette for Light vs Dark Mode
    val screenBg = if (isDark) Color(0xFF0F1115) else Color(0xFFF1F3F5)
    val leftGradientColors = if (isDark) {
        listOf(Color(0xFF0B0F19), Color(0xFF151C2C), Color(0xFF0B0F19))
    } else {
        listOf(Color(0xFF0A2540), Color(0xFF13395E), Color(0xFF0A2540))
    }
    val leftBorderColor = if (isDark) Color(0xFF263248) else Color.Transparent
    val cardSurfaceBg = if (isDark) Color(0xFF171B22) else Color.White
    val cardSurfaceBorder = if (isDark) Color(0xFF2B303C) else Color(0xFFE9ECEF)
    val tableHeaderBg = if (isDark) Color(0xFF1E40AF) else Color(0xFF0D6EFD)
    val totalCardBg = if (isDark) Color(0xFF0D0F14) else Color(0xFF212529)
    val totalCardBorder = if (isDark) Color(0xFF262C38) else Color.Transparent
    val itemTextColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF212529)
    val itemSubTextColor = if (isDark) Color(0xFF94A3B8) else Color.DarkGray
    val itemAmountColor = if (isDark) Color(0xFF60A5FA) else Color(0xFF0D6EFD)
    val zebraBg = if (isDark) Color(0xFF1D222B) else Color(0xFFF8F9FA)
    val dividerColor = if (isDark) Color(0xFF262C38) else Color(0xFFE9ECEF)

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
    ) {
        // LEFT COLUMN (38%): Store Info from Print Settings & Branding
        Surface(
            modifier = Modifier
                .weight(0.38f)
                .fillMaxHeight(),
            color = Color.Transparent,
            border = if (isDark) BorderStroke(1.dp, leftBorderColor) else null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(leftGradientColors))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // ဆိုင်အမည် (Store Name)
                    Text(
                        text = shopName,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ဆိုင်အမျိုးအစား (Store Tagline / Category)
                    if (shopTagline.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isDark) Color(0xFF1E3A8A).copy(alpha = 0.4f) else Color(0xFF0D6EFD).copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, if (isDark) Color(0xFF3B82F6).copy(alpha = 0.5f) else Color(0xFF0D6EFD).copy(alpha = 0.5f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFF93C5FD) else Color(0xFF90CDF4),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = shopTagline,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFF93C5FD) else Color(0xFF90CDF4),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // လိပ်စာ (Address)
                    if (shopAddress.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) Color(0xFF1E293B).copy(alpha = 0.65f) else Color.White.copy(alpha = 0.08f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Address",
                                    tint = if (isDark) Color(0xFF93C5FD) else Color(0xFF90CDF4),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(top = 1.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = shopAddress,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // ဖုန်းနံပါတ် (Phone)
                    if (shopPhone.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFD166).copy(alpha = if (isDark) 0.12f else 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Phone",
                                    tint = Color(0xFFFFD166),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = shopPhone,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD166)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // နှုတ်ဆက်စာသား (Greeting & Note) with Heart Icon
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Color(0xFF1E293B).copy(alpha = 0.75f * greetingGlow) else Color.White.copy(alpha = 0.12f * greetingGlow),
                        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = Color(0xFFFF6B6B),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = footerGreeting,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                            if (footerNote.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = footerNote,
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Lower Brand Signature: New Tech Group & Your Trusted Partner (Tightly Stacked)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "New Tech Group",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = if (isDark) Color(0xFFE2E8F0) else Color.White,
                        letterSpacing = 0.8.sp,
                        lineHeight = 14.sp
                    )
                    Text(
                        text = "Your Trusted Partner",
                        fontWeight = FontWeight.Medium,
                        fontSize = 8.5.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color.White.copy(alpha = 0.7f),
                        letterSpacing = 0.8.sp,
                        lineHeight = 11.sp
                    )
                }
            }
        }

        // RIGHT COLUMN (62%): Live Sales / Cart Items & High-Visibility Total Panel
        Column(
            modifier = Modifier
                .weight(0.62f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Table Header Bar (Localized by selected language)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = tableHeaderBg
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = labelNo, modifier = Modifier.weight(0.08f), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Text(text = labelItem, modifier = Modifier.weight(0.50f), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Text(text = labelQty, modifier = Modifier.weight(0.12f), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp, textAlign = TextAlign.Center)
                    Text(text = labelPrice, modifier = Modifier.weight(0.15f), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp, textAlign = TextAlign.End)
                    Text(text = labelAmount, modifier = Modifier.weight(0.15f), fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp, textAlign = TextAlign.End)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Cart Items Body or Animated Waiting Empty State
            if (parsedData.items.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = cardSurfaceBg,
                    border = BorderStroke(1.dp, cardSurfaceBorder)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isDark) Color(0xFF0D47A1).copy(alpha = 0.35f) else Color(0xFFE7F1FF),
                                modifier = Modifier
                                    .size(90.dp)
                                    .offset { IntOffset(0, cartOffsetY.toInt() * 4) }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingCart,
                                        contentDescription = "Cart",
                                        tint = if (isDark) Color(0xFF64B5F6) else Color(0xFF0D6EFD),
                                        modifier = Modifier.size(46.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = emptyTitle,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = itemTextColor,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = emptySubtitle,
                                fontSize = 13.sp,
                                color = itemSubTextColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = cardSurfaceBg,
                    border = BorderStroke(1.dp, cardSurfaceBorder)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 6.dp)
                    ) {
                        itemsIndexed(parsedData.items) { index, item ->
                            val isEven = index % 2 == 0
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isEven) cardSurfaceBg else zebraBg)
                                    .padding(horizontal = 8.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${index + 1}", modifier = Modifier.weight(0.08f), fontSize = 15.sp, fontWeight = FontWeight.Medium, color = itemTextColor)
                                Text(
                                    text = item.name,
                                    modifier = Modifier.weight(0.50f),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = itemTextColor
                                )
                                Text(
                                    text = "${item.qty}",
                                    modifier = Modifier.weight(0.12f),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = itemTextColor
                                )
                                Text(
                                    text = formatMoney(item.price),
                                    modifier = Modifier.weight(0.15f),
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.End,
                                    color = itemSubTextColor
                                )
                                Text(
                                    text = formatMoney(item.amount),
                                    modifier = Modifier.weight(0.15f),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End,
                                    color = itemAmountColor
                                )
                            }
                            HorizontalDivider(color = dividerColor)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // High Visibility POS Total Card with Animation (Localized)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = totalCardBg,
                border = if (isDark) BorderStroke(1.dp, totalCardBorder) else null,
                shadowElevation = if (isDark) 4.dp else 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Net Total Headline with animated number transition
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = labelTotalMain,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(0xFFFFD166)
                            )
                            Text(
                                text = labelTotalSub,
                                fontSize = 13.sp,
                                color = Color.LightGray
                            )
                        }

                        AnimatedContent(
                            targetState = parsedData.netAmount,
                            transitionSpec = {
                                (slideInVertically { height -> height } + fadeIn()) togetherWith
                                        (slideOutVertically { height -> -height } + fadeOut())
                            },
                            label = "animatedTotal"
                        ) { netAmount ->
                            Text(
                                text = "${formatMoney(netAmount)} MMK",
                                fontWeight = FontWeight.Black,
                                fontSize = 32.sp,
                                color = Color(0xFFFFD166)
                            )
                        }
                    }

                    if (parsedData.discount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "$labelDiscount:", fontSize = 14.sp, color = Color.LightGray)
                            Text(text = "-${formatMoney(parsedData.discount)} MMK", fontSize = 14.sp, color = Color(0xFFFF6B6B), fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(6.dp))

                    // Paid and Change (Localized)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "$labelPaid: ", fontSize = 16.sp, color = Color.White)
                            Text(text = "${formatMoney(parsedData.paid)} MMK", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "$labelChange: ", fontSize = 16.sp, color = Color.White)
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
class CustomerDisplayManager(
    private val context: Context,
    private val printerManager: ThermalPrinterManager? = null
) {

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
                    val presentation = CustomerDisplayPresentation(context, secondaryDisplay, _lastData, printerManager)
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
