package uk.govuk.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import uk.govuk.app.onboarding.ui.OnboardingRoute
import uk.govuk.app.ui.theme.GovUkAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            GovUkAppTheme {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    GovUkApp()
                    OnboardingRoute()
                }
            }
        }
    }
}
