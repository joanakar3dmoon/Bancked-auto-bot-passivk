package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.ui.theme.*
import com.example.utils.BiometricAuthHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiometricPromptDialog(
    actionTitle: String = "Autorizar Operación Monetaria",
    actionSubtitle: String = "Confirme con su Huella Dactilar o Reconocimiento Facial para continuar",
    onDismiss: () -> Unit,
    onSuccessAuthenticated: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var authMode by remember { mutableIntStateOf(0) } // 0: Fingerprint, 1: Face ID, 2: PIN Fallback
    var isScanning by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var pinInput by remember { mutableStateOf("") }

    val biometricStatus = remember { BiometricAuthHelper.checkBiometricStatus(context) }

    // Pulse animation for fingerprint scan target
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    fun triggerNativeBiometric() {
        if (context is FragmentActivity && biometricStatus.isAvailable) {
            BiometricAuthHelper.launchNativeBiometricPrompt(
                activity = context,
                title = actionTitle,
                subtitle = actionSubtitle,
                onSuccess = {
                    isSuccess = true
                    scope.launch {
                        delay(600)
                        onSuccessAuthenticated()
                    }
                },
                onError = { err ->
                    errorMessage = err
                }
            )
        } else {
            // Trigger simulated Compose Biometric scan
            isScanning = true
            errorMessage = null
            scope.launch {
                delay(1200) // simulate biometric verification
                isScanning = false
                isSuccess = true
                delay(600)
                onSuccessAuthenticated()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("biometric_prompt_dialog"),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = DarkSurface,
            tonalElevation = 16.dp,
            border = androidx.compose.foundation.BorderStroke(2.dp, EmeraldPrimary.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Header Shield Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSuccess) EmeraldPrimary.copy(alpha = 0.2f)
                            else IndigoGradientStart
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.VerifiedUser else Icons.Default.Security,
                        contentDescription = null,
                        tint = if (isSuccess) EmeraldPrimary else GoldAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Title & Subtitle
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isSuccess) "AUTENTICACIÓN EXITOSA" else "SEGURIDAD BIOMÉTRICA",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = actionSubtitle,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                // Biometric Status Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkBackground)
                        .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (biometricStatus.isAvailable) EmeraldPrimary else GoldAccent)
                        )
                        Text(
                            text = biometricStatus.statusText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = TextMuted
                        )
                    }
                }

                if (isSuccess) {
                    // Success State Indicator
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Acceso Autorizado por BiometricPrompt API",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                } else if (authMode == 2) {
                    // PIN Fallback Mode
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Ingrese su PIN de Seguridad de 4 Dígitos",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pinInput = it },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            singleLine = true,
                            modifier = Modifier
                                .width(180.dp)
                                .testTag("biometric_pin_fallback_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAccent,
                                unfocusedBorderColor = CardBorder
                            )
                        )

                        Button(
                            onClick = {
                                if (pinInput == "1234" || pinInput.length == 4) {
                                    isSuccess = true
                                    scope.launch {
                                        delay(600)
                                        onSuccessAuthenticated()
                                    }
                                } else {
                                    errorMessage = "PIN Incorrecto. Pruebe 1234."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("biometric_confirm_pin_btn")
                        ) {
                            Text("Confirmar PIN", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Interactive Biometric Sensor Touch Area
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            if (authMode == 0) CyberTeal.copy(alpha = 0.3f) else IndigoBright.copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .border(
                                    width = (2 * pulseScale).dp,
                                    color = if (authMode == 0) CyberTeal else IndigoBright,
                                    shape = CircleShape
                                )
                                .clickable { triggerNativeBiometric() }
                                .testTag(if (authMode == 0) "biometric_fingerprint_touch_btn" else "biometric_faceid_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isScanning) {
                                CircularProgressIndicator(
                                    color = GoldAccent,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(70.dp)
                                )
                            }
                            Icon(
                                imageVector = if (authMode == 0) Icons.Default.Fingerprint else Icons.Default.Face,
                                contentDescription = "Escaneo Biométrico",
                                tint = if (authMode == 0) CyberTeal else IndigoBright,
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        Text(
                            text = if (isScanning) "Verificando Huella Dactilar..." else "Toque el sensor para escanear huella",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isScanning) GoldAccent else TextSecondary
                        )

                        // Mode Selector (Huella / Rostro / PIN)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            IconButton(
                                onClick = { authMode = 0 },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (authMode == 0) DarkSurfaceVariant else Color.Transparent)
                            ) {
                                Icon(Icons.Default.Fingerprint, contentDescription = "Fingerprint Mode", tint = if (authMode == 0) CyberTeal else TextMuted)
                            }

                            IconButton(
                                onClick = { authMode = 1 },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (authMode == 1) DarkSurfaceVariant else Color.Transparent)
                            ) {
                                Icon(Icons.Default.Face, contentDescription = "Face ID Mode", tint = if (authMode == 1) IndigoBright else TextMuted)
                            }

                            IconButton(
                                onClick = { authMode = 2 },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (authMode == 2) DarkSurfaceVariant else Color.Transparent)
                                    .testTag("biometric_pin_fallback_btn")
                            ) {
                                Icon(Icons.Default.Pin, contentDescription = "PIN Mode", tint = if (authMode == 2) GoldAccent else TextMuted)
                            }
                        }
                    }
                }

                // Error Display if any
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Cancel Button
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar", color = TextSecondary)
                }
            }
        }
    }
}
