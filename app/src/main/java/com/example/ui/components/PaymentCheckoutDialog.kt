package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentCheckoutDialog(
    initialAmount: Double = 500.0,
    isSubscriptionMode: Boolean = false,
    subscriptionTierName: String = "",
    onDismiss: () -> Unit,
    onPaymentConfirmed: (amount: Double, method: String) -> Unit
) {
    var amountInput by remember { mutableStateOf(initialAmount.toString()) }
    var selectedMethod by remember { mutableStateOf("Visa / Mastercard") }
    var cardNumber by remember { mutableStateOf("4532 •••• •••• 8821") }
    var cardExpiry by remember { mutableStateOf("12/28") }
    var cardCvc by remember { mutableStateOf("942") }
    var isProcessing by remember { mutableStateOf(false) }

    val paymentGateways = listOf("Visa / Mastercard", "PayPal", "Stripe Pay", "Apple Pay", "Crypto BTC/USDT")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = "Payment Gateway",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isSubscriptionMode) "Upgrade to $subscriptionTierName" else "Secure Payment Gateway",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Encrypted SSL 256-Bit Checkout",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isSubscriptionMode) {
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Deposit Amount ($)") },
                        leadingIcon = {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = EmeraldPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = CardBorder,
                            focusedLabelColor = EmeraldPrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payment_amount_input")
                    )
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Subscription: $subscriptionTierName",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "$${String.format("%.2f", initialAmount)}/mo",
                                fontWeight = FontWeight.Black,
                                color = EmeraldPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Text(
                    text = "Select Pasarela de Pago (Gateway):",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                )

                // Gateway Selector Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    paymentGateways.chunked(2).forEach { rowGateways ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowGateways.forEach { gateway ->
                                val isSelected = selectedMethod == gateway
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) EmeraldPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 1.dp,
                                            color = if (isSelected) EmeraldPrimary else CardBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedMethod = gateway }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = gateway,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) EmeraldPrimary else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Card details form if Card or Stripe selected
                if (selectedMethod == "Visa / Mastercard" || selectedMethod == "Stripe Pay") {
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it },
                        label = { Text("Card Number") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = cardExpiry,
                            onValueChange = { cardExpiry = it },
                            label = { Text("Exp (MM/YY)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = cardCvc,
                            onValueChange = { cardCvc = it },
                            label = { Text("CVC") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else if (selectedMethod == "Crypto BTC/USDT") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Default.QrCode, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(32.dp))
                            Column {
                                Text("Web3 Direct Deposit", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 12.sp)
                                Text("0x71C...4A90 (Instant Confirmation)", color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Text(
                        text = "Real payment simulation with instant balance credit.",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedAmount = amountInput.toDoubleOrNull() ?: initialAmount
                    onPaymentConfirmed(parsedAmount, selectedMethod)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_payment_btn")
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Confirm Payment", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
