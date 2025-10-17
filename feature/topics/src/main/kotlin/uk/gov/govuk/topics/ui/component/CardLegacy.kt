package uk.gov.govuk.topics.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.GovUkCardLegacy
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.SubheadlineRegularLabel
import uk.gov.govuk.design.ui.component.Title3BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.R

@Composable
fun TopicSelectionCardLegacy(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    GovUkCardLegacy(
        modifier = modifier,
        isSelected = isSelected,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.icon
            )
            SmallVerticalSpacer()
            Title3BoldLabel(
                text = title,
                textAlign = TextAlign.Center
            )
            SubheadlineRegularLabel(
                text = description,
                textAlign = TextAlign.Center
            )
            MediumVerticalSpacer()
            Spacer(Modifier.weight(1f))
            if (isSelected) {
                SelectedButtonLegacy()
            } else {
                SelectButtonLegacy()
            }
        }
    }
}

@Composable
private fun SelectButtonLegacy(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.link
        )
        BodyRegularLabel(
            text = stringResource(R.string.select_button),
            modifier = Modifier.padding(top = 2.dp),
            color = GovUkTheme.colourScheme.textAndIcons.link
        )
    }
}

@Composable
private fun SelectedButtonLegacy(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Check,
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.buttonSuccess
        )
        Spacer(Modifier.width(2.dp))
        BodyRegularLabel(
            text = stringResource(R.string.selected_button),
            modifier = Modifier.padding(top = 2.dp),
            color = GovUkTheme.colourScheme.textAndIcons.buttonSuccess
        )
    }
}

@Preview
@Composable
private fun TopicSelectionCardUnselectedPreview() {
    GovUkTheme {
        TopicSelectionCardLegacy(
            icon = R.drawable.ic_topic_benefits,
            title = "Benefits",
            description = "Claiming benefits, managing your benefits",
            isSelected = false,
            onClick = { },
            modifier = Modifier.height(200.dp)
        )
    }
}

@Preview
@Composable
private fun TopicSelectionCardSelectedPreview() {
    GovUkTheme {
        TopicSelectionCardLegacy(
            icon = R.drawable.ic_topic_benefits,
            title = "Benefits",
            description = "Claiming benefits, managing your benefits",
            isSelected = true,
            onClick = { },
            modifier = Modifier.height(200.dp)
        )
    }
}
