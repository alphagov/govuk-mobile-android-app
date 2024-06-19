package uk.govuk.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import uk.govuk.app.onboarding.ui.OnboardingRoute
import uk.govuk.app.ui.theme.GovUkAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
