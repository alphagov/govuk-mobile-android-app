package uk.gov.govuk.login

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators
import javax.inject.Inject

internal class LoginFeatureProvider @Inject constructor(
    private val biometricManager: BiometricManager
) : LoginFeature {

    override fun isAuthenticationEnabled(): Boolean {
        val result  = biometricManager.canAuthenticate(
            Authenticators.BIOMETRIC_STRONG or Authenticators.DEVICE_CREDENTIAL
        )
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

}