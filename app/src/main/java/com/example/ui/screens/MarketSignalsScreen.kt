package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.ShowChart
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.BankedViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MarketSignalsScreen(
    viewModel: BankedViewModel
) {
    val tradeLogs by viewModel.tradeLogs.collectAsState()
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

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
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberTeal.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = "Live AI Market Signals & Logs",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "Real-time execution ledger of active market bots",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(onClick = { viewModel.clearLogs() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Logs", tint = TextMuted)
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberTeal.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyberTeal, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Gemini AI Signal: High volatility expansion detected in SOL & NVDA. Rebalancing capital towards Alpha Vision AI.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Execution History Ledger",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        if (tradeLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No trades logged yet. Bots generate signals every few seconds.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            items(tradeLogs) { log ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trade_log_item_${log.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (log.tradeType) {
                                            "BUY" -> EmeraldPrimary.copy(alpha = 0.2f)
                                            "SELL" -> GoldAccent.copy(alpha = 0.2f)
                                            "DEPOSIT" -> CyberTeal.copy(alpha = 0.2f)
                                            else -> DarkSurfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ListAlt,
                                    contentDescription = null,
                                    tint = when (log.tradeType) {
                                        "BUY" -> EmeraldPrimary
                                        "SELL" -> GoldAccent
                                        "DEPOSIT" -> CyberTeal
                                        else -> TextPrimary
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "${log.botName} • ${log.assetSymbol}",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${log.note} (${dateFormat.format(Date(log.timestamp))})",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (log.profit > 0) {
                            Text(
                                text = "+$${String.format("%.2f", log.profit)}",
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                fontSize = 14.sp
                            )
                        } else if (log.amount > 0) {
                            Text(
                                text = "$${String.format("%.2f", log.amount)}",
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
