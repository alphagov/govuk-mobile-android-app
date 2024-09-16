package uk.govuk.app.ui

import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import uk.govuk.app.R

@Composable
internal fun SplashScreen(
    onSplashDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.app_splash)
        )

        var state = animateLottieCompositionAsState(composition = composition)

        // Handle cases where animation is disabled...
        if (areAnimationsDisabled(LocalContext.current)) {
            state = animateLottieCompositionAsState(composition = composition, isPlaying = false)
            LaunchedEffect(true) {
                delay(6000) // wait for 6 seconds
                onSplashDone()
            }
            // Animations are enabled...
        } else {
            LaunchedEffect(state.progress) {
                if (state.progress == 1f) {
                    onSplashDone()
                }
            }
        }

        val altText: String = stringResource(id = uk.govuk.app.design.R.string.gov_uk_alt_text)

        LottieAnimation(
            composition = composition,
            progress = { state.progress },
            modifier = Modifier.semantics {
                contentDescription = altText
            }
        )
    }
}

fun areAnimationsDisabled(context: Context): Boolean {
    val animatorDurationScale = Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    )
    return animatorDurationScale == 0f
}
