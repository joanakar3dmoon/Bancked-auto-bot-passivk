package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PortfolioDistributionChart
import com.example.ui.components.PotentialYieldCalculator
import com.example.ui.components.WorkerCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.BankedViewModel

@Composable
fun WorkerBotsScreen(
    viewModel: BankedViewModel
) {
    val workerBots by viewModel.workerBots.collectAsState()
    val marketBots by viewModel.marketBots.collectAsState()
    val account by viewModel.userAccount.collectAsState()

    val currentAccount = account ?: return

    val totalMultiplier = workerBots.filter { it.isActive }.fold(1.0) { acc, worker ->
        acc * (1.0 + ((worker.effectMultiplier - 1.0) * worker.level))
    }

    var selectedRebalanceStrategy by remember { mutableStateOf(currentAccount.riskTolerance) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // Visual Summary: Portfolio Performance & Asset Distribution Chart
        item {
            val totalPortfolioValue = currentAccount.cashBalance + marketBots.sumOf { it.allocatedCapital }
            PortfolioDistributionChart(
                workerBots = workerBots,
                totalPortfolioValue = totalPortfolioValue,
                netMultiplier = totalMultiplier
            )
        }

        // Potential Yield Calculator Tool
        item {
            PotentialYieldCalculator(
                workerBots = workerBots,
                onUpgradeOrDeployBot = { bot ->
                    viewModel.upgradeWorkerBot(bot.id)
                }
            )
        }

        // Hero Card: Worker Bot Net Multiplier
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "3 Worker Upgrade Bots",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Maximize ROI, Reinvest Profits & Shield Drawdowns",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                fontSize = 11.sp
                            )
                        }
                    }

                    HorizontalDivider(color = CardBorder.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Worker Net Boost", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary), fontSize = 11.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                                Text(
                                    text = "${String.format("%.2f", totalMultiplier)}x Passive Speed",
                                    fontWeight = FontWeight.Black,
                                    color = GoldAccent,
                                    fontSize = 18.sp
                                )
                            }
                        }

                        Column {
                            Text("Available Cash", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary), fontSize = 11.sp)
                            Text(
                                text = "$${String.format("%.2f", currentAccount.cashBalance)}",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Compound Dividends Quick Action Button
                    Button(
                        onClick = { viewModel.compoundWorkerProfits() },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("compound_dividends_btn")
                    ) {
                        Icon(Icons.Default.Autorenew, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reinvertir Dividendos (Compound 15%)", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // AUTO-REBALANCE & CAPITAL PRESERVATION ENGINE CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, IndigoBright.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = IndigoBright)
                        Text(
                            text = "Estrategias de Auto-Rebalanceo AI",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Text(
                        text = "Ajusta dinámicamente el capital asignado entre las 3 bots según volatilidad y retorno esperado para proteger el capital.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        fontSize = 11.sp
                    )

                    // Strategy Selector Buttons
                    val strategies = listOf(
                        Triple("PROFIT_MAXIMIZER", "ROI Máximo", "60% Crypto / 30% Tech / 10% FX"),
                        Triple("CAPITAL_PROTECTION", "Shield Drawdown", "60% FX / 25% Tech / 15% Crypto"),
                        Triple("BALANCED", "Equilibrado", "33% Crypto / 33% Tech / 33% FX")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        strategies.forEach { (stratKey, title, allocation) ->
                            val isSelected = selectedRebalanceStrategy == stratKey
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) IndigoGradientStart else DarkSurfaceVariant)
                                    .border(1.dp, if (isSelected) IndigoBright else CardBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedRebalanceStrategy = stratKey }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(title, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else TextPrimary, fontSize = 13.sp)
                                        Text(allocation, fontSize = 10.sp, color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextSecondary)
                                    }
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.executeAutoRebalanceStrategy(selectedRebalanceStrategy) },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoGradientStart),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("execute_auto_rebalance_btn")
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ejecutar Auto-Rebalanceo Ahora", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "Worker Bot Upgrades",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        items(workerBots) { worker ->
            WorkerCard(
                worker = worker,
                onToggleActive = { viewModel.toggleWorkerBot(worker.id) },
                onUpgrade = { viewModel.upgradeWorkerBot(worker.id) },
                userBalance = currentAccount.cashBalance
            )
        }
    }
}
