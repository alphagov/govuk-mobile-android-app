package uk.gov.govuk.topics.ui.component

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.GovUkCard
import uk.gov.govuk.design.ui.component.MediumHorizontalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.R

@Composable
fun TopicSelectionCard(
    @DrawableRes icon: Int,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GovUkCard(
        modifier,
        isSelected = isSelected,
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_selected),
                        contentDescription = null
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = GovUkTheme.colourScheme.textAndIcons.iconSurroundPrimary,
                            shape = CircleShape
                        )
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        tint = GovUkTheme.colourScheme.textAndIcons.iconPrimary
                    )
                }
            }

            MediumHorizontalSpacer()

            val textColour = if (isSelected) {
                GovUkTheme.colourScheme.textAndIcons.listSelected
            } else {
                GovUkTheme.colourScheme.textAndIcons.listUnselected
            }
            BodyBoldLabel(
                text = title,
                color = textColour
            )
        }
    }
}

@Preview
@Composable
private fun TopicSelectionCardUnselectedPreview() {
    GovUkTheme {
        TopicSelectionCard(
            icon = R.drawable.ic_topic_benefits,
            title = "Benefits",
            isSelected = false,
            onClick = { }
        )
    }
}

@Preview
@Composable
private fun TopicSelectionCardSelectedPreview() {
    GovUkTheme {
        TopicSelectionCard(
            icon = R.drawable.ic_topic_benefits,
            title = "Benefits",
            isSelected = true,
            onClick = { }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TopicSelectionCardUnselectedDarkPreview() {
    GovUkTheme {
        TopicSelectionCard(
            icon = R.drawable.ic_topic_benefits,
            title = "Benefits",
            isSelected = false,
            onClick = { }
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TopicSelectionCardSelectedDarkPreview() {
    GovUkTheme {
        TopicSelectionCard(
            icon = R.drawable.ic_topic_benefits,
            title = "Benefits",
            isSelected = true,
            onClick = { }
        )
    }
}
