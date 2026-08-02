package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.UserAccountEntity
import com.example.ui.components.AdMobBanner
import com.example.ui.components.BotCard
import com.example.ui.components.LiveMarketChart
import com.example.ui.components.WorkerCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.BankedViewModel

@Composable
fun DashboardScreen(
    viewModel: BankedViewModel,
    onNavigateToBots: () -> Unit,
    onNavigateToWorkers: () -> Unit,
    onNavigateToMonetization: () -> Unit,
    onOpenRewardAd: () -> Unit,
    onOpenDeposit: () -> Unit
) {
    val account by viewModel.userAccount.collectAsState()
    val marketBots by viewModel.marketBots.collectAsState()
    val workerBots by viewModel.workerBots.collectAsState()
    val tradeLogs by viewModel.tradeLogs.collectAsState()

    val currentAccount = account ?: return

    val totalBotCapital = marketBots.sumOf { it.allocatedCapital }
    val netWorth = currentAccount.cashBalance + totalBotCapital
    val activeBotsCount = marketBots.count { it.isActive }
    val activeWorkersCount = workerBots.count { it.isActive }

    val isAdBoostActive = currentAccount.adBoostActiveUntil > System.currentTimeMillis()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // Portfolio Net Worth Summary Card (Atmospheric Gradient - Immersive UI)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4F46E5),
                                Color(0xFF1D4ED8),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(28.dp))
                    .testTag("dashboard_networth_card")
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "TOTAL PORTFOLIO YIELD",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.8f),
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$${String.format("%.2f", netWorth)}",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 32.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "+12.4%",
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Tier Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    when (currentAccount.subscriptionTier) {
                                        "INSTITUTIONAL" -> GoldAccent
                                        "PRO" -> CyberTeal
                                        else -> Color.White.copy(alpha = 0.2f)
                                    }
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = currentAccount.subscriptionTier,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (currentAccount.subscriptionTier == "FREE") Color.White else Color.Black
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                    // Stats Row: Yield $/sec, Cash, Total Profit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Passive Yield", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f)), fontSize = 11.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$${String.format("%.2f", currentAccount.passiveYieldPerSec)}/sec",
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Column {
                            Text("Cash Balance", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f)), fontSize = 11.sp)
                            Text(
                                text = "$${String.format("%.2f", currentAccount.cashBalance)}",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        Column {
                            Text("Total Earnings", style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.7f)), fontSize = 11.sp)
                            Text(
                                text = "$${String.format("%.2f", currentAccount.totalEarnings)}",
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent,
                                fontSize = 14.sp
                            )
                        }
                    }

                    if (isAdBoostActive) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "🔥 2.0x AdMob Reward Speed Boost Active!",
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Action Buttons Row (Immersive UI Glassmorphism Buttons)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onOpenDeposit,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.22f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("deposit_btn")
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Deposit", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.claimQuickYield() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.22f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("harvest_yield_btn")
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Harvest", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        IconButton(
                            onClick = onOpenRewardAd,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.22f))
                                .testTag("open_ad_reward_btn")
                        ) {
                            Icon(Icons.Default.OndemandVideo, contentDescription = "Watch Ad Boost", tint = GoldAccent)
                        }
                    }
                }
            }
        }

        // Live Market Chart
        item {
            LiveMarketChart(selectedMarket = "Crypto & Tech AI Index")
        }

        // 3 Analyzer Bots Quick Overview Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, tint = EmeraldPrimary)
                    Text(
                        text = "3 Market Analyzer Bots ($activeBotsCount/3 Active)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                TextButton(onClick = onNavigateToBots) {
                    Text("Manage All", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(marketBots) { bot ->
            BotCard(
                bot = bot,
                onToggleActive = { viewModel.toggleMarketBot(bot.id) }
            )
        }

        // 3 Worker Upgrade Bots Quick Overview Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, tint = GoldAccent)
                    Text(
                        text = "3 Worker Upgrade Bots ($activeWorkersCount/3 Active)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                TextButton(onClick = onNavigateToWorkers) {
                    Text("Upgrades", color = GoldAccent, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(workerBots) { worker ->
            WorkerCard(
                worker = worker,
                onToggleActive = { viewModel.toggleWorkerBot(worker.id) },
                onUpgrade = { viewModel.upgradeWorkerBot(worker.id) },
                userBalance = currentAccount.cashBalance
            )
        }

        // Live Algorithmic Trade Logs Stream
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("trade_logs_preview_card"),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
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
                        Text(
                            text = "Live Bot Execution Logs",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Icon(Icons.Default.Sync, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                    }

                    if (tradeLogs.isEmpty()) {
                        Text(
                            text = "Bots are analyzing market liquidity...",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    } else {
                        tradeLogs.take(4).forEach { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurfaceVariant)
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${log.botName} • ${log.assetSymbol}",
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = log.note,
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                                if (log.profit > 0) {
                                    Text(
                                        text = "+$${String.format("%.2f", log.profit)}",
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
