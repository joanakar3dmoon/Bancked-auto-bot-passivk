package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.WorkerBotEntity
import com.example.ui.theme.*

@Composable
fun PortfolioDistributionChart(
    workerBots: List<WorkerBotEntity>,
    totalPortfolioValue: Double,
    netMultiplier: Double,
    modifier: Modifier = Modifier
) {
    var chartType by remember { mutableIntStateOf(0) } // 0: Distribution Donut, 1: ROI Performance Trend

    val sliceColors = listOf(
        CyberTeal,
        IndigoBright,
        EmeraldPrimary,
        GoldAccent
    )

    // Calculate distribution shares based on worker bot upgradeCost * level
    val totalCostSum: Double = workerBots.sumOf { bot -> bot.upgradeCost * bot.level.toDouble() }.coerceAtLeast(1.0)
    val botShares = workerBots.mapIndexed { idx, bot ->
        val botCost: Double = bot.upgradeCost * bot.level.toDouble()
        val shareRatio: Double = botCost / totalCostSum
        val allocatedCapital: Double = totalPortfolioValue * shareRatio
        val color = sliceColors.getOrElse(idx % sliceColors.size) { GoldAccent }
        BotDistributionItem(
            name = bot.name,
            role = bot.role,
            shareRatio = shareRatio,
            allocatedCapital = allocatedCapital,
            color = color,
            multiplier = bot.effectMultiplier * bot.level.toDouble()
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, IndigoBright.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("portfolio_distribution_chart")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(IndigoGradientStart),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (chartType == 0) Icons.Default.PieChart else Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "DESGLOSE DE PORTAFOLIO",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Rendimiento ROI & Distribución Yield Bots",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }

                // Switcher Tab Buttons
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkBackground)
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (chartType == 0) IndigoBright else Color.Transparent)
                            .clickable { chartType = 0 }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Distribución", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (chartType == 0) Color.White else TextSecondary)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (chartType == 1) IndigoBright else Color.Transparent)
                            .clickable { chartType = 1 }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Tendencia ROI", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (chartType == 1) Color.White else TextSecondary)
                    }
                }
            }

            if (chartType == 0) {
                // DONUT ASSET DISTRIBUTION CANVAS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .testTag("donut_chart_canvas"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            var startAngle = -90f
                            botShares.forEach { item ->
                                val sweep = (item.shareRatio * 360f).toFloat()
                                drawArc(
                                    color = item.color,
                                    startAngle = startAngle,
                                    sweepAngle = sweep - 2f, // gap
                                    useCenter = false,
                                    style = Stroke(width = 28.dp.toPx())
                                )
                                startAngle += sweep
                            }
                        }

                        // Inner Donut Text
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Multiplier", fontSize = 10.sp, color = TextSecondary)
                            Text(
                                text = "${String.format("%.2f", netMultiplier)}x",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldPrimary
                            )
                        }
                    }

                    // Legend List
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        botShares.forEach { item ->
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
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(item.color)
                                    )
                                    Text(item.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }

                                Text(
                                    text = "${(item.shareRatio * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = item.color
                                )
                            }
                        }
                    }
                }
            } else {
                // ROI PERFORMANCE TREND CANVAS
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkBackground)
                        .padding(12.dp)
                        .testTag("roi_trend_canvas"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val points = listOf(0.2f, 0.35f, 0.3f, 0.55f, 0.62f, 0.8f, 0.95f)
                        val w = size.width
                        val h = size.height
                        val path = Path()
                        val fillPath = Path()

                        points.forEachIndexed { index, value ->
                            val x = (w / (points.size - 1)) * index
                            val y = h - (value * h)
                            if (index == 0) {
                                path.moveTo(x, y)
                                fillPath.moveTo(x, h)
                                fillPath.lineTo(x, y)
                            } else {
                                path.lineTo(x, y)
                                fillPath.lineTo(x, y)
                            }

                            drawCircle(color = EmeraldPrimary, radius = 3.dp.toPx(), center = Offset(x, y))
                        }

                        fillPath.lineTo(w, h)
                        fillPath.close()

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(EmeraldPrimary.copy(alpha = 0.3f), Color.Transparent)
                            )
                        )

                        drawPath(
                            path = path,
                            color = EmeraldPrimary,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                        Text("+24.8% ROI Rendimiento 7D", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                    }
                }
            }

            // Summary Footer Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkBackground)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Capital Distribuido En Bots", fontSize = 10.sp, color = TextSecondary)
                        Text(
                            text = "$${String.format("%.2f", totalPortfolioValue)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Eficiencia Global Algoritmo", fontSize = 10.sp, color = TextSecondary)
                        Text(
                            text = "OPTIMIZADA (99.4%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberTeal
                        )
                    }
                }
            }
        }
    }
}

private data class BotDistributionItem(
    val name: String,
    val role: String,
    val shareRatio: Double,
    val allocatedCapital: Double,
    val color: Color,
    val multiplier: Double
)
