package uk.gov.govuk.chat.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.FixedContainerDivider
import uk.gov.govuk.design.ui.component.LargeHorizontalSpacer
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun OnboardingPage(
    title: String,
    image: Painter,
    modifier: Modifier = Modifier,
    headerContent: @Composable () -> Unit,
    screenContent: @Composable ColumnScope.() -> Unit,
    buttonContent: @Composable () -> Unit
) {
    Column(
        modifier.fillMaxSize()
            .background(
                GovUkTheme.colourScheme.surfaces.background
            )
    ) {
        headerContent()

        Column(Modifier.weight(weight = 1f).fillMaxHeight()) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(
                        horizontal = GovUkTheme.spacing.medium,
                        vertical = GovUkTheme.spacing.large
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val isLogoVisible = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

                if (isLogoVisible) {
                    Image(
                        painter = image,
                        contentDescription = null,
                        modifier = Modifier.height(IntrinsicSize.Min)
                            .padding(all = GovUkTheme.spacing.medium)
                    )

                    LargeHorizontalSpacer()
                }

                LargeTitleBoldLabel(
                    text = title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .semantics { heading() }
                        .padding(horizontal = GovUkTheme.spacing.medium)
                )

                screenContent()
            }
        }

        FixedContainerDivider()

        MediumVerticalSpacer()

        buttonContent()

        ExtraLargeVerticalSpacer()
    }
}
