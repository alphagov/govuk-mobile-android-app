package uk.govuk.app.design.ui.component

import android.content.Context
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.window.core.layout.WindowHeightSizeClass
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun OnboardingPage(
    @StringRes title: Int,
    @StringRes body: Int,
    modifier: Modifier = Modifier,
    @DrawableRes image: Int? = null,
    @RawRes animation: Int? = null,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(top = GovUkTheme.spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT) {
            animation?.let { animation ->
                AnimatedImage(animation)
            } ?: image?.let { image ->
                Image(
                    painter = painterResource(id = image),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = GovUkTheme.spacing.extraLarge)
                )
            }
        }

        LargeTitleBoldLabel(
            text = stringResource(title),
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
                .padding(horizontal = GovUkTheme.spacing.extraLarge)
                .semantics { heading() },
            textAlign = TextAlign.Center
        )
        MediumVerticalSpacer()
        BodyRegularLabel(
            text = stringResource(body),
            modifier = Modifier
                .focusable()
                .padding(horizontal = GovUkTheme.spacing.extraLarge),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AnimatedImage(@RawRes image: Int) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(image)
    )

    var state = animateLottieCompositionAsState(composition = composition)

    val animationsDisabled = areAnimationsDisabled(LocalContext.current)
    if (animationsDisabled) {
        state = animateLottieCompositionAsState(composition = composition, isPlaying = false)
    }

    LottieAnimation(
        composition = composition,
        progress = { if (animationsDisabled) 1.0F else state.progress }
    )
}

private fun areAnimationsDisabled(context: Context): Boolean {
    val animatorDurationScale = Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    )
    return animatorDurationScale == 0f
}
