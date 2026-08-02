package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BotCard
import com.example.ui.components.LiveMarketChart
import com.example.ui.theme.*
import com.example.ui.viewmodel.BankedViewModel

@Composable
fun AnalyzerBotsScreen(
    viewModel: BankedViewModel
) {
    val marketBots by viewModel.marketBots.collectAsState()
    var selectedMarketTab by remember { mutableStateOf("Crypto & Tech AI Index") }
    var isScanning by remember { mutableStateOf(false) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            kotlinx.coroutines.delay(1200L)
            isScanning = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, IndigoAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = "3 Market Analyzer Bots",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "AI-Driven Algorithmic Investment Engine",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = { isScanning = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isScanning,
                            modifier = Modifier.testTag("ai_rebalance_btn")
                        ) {
                            Icon(
                                imageVector = if (isScanning) Icons.Default.Refresh else Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isScanning) "Scanning..." else "AI Scan",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Text(
                        text = "These 3 specialized market bots continuously analyze liquidity feeds, technical indicators, and sentiment metrics across Crypto, Equities, and Forex to execute optimal trades.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            LiveMarketChart(selectedMarket = selectedMarketTab)
        }

        item {
            Text(
                text = "Active Analyzer Bots Configuration",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        items(marketBots) { bot ->
            BotCard(
                bot = bot,
                onToggleActive = { viewModel.toggleMarketBot(bot.id) }
            )
        }
    }
}
