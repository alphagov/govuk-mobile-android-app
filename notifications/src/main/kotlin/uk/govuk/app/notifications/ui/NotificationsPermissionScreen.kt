package uk.govuk.app.notifications.ui

import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowHeightSizeClass
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ExtraLargeVerticalSpacer
import uk.govuk.app.design.ui.component.HorizontalButtonGroup
import uk.govuk.app.design.ui.component.LargeTitleBoldLabel
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.VerticalButtonGroup
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.notifications.NotificationsPermissionUiState
import uk.govuk.app.notifications.NotificationsPermissionViewModel
import uk.govuk.app.notifications.R
import uk.govuk.app.notifications.getNotificationsPermissionStatus

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun NotificationsPermissionRoute(
    notificationsPermissionCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: NotificationsPermissionViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val permissionStatus = getNotificationsPermissionStatus()
    LaunchedEffect(permissionStatus) {
        viewModel.updateUiState(permissionStatus)
    }

    uiState?.let { state ->
        when (state) {
            NotificationsPermissionUiState.OptIn -> {
                OptInScreen(
                    onContinue = {
                        viewModel.onContinueClick(it)
                        notificationsPermissionCompleted()
                    },
                    onSkip = {
                        viewModel.onSkipClick(it)
                        notificationsPermissionCompleted()
                    },
                    onPageView = { viewModel.onPageView() },
                    modifier = modifier
                )
            }

            NotificationsPermissionUiState.Finish -> {
                notificationsPermissionCompleted()
            }
        }
    }
}

@Composable
private fun OptInScreen(
    onContinue: (String) -> Unit,
    onSkip: (String) -> Unit,
    onPageView: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(modifier.fillMaxWidth()) {
        OptInPage()

        Spacer(modifier = Modifier.weight(1f))

        ListDivider()

        OptInFooter(
            onContinue = onContinue,
            onSkip = onSkip
        )
    }
}

@Composable
private fun OptInPage(
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(top = GovUkTheme.spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT) {
            AnimatedOptInImage()
            ExtraLargeVerticalSpacer()
        }

        LargeTitleBoldLabel(
            text = stringResource(R.string.permission_screen_title),
            modifier = Modifier
                .focusRequester(focusRequester)
                .focusable()
                .padding(horizontal = GovUkTheme.spacing.extraLarge)
                .semantics { heading() },
            textAlign = TextAlign.Center
        )
        MediumVerticalSpacer()
        BodyRegularLabel(
            stringResource(R.string.permission_screen_body),
            modifier = Modifier
                .focusable()
                .padding(horizontal = GovUkTheme.spacing.extraLarge),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AnimatedOptInImage() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.bell)
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

@Composable
private fun OptInFooter(
    onContinue: (String) -> Unit,
    onSkip: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = GovUkTheme.spacing.medium, bottom = GovUkTheme.spacing.small)
            .padding(horizontal = GovUkTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

        val continueButtonText = stringResource(R.string.continue_button)
        val skipButtonText = stringResource(R.string.skip_button)

        if (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
            HorizontalButtonGroup(
                primaryText = continueButtonText,
                onPrimary = { onContinue(continueButtonText) },
                secondaryText = skipButtonText,
                onSecondary = { onSkip(skipButtonText) }
            )
        } else {
            VerticalButtonGroup(
                primaryText = continueButtonText,
                onPrimary = { onContinue(continueButtonText) },
                secondaryText = skipButtonText,
                onSecondary = { onSkip(skipButtonText) }
            )
        }
    }
}

private fun areAnimationsDisabled(context: Context): Boolean {
    val animatorDurationScale = Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    )
    return animatorDurationScale == 0f
}

@Preview
@Composable
private fun OptInScreenPreview() {
    OptInScreen({}, {}, {})
}
