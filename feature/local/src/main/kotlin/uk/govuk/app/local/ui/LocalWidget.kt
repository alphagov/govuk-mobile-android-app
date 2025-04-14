package uk.govuk.app.local.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ExtraSmallVerticalSpacer
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SearchResultCard
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalWidgetUiState.LocalAuthoritySelected
import uk.govuk.app.local.LocalWidgetUiState.NoLocalAuthority
import uk.govuk.app.local.LocalWidgetViewModel
import uk.govuk.app.local.R

@Composable
fun LocalWidget(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LocalWidgetViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    uiState?.let {
        when (it) {
            is LocalAuthoritySelected -> LocalAuthorityCard(it.localAuthority, modifier)
            is NoLocalAuthority -> NoLocalAuthorityCard(onClick, modifier)
        }
    }
}

@Composable
private fun LocalAuthorityCard(
    localAuthority: LocalAuthorityUi,
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
                modifier = Modifier.semantics { heading() }
            )

            Spacer(Modifier.weight(1f))

            val editButtonText = stringResource(R.string.local_edit_button)
            val editButtonAltText = stringResource(R.string.local_edit_button_alt_text)

            TextButton(
                onClick = { }
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
            stringResource(R.string.local_unitary_authority_description, localAuthority.name)
        }

        localAuthority.parent?.let { parent ->
            BodyRegularLabel(stringResource(R.string.local_two_tier_title))
            SmallVerticalSpacer()
            SearchResultCard(
                title = parent.name,
                description = stringResource(
                    R.string.local_parent_authority_description,
                    parent.name
                ),
                url = parent.url,
                onClick = { _, _ -> } // TODO!!!
            )
            LargeVerticalSpacer()
        }

        SearchResultCard(
            title = localAuthority.name,
            description = description,
            url = localAuthority.url,
            onClick = { _, _ -> } // TODO!!!
        )
    }
}

@Composable
private fun NoLocalAuthorityCard(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = stringResource(R.string.local_widget_title)

    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GovUkTheme.colourScheme.surfaces.localBackground),
        border = BorderStroke(
            width = 1.dp,
            color = GovUkTheme.colourScheme.strokes.localBorder
        )
    ) {
        Column(
            Modifier
                .clickable { onClick(title) }
                .padding(
                    top = GovUkTheme.spacing.small,
                    bottom = GovUkTheme.spacing.medium
                )
        ) {
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .padding(start = GovUkTheme.spacing.medium),
                contentAlignment = Alignment.CenterStart
            ) {
                Title3BoldLabel(stringResource(R.string.local_section_title))
            }
            SmallVerticalSpacer()
            HorizontalDivider(color = GovUkTheme.colourScheme.strokes.localBorder)
            MediumVerticalSpacer()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(horizontal = GovUkTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(R.drawable.outline_pin_drop_24),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = GovUkTheme.colourScheme.textAndIcons.localIcon
                )
                SmallHorizontalSpacer()
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = GovUkTheme.spacing.medium)
                ) {
                    BodyBoldLabel(title)
                    ExtraSmallVerticalSpacer()
                    BodyRegularLabel(
                        stringResource(R.string.local_description),
                        color = GovUkTheme.colourScheme.textAndIcons.secondary
                    )
                }
                Icon(
                    painterResource(uk.gov.govuk.design.R.drawable.ic_chevron),
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.localIcon
                )
            }
        }
    }
}

@Preview
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
            localAuthority = LocalAuthorityUi(
                name = "London Borough of Tower Hamlets",
                url = "",
                slug = ""
            )
        )
    }
}

@Preview (showBackground = true)
@Composable
private fun TwoTierLocalAuthorityPreview() {
    GovUkTheme {
        LocalAuthorityCard(
            localAuthority = LocalAuthorityUi(
                name = "Derbyshire Dales District Council",
                url = "",
                slug = "",
                parent = LocalAuthorityUi(
                    name = "Derbyshire County Council",
                    url = "",
                    slug = ""
                )
            )
        )
    }
}
