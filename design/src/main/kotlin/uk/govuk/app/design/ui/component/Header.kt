package uk.govuk.app.design.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.R
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
fun SearchHeader(
    onBack: () -> Unit,
    placeholder: String,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester = FocusRequester()
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .clickable { onBack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.content_desc_back),
                    modifier = Modifier.align(Alignment.Center),
                    tint = GovUkTheme.colourScheme.textAndIcons.link
                )
            }
            SearchField(
                placeholder = placeholder,
                onSearch = onSearch,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
            )
        }
        ListDivider()
    }
}

@Composable
fun BaseHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .padding(GovUkTheme.spacing.medium)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Title2BoldLabel(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BaseHeaderPreview() {
    GovUkTheme {
        BaseHeader("Heading text")
    }
}

@Composable
fun SettingsHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Title3BoldLabel(
        text = text,
        modifier = Modifier.padding(
            top = GovUkTheme.spacing.medium,
            start = GovUkTheme.spacing.extraLarge,
            end = GovUkTheme.spacing.medium
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsHeaderPreview() {
    GovUkTheme {
        SettingsHeader("Settings text")
    }
}
