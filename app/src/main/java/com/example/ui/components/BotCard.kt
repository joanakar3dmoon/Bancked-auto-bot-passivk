package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.MarketBotEntity
import com.example.ui.theme.*

@Composable
fun BotCard(
    bot: MarketBotEntity,
    onToggleActive: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("market_bot_card_${bot.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (bot.isActive) DarkSurface else DarkSurface.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (bot.isActive) 1.5.dp else 1.dp,
            color = if (bot.isActive) IndigoAccent.copy(alpha = 0.5f) else CardBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Bot Name + Active Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (bot.isActive) IndigoAccent.copy(alpha = 0.15f) else DarkSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = if (bot.isActive) IndigoBright else TextMuted,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Column {
                        Text(
                            text = bot.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(IndigoAccent.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = bot.targetMarket,
                                    fontSize = 10.sp,
                                    color = IndigoBright,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkSurfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = bot.riskLevel,
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Switch(
                    checked = bot.isActive,
                    onCheckedChange = { onToggleActive() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = EmeraldPrimary,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DarkSurfaceVariant
                    ),
                    modifier = Modifier.testTag("toggle_bot_switch_${bot.id}")
                )
            }

            HorizontalDivider(color = CardBorder.copy(alpha = 0.5f))

            // Bot Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("AI Accuracy", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary), fontSize = 11.sp)
                    Text("${bot.accuracy}%", fontWeight = FontWeight.Bold, color = EmeraldPrimary, fontSize = 15.sp)
                }

                Column {
                    Text("Allocated Capital", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary), fontSize = 11.sp)
                    Text("$${String.format("%.2f", bot.allocatedCapital)}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 15.sp)
                }

                Column {
                    Text("24h ROI", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary), fontSize = 11.sp)
                    Text("+${bot.roi24h}%", fontWeight = FontWeight.Bold, color = GoldAccent, fontSize = 15.sp)
                }
            }

            // Strategy & Last Action
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Strategy: ${bot.strategyDescription}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        fontSize = 11.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(12.dp))
                        Text(
                            text = bot.lastAction,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = EmeraldPrimary
                        )
                    }
                }
            }
        }
    }
}
