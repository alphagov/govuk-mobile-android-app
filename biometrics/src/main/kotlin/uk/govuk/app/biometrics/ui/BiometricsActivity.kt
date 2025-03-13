package uk.govuk.app.biometrics.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.biometrics.BiometricsManager

class BiometricsActivity : AppCompatActivity() {
    private val biometricsManager by lazy {
        BiometricsManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GovUkTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val biometricResult by biometricsManager.results.collectAsState(initial = null)

                    val enrollLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult(),
                        onResult = { result ->
                            when (result.resultCode) {
                                RESULT_OK -> Toast.makeText(
                                    this,
                                    "Biometrics successfully enrolled",
                                    Toast.LENGTH_SHORT
                                ).show()

                                RESULT_CANCELED -> Toast.makeText(
                                    this,
                                    "Biometrics not enrolled",
                                    Toast.LENGTH_SHORT
                                ).show()

                                RESULT_FIRST_USER -> Toast.makeText(
                                    this,
                                    "Biometrics first user",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )

                    LaunchedEffect(biometricResult) {
                        if (biometricResult is BiometricsManager.BiometricResult.Unavailable) {
                            if ((biometricResult as BiometricsManager.BiometricResult.Unavailable).error == BiometricsManager.BiometricError.NoneEnrolled) {
                                if (Build.VERSION.SDK_INT >= 30) {
                                    val enrollIntent =
                                        Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                            putExtra(
                                                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                            )
                                        }
                                    enrollLauncher.launch(enrollIntent)
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.padding(innerPadding)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                biometricsManager.showPrompt(
                                    title = "Authentication",
                                    subtitle = "Authentication is required",
                                    description = "Please authenticate to continue",
                                    negativeButtonText = "Login with password"
                                )
                            },
                        ) {
                            Text(text = "Authenticate")
                        }

                        biometricResult?.let { result ->
                            Text(
                                when (result) {
                                    is BiometricsManager.BiometricResult.Success -> "Success"
                                    is BiometricsManager.BiometricResult.Error -> "Error: ${result.error}"
                                    is BiometricsManager.BiometricResult.Failed -> "Failed"
                                    is BiometricsManager.BiometricResult.Unavailable -> "Unavailable"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
