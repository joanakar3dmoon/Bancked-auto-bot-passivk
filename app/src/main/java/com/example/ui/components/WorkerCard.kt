package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Upgrade
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
import com.example.data.db.WorkerBotEntity
import com.example.ui.theme.*

@Composable
fun WorkerCard(
    worker: WorkerBotEntity,
    onToggleActive: () -> Unit,
    onUpgrade: () -> Unit,
    userBalance: Double,
    modifier: Modifier = Modifier
) {
    val canAfford = userBalance >= worker.upgradeCost

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("worker_bot_card_${worker.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (worker.isActive) DarkSurface else DarkSurface.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (worker.isActive) 1.5.dp else 1.dp,
            color = if (worker.isActive) GoldAccent.copy(alpha = 0.5f) else CardBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Worker Name + Level + Active Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = worker.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(GoldAccent)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "LVL ${worker.level}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }

                        Text(
                            text = worker.role,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = worker.isActive,
                    onCheckedChange = { onToggleActive() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = GoldAccent,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DarkSurfaceVariant
                    ),
                    modifier = Modifier.testTag("toggle_worker_switch_${worker.id}")
                )
            }

            HorizontalDivider(color = CardBorder.copy(alpha = 0.5f))

            // Stats: Yield Multiplier boost
            val currentMultiplier = 1.0 + ((worker.effectMultiplier - 1.0) * worker.level)
            val nextMultiplier = 1.0 + ((worker.effectMultiplier - 1.0) * (worker.level + 1))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Yield Efficiency", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary), fontSize = 11.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                        Text(
                            text = "${String.format("%.2f", currentMultiplier)}x Boost",
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent,
                            fontSize = 16.sp
                        )
                    }
                }

                Button(
                    onClick = onUpgrade,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldAccent,
                        disabledContainerColor = DarkSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("upgrade_worker_btn_${worker.id}")
                ) {
                    Icon(Icons.Default.Upgrade, contentDescription = null, tint = if (canAfford) Color.Black else TextMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Upgrade ($${String.format("%.0f", worker.upgradeCost)})",
                        color = if (canAfford) Color.Black else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = worker.description,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                fontSize = 11.sp
            )
        }
    }
}
