package uk.gov.govuk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import uk.gov.govuk.R
import uk.gov.govuk.design.ui.extension.areAnimationsEnabled
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun SplashScreen(
    onSplashDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GovUkTheme.colourScheme.surfaces.splash),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.app_splash)
        )

        var state = animateLottieCompositionAsState(composition = composition)

        if (LocalContext.current.areAnimationsEnabled()) {
            LaunchedEffect(state.progress) {
                if (state.progress == 1f) {
                    onSplashDone()
                }
            }
        } else {
            // Handle cases where animations are disabled...
            state = animateLottieCompositionAsState(composition = composition, isPlaying = false)
            LaunchedEffect(true) {
                delay(1000)
                onSplashDone()
            }
        }

        val altText: String = stringResource(id = uk.gov.govuk.design.R.string.gov_uk_alt_text)

        Column {
            LottieAnimation(
                composition = composition,
                progress = { state.progress },
                modifier = Modifier.semantics {
                    contentDescription = altText
                }
            )
            Spacer(Modifier.height(20.dp))
        }
    }
}