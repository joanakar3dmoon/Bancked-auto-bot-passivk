package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.WorkerBotEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PotentialYieldCalculator(
    workerBots: List<WorkerBotEntity>,
    onUpgradeOrDeployBot: (WorkerBotEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var amountInput by remember { mutableStateOf("5000") }
    var selectedMonths by remember { mutableIntStateOf(12) } // 1, 6, 12, 36 months
    var selectedBotId by remember { mutableStateOf(workerBots.firstOrNull()?.id ?: "") }

    val amountDouble = amountInput.toDoubleOrNull() ?: 0.0
    val selectedBot = workerBots.find { it.id == selectedBotId } ?: workerBots.firstOrNull()

    // APY Estimation Formula:
    // Base APY depends on worker bot multiplier & level
    val botMultiplier = selectedBot?.let { it.effectMultiplier * it.level } ?: 1.15
    val estimatedAnnualRate = 0.08 + (botMultiplier - 1.0) * 0.12 // e.g. 1.25x -> ~11% APY compound
    val monthlyRate = estimatedAnnualRate / 12.0
    
    // Compound interest: A = P * (1 + r)^n
    val projectedTotal = amountDouble * Math.pow(1.0 + monthlyRate, selectedMonths.toDouble())
    val totalProfit = (projectedTotal - amountDouble).coerceAtLeast(0.0)
    val monthlyYieldAvg = if (selectedMonths > 0) totalProfit / selectedMonths else 0.0
    val netRoiPercentage = if (amountDouble > 0) (totalProfit / amountDouble) * 100.0 else 0.0

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldAccent.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("potential_yield_calculator")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
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
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "CALCULADORA DE RENDIMIENTO",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "Proyección Passive Income & Elección de Bot",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EmeraldPrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("APY INTERACTIVO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                }
            }

            // Input 1: Investment Capital Preset Chips & TextField
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Inversión Estimada ($)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it.filter { char -> char.isDigit() || char == '.' } },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = EmeraldPrimary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("calculator_amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = CardBorder
                    )
                )

                // Quick Amount Presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1000, 2500, 5000, 10000, 25000).forEach { preset ->
                        val isSelected = amountInput == preset.toString()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GoldAccent else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) GoldAccent else CardBorder, RoundedCornerShape(8.dp))
                                .clickable { amountInput = preset.toString() }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$${preset / 1000}k",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else TextPrimary
                            )
                        }
                    }
                }
            }

            // Input 2: Timeframe Horizon Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Horizonte Temporal (Meses)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)

                val timeframes = listOf(
                    1 to "1 Mes",
                    6 to "6 Meses",
                    12 to "1 Año",
                    36 to "3 Años"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    timeframes.forEach { (months, label) ->
                        val isSelected = selectedMonths == months
                        val testTagKey = when(months) {
                            1 -> "calculator_timeframe_1m"
                            6 -> "calculator_timeframe_6m"
                            12 -> "calculator_timeframe_1y"
                            else -> "calculator_timeframe_3y"
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) IndigoGradientStart else DarkSurfaceVariant)
                                .border(1.dp, if (isSelected) IndigoBright else CardBorder, RoundedCornerShape(10.dp))
                                .clickable { selectedMonths = months }
                                .padding(vertical = 8.dp)
                                .testTag(testTagKey),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }

            // Input 3: Worker Bot Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Seleccionar Yield Bot para Simular:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    workerBots.forEach { bot ->
                        val isSelected = bot.id == selectedBotId
                        val botMult = bot.effectMultiplier * bot.level

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) IndigoGradientStart.copy(alpha = 0.6f) else DarkBackground)
                                .border(1.dp, if (isSelected) IndigoBright else CardBorder, RoundedCornerShape(12.dp))
                                .clickable { selectedBotId = bot.id }
                                .padding(12.dp)
                                .testTag("bot_option_${bot.id}")
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
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedBotId = bot.id },
                                        colors = RadioButtonDefaults.colors(selectedColor = GoldAccent)
                                    )

                                    Column {
                                        Text(bot.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(
                                            text = "${bot.role} · Nivel ${bot.level}",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CyberTeal.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${String.format("%.2f", botMult)}x Multiplicador",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberTeal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Results Card: Projected Return & Net ROI Breakdown
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(DarkBackground, Color(0xFF0F172A))
                        )
                    )
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .testTag("projected_yield_result")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Retorno Total Proyectado", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "$${String.format("%.2f", projectedTotal)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = EmeraldPrimary
                        )
                    }

                    Divider(color = CardBorder.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Ganancia Neta (Passive)", fontSize = 10.sp, color = TextSecondary)
                            Text(
                                text = "+$${String.format("%.2f", totalProfit)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccent
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Promedio Mensual", fontSize = 10.sp, color = TextSecondary)
                            Text(
                                text = "$${String.format("%.2f", monthlyYieldAvg)}/mes",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("ROI Neto Acumulado", fontSize = 10.sp, color = TextSecondary)
                            Text(
                                text = "+${String.format("%.1f", netRoiPercentage)}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldPrimary
                            )
                        }
                    }
                }
            }

            // Action Button: Deploy or Upgrade Selected Bot Directly
            if (selectedBot != null) {
                Button(
                    onClick = { onUpgradeOrDeployBot(selectedBot) },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("deploy_calculated_bot_btn")
                ) {
                    Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MEJORAR / DESPLEGAR ${selectedBot.name.uppercase()} ($${String.format("%.0f", selectedBot.upgradeCost)})",
                        fontWeight = FontWeight.Black,
                        color = Color.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
