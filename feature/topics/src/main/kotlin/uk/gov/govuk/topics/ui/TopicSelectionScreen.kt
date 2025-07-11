package uk.gov.govuk.topics.ui

import android.content.Context
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedDoubleButtonGroup
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.R
import uk.gov.govuk.topics.TopicSelectionUiState
import uk.gov.govuk.topics.TopicSelectionViewModel
import uk.gov.govuk.topics.ui.component.TopicSelectionCard
import uk.gov.govuk.topics.ui.component.TopicsGrid

@Composable
internal fun TopicSelectionRoute(
    onDone: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: TopicSelectionViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    TopicSelectionScreen(
        uiState = uiState,
        onPageView = { title -> viewModel.onPageView(title) },
        onClick = { ref, title ->
            viewModel.onClick(ref, title)
        },
        onDone = { text ->
            viewModel.onDone(text)
            onDone()
        },
        onSkip = { text ->
            viewModel.onSkip(text)
            onSkip()
        },
        modifier = modifier
    )
}

@Composable
private fun TopicSelectionScreen(
    uiState: TopicSelectionUiState?,
    onPageView: (String) -> Unit,
    onClick: (ref: String, title: String) -> Unit,
    onDone: (String) -> Unit,
    onSkip: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val title = stringResource(R.string.topicSelectionTitle)

    LaunchedEffect(Unit) {
        onPageView(title)
    }

    Column(modifier) {
        LargeTitleBoldLabel(
            text = title,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = GovUkTheme.spacing.medium)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .semantics { heading() }
        )

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            BodyRegularLabel(
                text = stringResource(R.string.topicSelectionDescription),
            )
            MediumVerticalSpacer()

            TopicsGrid(
                topics = uiState?.topics ?: emptyList(),
            ) { modifier, topic ->
                val view = LocalView.current

                val selectedAltText = stringResource(R.string.selected_alt_text)
                val removedAltText = stringResource(R.string.removed_alt_text)

                TopicSelectionCard(
                    icon = topic.icon,
                    title = topic.title,
                    description = topic.description,
                    isSelected = topic.isSelected,
                    onClick = {
                        val accessibilityManager =
                            view.context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

                        if (accessibilityManager.isEnabled) {
                            accessibilityManager.interrupt()
                        }

                        if (topic.isSelected) {
                            view.announceForAccessibility(removedAltText)
                        } else {
                            view.announceForAccessibility(selectedAltText)
                        }
                        onClick(topic.ref, topic.title)
                    },
                    modifier = modifier
                )
            }

            MediumVerticalSpacer()
        }

        uiState?.let {
            val doneButtonText = stringResource(R.string.doneButton)
            val skipButtonText = stringResource(R.string.skipButton)
            val isDoneEnabled = it.isDoneEnabled

            FixedDoubleButtonGroup(
                primaryText = doneButtonText,
                onPrimary = { onDone(doneButtonText) },
                secondaryText = skipButtonText,
                onSecondary = { onSkip(skipButtonText) },
                primaryEnabled = isDoneEnabled
            )
        }
    }
}