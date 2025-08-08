package uk.gov.govuk

import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import uk.gov.govuk.design.ui.theme.GovUkTheme

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

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
                    AndroidView(factory = {
                        ImageView(it).apply {
                            val source = ImageDecoder.createSource(context.resources, R.drawable.animation)
                            val drawable = ImageDecoder.decodeDrawable(source)
                            setImageDrawable(drawable)
                            if (drawable is AnimatedImageDrawable) {
                                drawable.start()
                            }
                        }
                    })
                }
            }
        }
    }

    /*
    private val _intentFlow: MutableSharedFlow<Intent> =
        MutableSharedFlow(replay = 1)
    internal val intentFlow = _intentFlow.asSharedFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

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
     */
}