package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.BuildConfig
import com.example.data.db.WithdrawalRequestEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminWithdrawalDialog(
    cashBalance: Double,
    withdrawalRequests: List<WithdrawalRequestEntity>,
    onDismiss: () -> Unit,
    onSubmitWithdrawal: (amount: Double, gateway: String, destination: String, note: String) -> Unit,
    onAdminUpdateStatus: (requestId: Int, newStatus: String, note: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Submit Withdrawal, 1: Admin Webhook Ledger
    var selectedGateway by remember { mutableStateOf("PAYPAL") } // "PAYPAL", "WISE", "STRIPE_WEBHOOK", "CRYPTO_WALLET", "BIZUM"

    var withdrawalAmountInput by remember { mutableStateOf("250.00") }
    var destinationInput by remember { mutableStateOf("admin@paypal.com") }
    var stripeWebhookUrl by remember { mutableStateOf("https://api.stripe.com/v1/payouts/webhooks") }
    var stripeSecretKey by remember { mutableStateOf(BuildConfig.STRIPE_SECRET_KEY.ifEmpty { "whsec_live_9988210041" }) }
    var customNoteInput by remember { mutableStateOf("Instant Withdrawal Execution") }

    var showBiometricAuthPrompt by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .testTag("admin_withdrawal_dialog"),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            tonalElevation = 12.dp,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, IndigoBright.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
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
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White)
                        }
                        Column {
                            Text(
                                text = "ZONA ADMIN RETIROS",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                text = "Pasarelas Real & Control de Webhooks",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                // Balance summary strip
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
                        Text("Balance Liquido Disponible", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "$${String.format("%.2f", cashBalance)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = EmeraldPrimary
                        )
                    }
                }

                // Tab Switcher: 0: Pasarelas Payout, 1: Admin Webhooks Console
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkBackground,
                    contentColor = IndigoBright,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = IndigoBright
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Pasarelas Retiro", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("admin_tab_gateways")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Console Webhooks (${withdrawalRequests.size})", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("admin_tab_webhooks")
                    )
                }

                if (selectedTab == 0) {
                    // GATEWAY SELECTION WRAPPER
                    Text("Seleccionar Pasarela de Pago:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)

                    val gateways = listOf(
                        Triple("PAYPAL", "PayPal", Icons.Default.AccountBalanceWallet),
                        Triple("WISE", "Wise (FX)", Icons.Default.CurrencyExchange),
                        Triple("STRIPE_WEBHOOK", "Stripe Webhook", Icons.Default.Webhook),
                        Triple("CRYPTO_WALLET", "Crypto Wallet", Icons.Default.QrCodeScanner),
                        Triple("BIZUM", "Bizum Payout", Icons.Default.PhoneIphone)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        gateways.forEach { (key, name, icon) ->
                            val isSelected = selectedGateway == key
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) IndigoGradientStart else DarkSurfaceVariant)
                                    .border(
                                        1.dp,
                                        if (isSelected) IndigoBright else CardBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        selectedGateway = key
                                        destinationInput = when (key) {
                                            "PAYPAL" -> "finance@app-payouts.paypal.com"
                                            "WISE" -> "ES91 2100 0418 4502 0005 1199"
                                            "STRIPE_WEBHOOK" -> "acct_1N92X88K9012"
                                            "CRYPTO_WALLET" -> "0x71C7656EC7ab88b098defB751B7401B5f6d8976F"
                                            "BIZUM" -> "+34 699 123 456"
                                            else -> ""
                                        }
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = name,
                                        tint = if (isSelected) Color.White else TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = name,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // GATEWAY SPECIFIC FORM
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = withdrawalAmountInput,
                            onValueChange = { withdrawalAmountInput = it },
                            label = { Text("Monto a Retirar ($)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdrawal_amount_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = IndigoBright,
                                unfocusedBorderColor = CardBorder
                            )
                        )

                        OutlinedTextField(
                            value = destinationInput,
                            onValueChange = { destinationInput = it },
                            label = {
                                Text(
                                    when (selectedGateway) {
                                        "PAYPAL" -> "Email de PayPal"
                                        "WISE" -> "IBAN / Cuenta Wise"
                                        "STRIPE_WEBHOOK" -> "Stripe Account ID / Webhook Target"
                                        "CRYPTO_WALLET" -> "Dirección Wallet (BTC / ETH / SOL)"
                                        "BIZUM" -> "Teléfono Bizum"
                                        else -> "Destino"
                                    }
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("withdrawal_destination_field"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = IndigoBright,
                                unfocusedBorderColor = CardBorder
                            )
                        )

                        if (selectedGateway == "STRIPE_WEBHOOK") {
                            OutlinedTextField(
                                value = stripeWebhookUrl,
                                onValueChange = { stripeWebhookUrl = it },
                                label = { Text("Endpoint Webhook URL") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyberTeal)
                            )
                        }

                        // Gateway Details Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkBackground)
                                .padding(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                Text(
                                    text = when (selectedGateway) {
                                        "PAYPAL" -> "Procesamiento inmediato con Webhook IPN Sandbox/Live PayPal API."
                                        "WISE" -> "Transferencia SWIFT/SEPA con comisión garantizada del 0.15%."
                                        "STRIPE_WEBHOOK" -> "Firma HMAC SHA256 activa. Notificación automática event `payout.paid`."
                                        "CRYPTO_WALLET" -> "Red Ethereum/Solana con tarifa de Gas adaptativa estimada: $1.20."
                                        "BIZUM" -> "Pago instantáneo en España mediante API Bizum empresarial."
                                        else -> "Pasarela segura con encriptación SSL 256-bit."
                                    },
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val amt = withdrawalAmountInput.toDoubleOrNull() ?: 0.0
                                pendingAction = {
                                    onSubmitWithdrawal(amt, selectedGateway, destinationInput, customNoteInput)
                                    onDismiss()
                                }
                                showBiometricAuthPrompt = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoGradientStart),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("submit_withdrawal_button")
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, tint = GoldAccent)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AUTORIZAR RETIRO CON BIOMETRÍA ($selectedGateway)", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                } else {
                    // ADMIN WEBHOOK CONSOLE & HISTORY
                    if (withdrawalRequests.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay solicitudes de retiro registradas.", color = TextMuted, fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.height(280.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(withdrawalRequests) { req ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = DarkBackground),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
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
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(IndigoAccent.copy(alpha = 0.2f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(req.gateway, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IndigoBright)
                                                }
                                                Text("#${req.id}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            }

                                            Text(
                                                text = "$${String.format("%.2f", req.amount)}",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 14.sp,
                                                color = EmeraldPrimary
                                            )
                                        }

                                        Text("Destino: ${req.destinationAccount}", fontSize = 11.sp, color = TextSecondary)
                                        Text(
                                            text = "Webhook Tx: ${req.webhookTxId}",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CyberTeal
                                        )

                                        // Webhook Payload Simulation Box
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(DarkSurfaceVariant)
                                                .padding(6.dp)
                                        ) {
                                            Text(
                                                text = "{\"event\": \"payout.created\", \"gateway\": \"${req.gateway}\", \"tx\": \"${req.webhookTxId}\", \"status\": \"${req.status}\"}",
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = TextSecondary
                                            )
                                        }

                                        // Admin Status Action Buttons
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    onAdminUpdateStatus(req.id, "APPROVED", "Approved via Admin Webhook Console")
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimaryDark),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f),
                                                contentPadding = PaddingValues(2.dp)
                                            ) {
                                                Text("Aprobar Webhook", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }

                                            Button(
                                                onClick = {
                                                    onAdminUpdateStatus(req.id, "REJECTED", "Refunded by Admin")
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f),
                                                contentPadding = PaddingValues(2.dp)
                                            ) {
                                                Text("Rechazar/Reembolso", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBiometricAuthPrompt) {
        BiometricPromptDialog(
            actionTitle = "Autorización de Retiro de Fondos",
            actionSubtitle = "Escanee su huella o rostro para autorizar el desembolso seguro",
            onDismiss = { showBiometricAuthPrompt = false },
            onSuccessAuthenticated = {
                showBiometricAuthPrompt = false
                pendingAction?.invoke()
                pendingAction = null
            }
        )
    }
}
