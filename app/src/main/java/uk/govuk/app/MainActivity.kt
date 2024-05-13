package uk.govuk.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.ui.theme.GovukmobileandroidappTheme
import uk.govuk.homepage.HomepageStart
import uk.govuk.onboarding.OnboardingStart
import uk.govuk.services.ServicesStart

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GovukmobileandroidappTheme {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        Greeting("GOV.UK Android App")
                        OnboardingStart()
                        HomepageStart()
                        ServicesStart()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Hello from $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GovukmobileandroidappTheme {
        Greeting("GOV.UK Android App")
    }
}
