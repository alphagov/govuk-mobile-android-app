package uk.govuk.app.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.theme.GovUkTheme

@Composable
internal fun SearchRoute(
    modifier: Modifier = Modifier
) {
    SearchScreen(modifier)
}

@Composable
private fun SearchScreen(
    modifier: Modifier = Modifier
) {
    Column(modifier) {
       SearchFieldHeader()
    }
}

@Composable
private fun SearchFieldHeader(
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable {
        mutableStateOf("")
    }

    Column(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .clickable { }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center),
                    tint = GovUkTheme.colourScheme.textAndIcons.link
                )
            }
            SearchField(
                value = searchQuery,
                onValueChange = { value -> searchQuery = value },
                Modifier.weight(1f)
            )
        }
        // Todo - add list divider to component library???
        HorizontalDivider(
            thickness = 1.dp,
            color = GovUkTheme.colourScheme.strokes.listDivider
        )
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        modifier = modifier,
        placeholder = {
            BodyRegularLabel("Search")
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clickable { onValueChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center),
                        tint = GovUkTheme.colourScheme.textAndIcons.trailingIcon
                    )
                }
            }
        },
        singleLine = true,
        textStyle = GovUkTheme.typography.bodyRegular,
        colors = TextFieldDefaults.colors()
            .copy(
                focusedTextColor = GovUkTheme.colourScheme.textAndIcons.primary,
                unfocusedTextColor = GovUkTheme.colourScheme.textAndIcons.primary,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
    )
}
