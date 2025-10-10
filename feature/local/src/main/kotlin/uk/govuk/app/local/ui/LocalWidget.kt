package uk.govuk.app.local.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CentredCardWithIcon
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.NavigationCard
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalWidgetUiState.LocalAuthoritySelected
import uk.govuk.app.local.LocalWidgetUiState.NoLocalAuthority
import uk.govuk.app.local.LocalWidgetViewModel
import uk.govuk.app.local.R
import uk.govuk.app.local.domain.model.LocalAuthority

@Composable
fun LocalWidget(
    onLookupClick: (String) -> Unit,
    onLocalAuthorityClick: (String, String) -> Unit,
    onEditClick: (String) -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LocalWidgetViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    uiState?.let {
        when (it) {
            is LocalAuthoritySelected ->
                LocalAuthorityCard(
                    it.localAuthority,
                    onLocalAuthorityClick,
                    onEditClick,
                    launchBrowser,
                    modifier
                )
            is NoLocalAuthority -> NoLocalAuthorityCard(onLookupClick, modifier)
        }
    }
}

@Composable
private fun LocalAuthorityCard(
    localAuthority: LocalAuthority,
    onClick: (String, String) -> Unit,
    onEditClick: (String) -> Unit,
    launchBrowser: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Title3BoldLabel(
                text = stringResource(R.string.local_widget_title),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = GovUkTheme.spacing.large)
                    .semantics { heading() }
            )

            val editButtonText = stringResource(R.string.local_edit_button)
            val editButtonAltText = stringResource(R.string.local_edit_button_alt_text)

            TextButton(
                onClick = { onEditClick(editButtonAltText) }
            ) {
                BodyRegularLabel(
                    text = editButtonText,
                    color = GovUkTheme.colourScheme.textAndIcons.link,
                    modifier = Modifier.semantics {
                        contentDescription = editButtonAltText
                    }
                )
            }
        }

        val description = if (localAuthority.parent != null) {
            stringResource(R.string.local_child_authority_description, localAuthority.name)
        } else {
            null
        }

        localAuthority.parent?.let { parent ->
            BodyRegularLabel(stringResource(R.string.local_two_tier_title))
            SmallVerticalSpacer()
            NavigationCard(
                title = parent.name,
                description = stringResource(
                    R.string.local_parent_authority_description,
                    parent.name
                ),
                onClick = {
                    onClick(parent.name, parent.url)
                    launchBrowser(parent.url)
                }
            )
            LargeVerticalSpacer()
        }

        NavigationCard(
            title = localAuthority.name,
            description = description,
            onClick = {
                onClick(localAuthority.name, localAuthority.url)
                launchBrowser(localAuthority.url)
            }
        )
    }
}

@Composable
private fun NoLocalAuthorityCard(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = GovUkTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Title3BoldLabel(
                text = stringResource(R.string.local_widget_title),
                modifier = Modifier
                    .padding(end = GovUkTheme.spacing.large)
                    .semantics { heading() }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val description = stringResource(R.string.local_widget_description)

            CentredCardWithIcon(
                onClick = { onClick(description) },
                icon = R.drawable.outline_add_circle_outline_24,
                description = description
            )
        }
    }
}

@Preview (showBackground = true)
@Composable
private fun NoLocalAuthorityPreview() {
    GovUkTheme {
        NoLocalAuthorityCard(
            onClick = { }
        )
    }
}

@Preview (showBackground = true)
@Composable
private fun UnitaryLocalAuthorityPreview() {
    GovUkTheme {
        LocalAuthorityCard(
            localAuthority = LocalAuthority(
                name = "London Borough of Tower Hamlets",
                url = "",
                slug = ""
            ),
            onClick = { _, _ -> },
            onEditClick = { },
            launchBrowser = {}
        )
    }
}

@Preview (showBackground = true)
@Composable
private fun TwoTierLocalAuthorityPreview() {
    GovUkTheme {
        LocalAuthorityCard(
            localAuthority = LocalAuthority(
                name = "Derbyshire Dales District Council",
                url = "",
                slug = "",
                parent = LocalAuthority(
                    name = "Derbyshire County Council",
                    url = "",
                    slug = ""
                )
            ),
            onClick = { _, _ -> },
            onEditClick = { },
            launchBrowser = {}
        )
    }
}
