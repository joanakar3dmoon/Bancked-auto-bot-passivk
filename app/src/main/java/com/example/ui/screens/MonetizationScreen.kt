package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.AdMobBanner
import com.example.ui.components.BiometricPromptDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.BankedViewModel

@Composable
fun MonetizationScreen(
    viewModel: BankedViewModel,
    onOpenDeposit: () -> Unit,
    onOpenAdminWithdrawal: () -> Unit,
    onOpenRewardAd: () -> Unit,
    onOpenInterstitialAd: () -> Unit,
    onOpenSubscriptionCheckout: (tierName: String, price: Double) -> Unit
) {
    val account by viewModel.userAccount.collectAsState()
    val currentAccount = account ?: return

    var showBiometricTestDialog by remember { mutableStateOf(false) }
    var isBiometricRequirementEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // TOP HERO: ZONA ADMIN RETIROS & REAL PAYMENT GATEWAYS
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(IndigoGradientStart, Color(0xFF1E1B4B))
                        )
                    )
                    .border(1.5.dp, IndigoBright.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                    .testTag("admin_withdrawals_hero")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
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
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }

                            Column {
                                Text(
                                    text = "ZONA ADMIN RETIROS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = "PayPal · Wise · Stripe Webhooks · Crypto · Bizum",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.8f))
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(EmeraldPrimary)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("ACCESO LIBRE", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }

                    Text(
                        text = "Gestión total de fondos real, emisión de payouts instantáneos y consola de auditoría de Webhooks de pago para administrador.",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Button(
                        onClick = onOpenAdminWithdrawal,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("open_admin_withdrawal_btn")
                    ) {
                        Icon(Icons.Default.Output, contentDescription = null, tint = IndigoGradientStart)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ABRIR ZONA ADMIN RETIROS", color = IndigoGradientStart, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Top Card: Subscription Status & Recurring Payment Management
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberTeal.copy(alpha = 0.4f)),
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
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Autorenew,
                                contentDescription = null,
                                tint = CyberTeal,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Gestión de Suscripciones & Cobro Recurrente",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Auto-renewal, billing dates & recurring payment execution",
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
                            Text("Plan Activo", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary), fontSize = 11.sp)
                            Text(
                                text = currentAccount.subscriptionTier,
                                fontWeight = FontWeight.Black,
                                color = GoldAccent,
                                fontSize = 16.sp
                            )
                        }

                        Column {
                            Text("Límite de Capital Bot", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary), fontSize = 11.sp)
                            Text(
                                text = "$${String.format("%.0f", currentAccount.maxCapitalLimit)}",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 15.sp
                            )
                        }

                        Column {
                            Text("Auto-Renovación", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary), fontSize = 11.sp)
                            Text(
                                text = if (currentAccount.subscriptionAutoRenew) "ACTIVADA" else "PAUSADA",
                                fontWeight = FontWeight.Bold,
                                color = if (currentAccount.subscriptionAutoRenew) EmeraldPrimary else DangerRed,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Recurring billing controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.toggleSubscriptionAutoRenew() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("toggle_auto_renew_btn")
                        ) {
                            Icon(
                                if (currentAccount.subscriptionAutoRenew) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (currentAccount.subscriptionAutoRenew) "Pausar Renovación" else "Activar Auto-Renew",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { viewModel.processRecurringBillingCycle() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("process_recurring_charge_btn")
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cobrar Ciclo Recurrente", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section 1: Pasarelas de Pago & Cash Deposit
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = EmeraldPrimary)
                        Text(
                            text = "Pasarelas de Pago (Payment Gateways)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Text(
                        text = "Supported integration gateways: Visa / Mastercard, PayPal, Stripe Checkout, Apple Pay, and Web3 Crypto.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        fontSize = 11.sp
                    )

                    Button(
                        onClick = onOpenDeposit,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_payment_gateway_btn")
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Payment Gateway & Deposit Funds", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 2: Subscription Plans
        item {
            Text(
                text = "Subscription Tiers (Suscriptores)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }

        item {
            SubscriptionCard(
                tierName = "PRO TRADER",
                price = 9.99,
                perks = listOf(
                    "2.0x Passive Yield Yield Boost",
                    "Zero Brokerage Trading Fees",
                    "Priority AI Signals Execution",
                    "Ad-Free Premium Experience"
                ),
                isCurrent = currentAccount.subscriptionTier == "PRO",
                onSelect = { onOpenSubscriptionCheckout("PRO", 9.99) }
            )
        }

        item {
            SubscriptionCard(
                tierName = "INSTITUTIONAL WHALE",
                price = 29.99,
                perks = listOf(
                    "5.0x Passive Yield Speed Multiplier",
                    "Unlimited Worker Level Upgrades",
                    "Max Bot Allocation Slots",
                    "Institutional VIP Market Insights"
                ),
                isCurrent = currentAccount.subscriptionTier == "INSTITUTIONAL",
                isFeatured = true,
                onSelect = { onOpenSubscriptionCheckout("INSTITUTIONAL", 29.99) }
            )
        }

        item {
            SubscriptionCard(
                tierName = "VIP APEX MASTER",
                price = 99.99,
                perks = listOf(
                    "10.0x Passive Yield Speed Multiplier",
                    "Auto-Rebalance Portfolio AI Shield Engine",
                    "Unlimited $1,000,000 Bot Capital Limit",
                    "Instant Stripe/PayPal Webhook Auto-Payouts"
                ),
                isCurrent = currentAccount.subscriptionTier == "VIP_APEX",
                isFeatured = false,
                onSelect = { onOpenSubscriptionCheckout("VIP_APEX", 99.99) }
            )
        }

        // Section 3: Google Mobile Ads SDK Center
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.AdUnits, contentDescription = null, tint = GoldAccent)
                            Text(
                                text = "Google Mobile Ads SDK Center",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(GoldAccent.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("AdMob Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldAccent)
                        }
                    }

                    Text(
                        text = "Monetice la interacción con formatos Interstitial Fullscreen, Rewarded Video y Banners patrocinados.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        fontSize = 11.sp
                    )

                    // SDK Configuration Info Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkBackground)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("App ID: ca-app-pub-3904073712994742~3347511713", fontSize = 9.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = TextMuted)
                            Text("Banner Unit: ca-app-pub-3904073712994742/6300978111", fontSize = 9.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = EmeraldPrimary)
                            Text("Interstitial Unit: ca-app-pub-3904073712994742/1033173712", fontSize = 9.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = GoldAccent)
                            Text("Rewarded Unit: ca-app-pub-3904073712994742/5224354917", fontSize = 9.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, color = CyberTeal)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenInterstitialAd,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("monetization_interstitial_ad_btn")
                        ) {
                            Icon(Icons.Default.Fullscreen, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ad Interstitial", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = onOpenRewardAd,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberTeal),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("monetization_reward_ad_btn")
                        ) {
                            Icon(Icons.Default.OndemandVideo, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rewarded Video", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.simulateAdImpression() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("monetization_banner_impression_btn")
                    ) {
                        Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simular Impresión Banner (+$0.35)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 4: BiometricPrompt API Security Center
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("biometric_security_card")
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = EmeraldPrimary)
                            Text(
                                text = "Seguridad BiometricPrompt API",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(EmeraldPrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Hardware API Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        }
                    }

                    Text(
                        text = "Proteja las acciones de retiro de fondos y el acceso a su portafolio con huella dactilar o reconocimiento facial de nivel bancario.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        fontSize = 11.sp
                    )

                    // Toggle Switch for Biometric Requirement
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkBackground)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                            Text("Exigir Biometría para Retiros", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        Switch(
                            checked = isBiometricRequirementEnabled,
                            onCheckedChange = { isBiometricRequirementEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = EmeraldPrimary
                            ),
                            modifier = Modifier.testTag("toggle_biometric_requirement_switch")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showBiometricTestDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_biometric_prompt_btn")
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Probar Escáner", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = onOpenAdminWithdrawal,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("biometric_withdrawal_trigger_btn")
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Retiro Seguro", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showBiometricTestDialog) {
        BiometricPromptDialog(
            actionTitle = "Verificación de Seguridad Biométrica",
            actionSubtitle = "Escanee su huella dactilar o reconocimiento facial de prueba",
            onDismiss = { showBiometricTestDialog = false },
            onSuccessAuthenticated = {
                showBiometricTestDialog = false
                viewModel.setUiMessage("🔒 Biometría Verificada con Éxito. Acceso a Funciones Sensibles Autorizado.")
            }
        )
    }
}

@Composable
fun SubscriptionCard(
    tierName: String,
    price: Double,
    perks: List<String>,
    isCurrent: Boolean,
    isFeatured: Boolean = false,
    onSelect: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isFeatured) DarkSurface else DarkSurface.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isFeatured || isCurrent) 2.dp else 1.dp,
            color = if (isCurrent) EmeraldPrimary else if (isFeatured) GoldAccent else CardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subscription_card_$tierName")
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tierName,
                        fontWeight = FontWeight.Black,
                        color = if (isFeatured) GoldAccent else TextPrimary,
                        fontSize = 16.sp
                    )
                    if (isFeatured) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GoldAccent)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("POPULAR", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }
                    }
                }

                Text(
                    text = "$${String.format("%.2f", price)} / mo",
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary,
                    fontSize = 15.sp
                )
            }

            perks.forEach { perk ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(14.dp))
                    Text(text = perk, color = TextSecondary, fontSize = 11.sp)
                }
            }

            Button(
                onClick = onSelect,
                enabled = !isCurrent,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFeatured) GoldAccent else EmeraldPrimary,
                    disabledContainerColor = DarkSurfaceVariant
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isCurrent) "Current Active Tier" else "Subscribe via Payment Gateway",
                    color = if (!isCurrent) Color.Black else TextMuted,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
