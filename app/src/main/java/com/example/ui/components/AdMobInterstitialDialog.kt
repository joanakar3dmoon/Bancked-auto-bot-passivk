package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdMobInterstitialDialog(
    onDismiss: () -> Unit,
    onAdImpression: (adRevenue: Double) -> Unit
) {
    var countdown by remember { mutableIntStateOf(3) }
    var canClose by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }
        canClose = true
        onAdImpression(0.75) // Interstitial impression reward
    }

    AlertDialog(
        onDismissRequest = { if (canClose) onDismiss() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("admob_interstitial_dialog"),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            tonalElevation = 16.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Header: AdMob Interstitial Format Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GoldAccent)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("AD INTERSTITIAL", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                        Text(
                            text = "Google Mobile Ads SDK",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (canClose) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceVariant)
                                .testTag("close_interstitial_ad_btn")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close Ad", tint = TextPrimary, modifier = Modifier.size(18.dp))
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Skip in $countdown s", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Main Interstitial Content Simulation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            )
                        )
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "AI Portfolio Automated Growth",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Upgrade to VIP Apex Tier for 0% commission on automated rebalancing.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text("Learn More", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // Configuration Details Box (Unit IDs & App ID)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkBackground)
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "App ID: ca-app-pub-3904073712994742~3347511713",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextMuted
                        )
                        Text(
                            text = "Ad Unit: ca-app-pub-3904073712994742/1033173712 (Interstitial)",
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            color = CyberTeal
                        )
                        if (canClose) {
                            Text(
                                text = "✅ Impression Verified (+$0.75 Ad Revenue Earned)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }
                    }
                }

                if (canClose) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoGradientStart),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue to Application", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
