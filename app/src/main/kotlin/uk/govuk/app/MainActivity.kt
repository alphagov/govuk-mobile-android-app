package uk.govuk.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.ui.GovUkApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GovUkTheme {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    color = GovUkTheme.colourScheme.surfaces.background
                ) {
                    GovUkApp()
                }
            }
        }
    }
}
