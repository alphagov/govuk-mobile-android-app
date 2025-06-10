package uk.gov.govuk.chat.ui

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.domain.StringCleaner
import uk.gov.govuk.chat.ui.model.ConversationUi
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.FixedContainerDivider
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun ChatRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ChatViewModel = hiltViewModel()
    val conversation by viewModel.conversation.collectAsState()

    ChatScreen(
        conversation = conversation,
        onSubmit = { question ->
            viewModel.onSubmit(question)
        },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun ChatScreen(
    conversation: ConversationUi?,
    onSubmit: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var question by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Column(modifier) {
        FullScreenHeader(
            modifier = Modifier
                .semantics {
                    isTraversalGroup = true
                    traversalIndex = -1f
                },
            onBack = { onBack() }
        )

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            if (conversation != null) {
                MediumVerticalSpacer()

                conversation.answeredQuestions.forEach { question ->
                    BodyBoldLabel(
                        text = question.message,
                        modifier = modifier
                    )
                    MediumVerticalSpacer()

                    val answer = StringCleaner.removeInParagraphNewlines(question.answer.message)
                    MarkdownText(
                        markdown = answer,
                        modifier = modifier
                    )
                    MediumVerticalSpacer()
                }
            }
        }

        Column(
            modifier.fillMaxWidth()
        ) {
            FixedContainerDivider()
            MediumVerticalSpacer()

            // TODO: display any error messages...
            TextField(
                value = question,
                onValueChange = {
                    // TODO: check for PII
                    question = it
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.input_label)
                    )
                },
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(focusRequester)
                    .focusable(true)
                    .padding(horizontal = GovUkTheme.spacing.medium),
                singleLine = false,
                colors = TextFieldDefaults.colors(
                    cursorColor = GovUkTheme.colourScheme.strokes.textFieldCursor,
                    errorContainerColor = GovUkTheme.colourScheme.surfaces.textFieldBackground,
                    errorCursorColor = GovUkTheme.colourScheme.strokes.textFieldCursor,
                    errorIndicatorColor = GovUkTheme.colourScheme.strokes.textFieldError,
                    errorLabelColor = GovUkTheme.colourScheme.textAndIcons.primary,
                    errorPrefixColor = GovUkTheme.colourScheme.textAndIcons.textFieldError,
                    errorPlaceholderColor = GovUkTheme.colourScheme.textAndIcons.textFieldError,
                    errorSuffixColor = GovUkTheme.colourScheme.textAndIcons.textFieldError,
                    errorSupportingTextColor = GovUkTheme.colourScheme.textAndIcons.textFieldError,
                    errorTextColor = GovUkTheme.colourScheme.textAndIcons.primary,
                    focusedContainerColor = GovUkTheme.colourScheme.surfaces.textFieldBackground,
                    focusedIndicatorColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    focusedLabelColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    focusedPlaceholderColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    focusedTextColor = GovUkTheme.colourScheme.textAndIcons.primary,
                    selectionColors = TextSelectionColors(
                        handleColor = GovUkTheme.colourScheme.surfaces.textFieldHighlighted,
                        backgroundColor = GovUkTheme.colourScheme.surfaces.textFieldHighlighted
                    ),
                    unfocusedContainerColor = GovUkTheme.colourScheme.surfaces.textFieldBackground,
                    unfocusedIndicatorColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    unfocusedLabelColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    unfocusedPlaceholderColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    unfocusedTextColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                )
            )

            MediumVerticalSpacer()

            PrimaryButton(
                text = stringResource(id = R.string.button_text),
                onClick = { onSubmit(question) },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = GovUkTheme.spacing.medium),
            )

            ExtraLargeVerticalSpacer()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    GovUkTheme {
        ChatScreen(
            conversation = null,
            onSubmit = { _ -> },
            onBack = {},
        )
    }
}
