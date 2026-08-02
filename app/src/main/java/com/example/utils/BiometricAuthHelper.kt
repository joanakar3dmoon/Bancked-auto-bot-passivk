package com.example.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object BiometricAuthHelper {

    fun checkBiometricStatus(context: Context): BiometricStatusInfo {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        
        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatusInfo(
                isAvailable = true,
                statusText = "Sensor Biométrico Disponible y Registrado",
                code = "BIOMETRIC_SUCCESS"
            )
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatusInfo(
                isAvailable = false,
                statusText = "Sin Hardware Biométrico (Simulación Activa)",
                code = "BIOMETRIC_ERROR_NO_HARDWARE"
            )
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatusInfo(
                isAvailable = false,
                statusText = "Hardware Biométrico Temporalmente No Disponible",
                code = "BIOMETRIC_ERROR_HW_UNAVAILABLE"
            )
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatusInfo(
                isAvailable = false,
                statusText = "No hay Huella o Rostro Registrado (Usar PIN)",
                code = "BIOMETRIC_ERROR_NONE_ENROLLED"
            )
            else -> BiometricStatusInfo(
                isAvailable = false,
                statusText = "Autenticación por PIN de Respaldo",
                code = "BIOMETRIC_UNKNOWN"
            )
        }
    }

    fun launchNativeBiometricPrompt(
        activity: FragmentActivity,
        title: String = "Autenticación Biométrica Requerida",
        subtitle: String = "Escanee su huella dactilar o reconocimiento facial para autorizar la transacción",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("HUELLA_NO_RECONOCIDA: Intente de nuevo")
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or 
                BiometricManager.Authenticators.BIOMETRIC_WEAK or 
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        try {
            val biometricPrompt = BiometricPrompt(activity, executor, callback)
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            onError("Error al iniciar BiometricPrompt: ${e.message}")
        }
    }
}

data class BiometricStatusInfo(
    val isAvailable: Boolean,
    val statusText: String,
    val code: String
)
