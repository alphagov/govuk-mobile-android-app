package uk.govuk.app.local.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalViewModel
import uk.govuk.app.local.R
import uk.govuk.app.local.navigation.navigateToLocalEdit

@Composable
internal fun LocalRoute(
    navController: NavController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LocalViewModel = hiltViewModel()

    LocalScreen(
        onBack = onBack,
        onPageView = { viewModel.onExplainerPageView() },
        onContinueClick = { text ->
            viewModel.onExplainerButtonClick(text)
            navController.navigateToLocalEdit()
        },
        modifier = modifier
    )
}

@Composable
private fun LocalScreen(
    onBack: () -> Unit,
    onPageView: () -> Unit,
    onContinueClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier.fillMaxWidth(),

        topBar = {
            FullScreenHeader(
                onBack = { onBack() },
                modifier = modifier.padding(bottom = GovUkTheme.spacing.large)
            )
        },
        bottomBar = {
            BottomNavBar(
                onContinueClick = onContinueClick,
                modifier = modifier
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SmallVerticalSpacer()
            Image(
                painter = painterResource(id = R.drawable.local_icon),
                contentDescription = "",
                modifier = Modifier
            )
            MediumVerticalSpacer()
            LargeTitleBoldLabel(
                text = stringResource(R.string.local_title),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier,
                textAlign = TextAlign.Center
            )
            SmallVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(R.string.local_explainer),
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.padding(horizontal = GovUkTheme.spacing.extraLarge),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    onContinueClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
        HorizontalDivider(
            thickness = 1.dp,
            color = GovUkTheme.colourScheme.strokes.fixedContainer
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 64.dp)
                .padding(
                    start = GovUkTheme.spacing.medium,
                    end = GovUkTheme.spacing.medium,
                    bottom = GovUkTheme.spacing.large
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val buttonText = stringResource(R.string.local_continue_button)
            PrimaryButton(
                text = buttonText,
                onClick = { onContinueClick(buttonText) },
            )
        }
    }
}
