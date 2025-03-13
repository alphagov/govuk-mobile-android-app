package uk.govuk.app.biometrics

import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class BiometricsManager(
    private val activity: FragmentActivity,
) {
    private val resultChannel = Channel<BiometricResult>()
    val results: Flow<BiometricResult> = resultChannel.receiveAsFlow()

    fun showPrompt(
        title: String,
        subtitle: String,
        description: String,
        negativeButtonText: String,
        allowDeviceCredential: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R,
    ) {
        val biometricManager = BiometricManager.from(activity)
        val authenticators = if (allowDeviceCredential) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else BIOMETRIC_STRONG

        val biometricAuthenticationIsPossible = checkBiometricAvailability(biometricManager, authenticators)
        if (!biometricAuthenticationIsPossible) {
            return
        }

        val promptInfo =
            createPromptInfo(title, subtitle, description, negativeButtonText, authenticators)

        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errorString: CharSequence) {
                    // TODO: do we need finer grained error handling here?
                    val error = BiometricError.fromErrorCode(errorCode, errorString.toString())
                    resultChannel.trySend(BiometricResult.Error(error))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    resultChannel.trySend(BiometricResult.Success)
                }

                override fun onAuthenticationFailed() {
                    resultChannel.trySend(BiometricResult.Failed)
                }
            }
        )

        prompt.authenticate(promptInfo)
    }

    private fun createPromptInfo(
        title: String,
        subtitle: String,
        description: String,
        negativeButtonText: String,
        authenticators: Int
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)
            .apply {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    setNegativeButtonText(negativeButtonText)
                }
            }.build()
    }

    private fun checkBiometricAvailability(
        biometricManager: BiometricManager,
        authenticators: Int
    ): Boolean {
        return when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                true
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(BiometricResult.Unavailable(BiometricError.HardwareUnavailable))
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                resultChannel.trySend(BiometricResult.Unavailable(BiometricError.NoHardware))
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(BiometricResult.Unavailable(BiometricError.NoneEnrolled))
                false
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                resultChannel.trySend(BiometricResult.Unavailable(BiometricError.SecurityUpdateRequired))
                false
            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                resultChannel.trySend(BiometricResult.Unavailable(BiometricError.Unsupported))
                false
            }

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                resultChannel.trySend(BiometricResult.Unavailable(BiometricError.Unknown))
                false
            }

            else -> {
                resultChannel.trySend(BiometricResult.Unavailable(BiometricError.Unknown))
                false
            }
        }
    }

    sealed interface BiometricResult {
        data class Unavailable(val error: BiometricError) : BiometricResult
        data object Success : BiometricResult
        data object Failed : BiometricResult
        data class Error(val error: BiometricError) : BiometricResult
    }

    sealed class BiometricError(val message: String) {
        data object HardwareUnavailable : BiometricError("Biometric hardware is unavailable.")
        data object NoHardware : BiometricError("No biometric hardware found.")
        data object NoneEnrolled : BiometricError("No biometrics enrolled.")
        data object SecurityUpdateRequired : BiometricError("Security update required.")
        data object Unsupported : BiometricError("Biometric authentication is unsupported.")
        data object Unknown : BiometricError("An unknown error occurred.")
        data class AuthenticationError(val code: Int, val error: String) :
            BiometricError("Authentication error with code: $code: $error")

        companion object {
            fun fromErrorCode(code: Int, error: String): BiometricError {
                return when (code) {
                    BiometricPrompt.ERROR_HW_UNAVAILABLE -> HardwareUnavailable
                    BiometricPrompt.ERROR_NO_BIOMETRICS -> NoneEnrolled
                    BiometricPrompt.ERROR_HW_NOT_PRESENT -> NoHardware
                    else -> AuthenticationError(code, error)
                }
            }
        }
    }
}
