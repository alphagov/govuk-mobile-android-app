package uk.gov.govuk.onboarding.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.Title1RegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.onboarding.OnboardingViewModel
import uk.gov.govuk.onboarding.R

@Composable
internal fun OnboardingRoute(
    onboardingCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: OnboardingViewModel = hiltViewModel()

    OnboardingScreen(
        onPageView = { viewModel.onPageView() },
        onButtonClick = { text ->
            viewModel.onButtonClick(text)
            onboardingCompleted()
        },
        modifier
    )
}

@Composable
private fun OnboardingScreen(
    onPageView: () -> Unit,
    onButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier.fillMaxSize(),

        bottomBar = {
            BottomNavBar(
                onButtonClick = onButtonClick,
                modifier = Modifier
                    .semantics {
                        isTraversalGroup = true
                        traversalIndex = 1f
                    }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val isLogoVisible = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

            if (isLogoVisible) {
                Image(
                    painter = painterResource(id = R.drawable.welcome_image),
                    contentDescription = null,
                    modifier = Modifier.height(209.dp)
                )
                ExtraLargeVerticalSpacer()
            }

            LargeTitleBoldLabel(
                text = stringResource(id = R.string.onboardingTitle),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            Title1RegularLabel(
                text = stringResource(id = R.string.onboardingBody),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    onButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
        val buttonText = stringResource(id = R.string.onboardingButton)
        FixedPrimaryButton(
            text = buttonText,
            onClick = { onButtonClick(buttonText) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    GovUkTheme {
        OnboardingScreen(
            onPageView = { },
            onButtonClick = { _ -> },
        )
    }
}
