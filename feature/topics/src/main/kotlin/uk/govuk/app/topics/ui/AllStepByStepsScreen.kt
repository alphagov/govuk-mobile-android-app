package uk.govuk.app.topics.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.ExternalLinkListItem
import uk.govuk.app.design.ui.component.LargeVerticalSpacer
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.topics.AllStepByStepsViewModel
import uk.govuk.app.topics.R
import uk.govuk.app.topics.ui.model.TopicUi.TopicContent

@Composable
internal fun AllStepByStepRoute(
    onBack: () -> Unit,
    onClick: (url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: AllStepByStepsViewModel = hiltViewModel()
    val stepBySteps by viewModel.stepBySteps.collectAsState()

    AllStepByStepsScreen(
        stepBySteps = stepBySteps,
        onPageView = { title -> viewModel.onPageView(title) },
        onBack = onBack,
        onExternalLink = { section, text, url ->
            viewModel.onStepByStepClick(
                section = section,
                text = text,
                url = url
            )
            onClick(url)
        },
        modifier = modifier
    )
}

@Composable
private fun AllStepByStepsScreen(
    stepBySteps: List<TopicContent>?,
    onPageView: (String) -> Unit,
    onBack: () -> Unit,
    onExternalLink: (section: String, text: String, url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxSize()) {
        val title = stringResource(R.string.stepByStepGuidesTitle)

        LaunchedEffect(Unit) {
            onPageView(title)
        }

        ChildPageHeader(
            text = title,
            onBack = onBack
        )

        if (stepBySteps != null) {
            LazyColumn(Modifier.padding(horizontal = GovUkTheme.spacing.medium)) {
                item {
                    MediumVerticalSpacer()
                }

                stepBySteps(
                    stepBySteps = stepBySteps,
                    onClick = { text, url ->
                        onExternalLink(title, text, url)
                    }
                )
            }
        }
    }
}

private fun LazyListScope.stepBySteps(
    stepBySteps: List<TopicContent>,
    onClick: (text:String, url:String) -> Unit
) {
    itemsIndexed(stepBySteps) { index, content ->
        ExternalLinkListItem(
            title = content.title,
            onClick = { onClick(content.title, content.url) },
            isFirst = index == 0,
            isLast = index == stepBySteps.lastIndex
        )
    }

    item {
        LargeVerticalSpacer()
    }
}