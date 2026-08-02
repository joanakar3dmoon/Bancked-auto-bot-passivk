package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.theme.*
import kotlin.random.Random

@Composable
fun LiveMarketChart(
    selectedMarket: String = "Crypto AI Index",
    modifier: Modifier = Modifier
) {
    // Generate synthetic price points for the chart
    val points = remember(selectedMarket) {
        val list = mutableListOf<Float>()
        var start = 100f
        for (i in 0..24) {
            val delta = Random.nextFloat() * 12f - 5.5f
            start += delta
            if (start < 60f) start = 60f
            list.add(start)
        }
        list
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("live_market_chart_card"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = selectedMarket,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Real-Time 24h Algorithmic Feed • Live Pulse",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(EmeraldPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "LIVE +14.8%",
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            // Canvas Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceVariant)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val maxVal = points.maxOrNull() ?: 150f
                    val minVal = points.minOrNull() ?: 50f
                    val range = if (maxVal == minVal) 1f else maxVal - minVal

                    // Draw Horizontal Grid lines
                    val gridLines = 4
                    for (i in 1 until gridLines) {
                        val y = height * (i.toFloat() / gridLines)
                        drawLine(
                            color = CardBorder.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    // Build line path
                    val path = Path()
                    val stepX = width / (points.size - 1)

                    points.forEachIndexed { index, valF ->
                        val x = index * stepX
                        val normalizedY = 1f - ((valF - minVal) / range)
                        val y = (normalizedY * (height - 40f)) + 20f

                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            val prevX = (index - 1) * stepX
                            val prevNormalizedY = 1f - ((points[index - 1] - minVal) / range)
                            val prevY = (prevNormalizedY * (height - 40f)) + 20f

                            // Cubic curve for smooth line
                            val controlX1 = prevX + (stepX / 2f)
                            val controlY1 = prevY
                            val controlX2 = prevX + (stepX / 2f)
                            val controlY2 = y

                            path.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                        }
                    }

                    // Fill gradient under path
                    val fillPath = Path().apply {
                        addPath(path)
                        lineTo(width, height)
                        lineTo(0f, height)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                EmeraldPrimary.copy(alpha = 0.35f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw Stroke Line
                    drawPath(
                        path = path,
                        color = EmeraldPrimary,
                        style = Stroke(width = 4f)
                    )

                    // Draw last point pulsing circle
                    val lastX = width
                    val lastVal = points.last()
                    val lastNormalizedY = 1f - ((lastVal - minVal) / range)
                    val lastY = (lastNormalizedY * (height - 40f)) + 20f

                    drawCircle(
                        color = EmeraldPrimary,
                        radius = 8f,
                        center = Offset(lastX - 10f, lastY)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4f,
                        center = Offset(lastX - 10f, lastY)
                    )
                }
            }
        }
    }
}
