package uk.govuk.app.local.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SecondaryButton
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.local.R

@Composable
internal fun LocalAuthoritySelectRoute(
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onSelect: () -> Unit,
    postcode: String,
    modifier: Modifier = Modifier,
) {
    LocalAuthoritySelectScreen(
        onBack = onBack,
        onCancel = onCancel,
        onSelect = onSelect,
        postcode = postcode,
        modifier = modifier
    )
}

@Composable
private fun LocalAuthoritySelectScreen(
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onSelect: () -> Unit,
    postcode: String,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier.fillMaxWidth(),

        topBar = {
            FullScreenHeader(
                modifier = Modifier
                    .semantics {
                        isTraversalGroup = true
                        traversalIndex = -1f
                    },
                onBack = { onBack() },
                actionText = stringResource(R.string.local_cancel_button),
                onAction = onCancel
            )
        },
        bottomBar = {
            BottomNavBar(
                onSelect = onSelect,
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
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .fillMaxWidth()
        ) {
            SmallVerticalSpacer()
            Title1BoldLabel(
                text = stringResource(R.string.local_select_council_title)
            )
            SmallVerticalSpacer()

            BodyRegularLabel(
                text = stringResource(R.string.local_select_council_description, postcode)
            )

            MediumVerticalSpacer()

            // TODO: remove hardcoded value
            val isError = false
            if (isError) {
                BodyBoldLabel(
                    text = stringResource(R.string.local_select_council_error),
                    color = GovUkTheme.colourScheme.textAndIcons.textFieldError
                )
                MediumVerticalSpacer()
            }

            RadioButtonGroup(
                listOf(
                    "Bournemouth, Christchurch and Poole Council",
                    "Dorset Council"
                )
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
        val buttonText = stringResource(R.string.local_select_council_confirm)
        FixedPrimaryButton(
            text = buttonText,
            onClick = { /* TODO */ }
        )

        SecondaryButton(
            text = stringResource(R.string.local_select_by_address),
            onClick = onSelect
        )

        MediumVerticalSpacer()
    }
}

@Preview
@Composable
private fun LocalAuthoritySelectScreenPreview() {
    GovUkTheme {
        LocalAuthoritySelectRoute(
            onBack = {},
            onCancel = {},
            onSelect = {},
            postcode = "E18QS",
//            addresses = listOf(
//                Address(
//                    address = "BH22 8UB",
//                    slug = "dorset",
//                    name = "Dorset County Council"
//                ),
//                Address(
//                    address = "BH22 8UB",
//                    slug = "bournemouth-christchurch-poole",
//                    name = "Bournemouth, Christchurch, and Poole"
//                )
//            )
        )
    }
}
