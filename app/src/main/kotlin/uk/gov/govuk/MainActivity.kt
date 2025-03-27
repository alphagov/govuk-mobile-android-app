package uk.gov.govuk

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.ui.GovUkApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val _intentFlow: MutableSharedFlow<Intent> =
        MutableSharedFlow(replay = 1, extraBufferCapacity = 1)
    internal val intentFlow = _intentFlow.asSharedFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setIntentFlags()

        emitIntent(savedInstanceState)

        setContent {
            GovUkTheme {
                Surface(
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    color = GovUkTheme.colourScheme.surfaces.background
                ) {
                    GovUkApp(_intentFlow)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        _intentFlow.tryEmit(intent)
    }

    private fun setIntentFlags() {
        // FLAG_ACTIVITY_CLEAR_TASK prevents activity recreation when app is started from a deep link.
        // It must be used in conjunction with FLAG_ACTIVITY_NEW_TASK.
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
    }

    private fun emitIntent(savedInstanceState: Bundle?) {
        // Only emit intent when app launched from cold so deep links only ever run once
        savedInstanceState ?: run {
            _intentFlow.tryEmit(intent)
        }
    }
}
