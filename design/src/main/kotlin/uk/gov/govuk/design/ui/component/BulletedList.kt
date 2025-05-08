package uk.gov.govuk.design.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
fun BulletedList(
    items: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        repeat(items.size) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .semantics(mergeDescendants = true) {}
            ) {
                BodyBoldLabel(
                    text = "•",
                    modifier = Modifier.alignByBaseline(),
                    color = GovUkTheme.colourScheme.textAndIcons.primary,
                )
                BodyRegularLabel(
                    text = items[index],
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .alignByBaseline(),
                    color = GovUkTheme.colourScheme.textAndIcons.primary
                )
            }
            if (index < items.size.minus(1)) {
                SmallVerticalSpacer()
            }
        }
    }
}

@Preview
@Composable
private fun BulletedListPreview() {
    val items = listOf(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        "Lorem ipsum dolor sit amet",
        "Lorem ipsum dolor sit amet"
    )
    BulletedList(items = items)
}
