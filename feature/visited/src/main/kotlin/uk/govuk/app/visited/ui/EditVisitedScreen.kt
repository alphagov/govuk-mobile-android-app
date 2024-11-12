package uk.govuk.app.visited.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.Title2BoldLabel
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.visited.R

@Composable
internal fun EditVisitedRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    EditVisitedScreen(
        onBack = onBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditVisitedScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val title = stringResource(R.string.visited_items_title)
    val doneText = stringResource(R.string.visited_items_done_button)
    val selectText = stringResource(R.string.visited_items_select_all_button)
    val removeText = stringResource(R.string.visited_items_remove_button)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = GovUkTheme.colourScheme.surfaces.background,
                    titleContentColor = GovUkTheme.colourScheme.textAndIcons.primary,
                ),
                title = {
                    Title2BoldLabel(
                        title,
                    )
                },
                actions = {
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.wrapContentSize()
                    ) {
                        BodyRegularLabel(
                            text = doneText,
                            color = GovUkTheme.colourScheme.textAndIcons.link,
                            textAlign = TextAlign.End
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = GovUkTheme.colourScheme.surfaces.background,
                contentColor = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = modifier.fillMaxWidth()
                    .height(81.dp)
                    .border(width = 1.dp, color = GovUkTheme.colourScheme.strokes.listDivider)
            ) {
                Row(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = GovUkTheme.spacing.large),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { /* TODO */ }
                    ) {
                        BodyRegularLabel(
                            text = selectText,
                            color = GovUkTheme.colourScheme.textAndIcons.link,
                            textAlign = TextAlign.Start
                        )
                    }
                    TextButton(
                        onClick =  { /* TODO */ },
                    ) {
                        BodyRegularLabel(
                            text = removeText,
                            color = GovUkTheme.colourScheme.textAndIcons.buttonRemove,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column (
            modifier = modifier.padding(innerPadding)
                .fillMaxWidth()
                .height(64.dp)
                .background(GovUkTheme.colourScheme.surfaces.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO: Add real content
            BodyRegularLabel(
                text = "Coming soon!",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditVisitedScreenPreview() {
    GovUkTheme {
        EditVisitedScreen(
            onBack = {},
            modifier = Modifier
        )
    }
}
