package uk.gov.govuk.chat.ui

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.ChatUiState
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.data.remote.model.Answer
import uk.gov.govuk.chat.data.remote.model.AnsweredQuestion
import uk.gov.govuk.chat.data.remote.model.Conversation
import uk.gov.govuk.chat.data.remote.model.Source
import uk.gov.govuk.chat.domain.StringCleaner
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.FixedContainerDivider
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import kotlin.math.abs

@Composable
internal fun ChatRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ChatViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    ChatScreen(
        uiState = uiState,
        onSubmit = { question ->
            viewModel.onSubmit(question)
        },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun ChatScreen(
    uiState: ChatUiState?,
    onSubmit: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var question by remember { mutableStateOf("") }
    var scrollPosition by remember { mutableIntStateOf(0) }
    var isError by remember { mutableStateOf(false) }
    var characterCount by remember { mutableIntStateOf(0) }
    var buttonDisabled by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState?.chatEntries?.size) {
        val answerCount = uiState?.chatEntries?.size ?: 0
        if (answerCount > 0) {
            delay(150)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

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
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            if (uiState?.chatEntries != null && uiState.chatEntries.isNotEmpty()) {
                MediumVerticalSpacer()

                uiState.chatEntries.entries.forEachIndexed { index, chatEntry ->
                    val isLastQuestion = index == uiState.chatEntries.size - 1
                    Column(
                        modifier = if (isLastQuestion) {
                            Modifier.onGloballyPositioned { layoutCoordinates ->
                                val positionInScrollableContent = layoutCoordinates.positionInParent().y
                                scrollPosition = positionInScrollableContent.toInt()
                            }
                        } else {
                            Modifier
                        }
                    ) {
                        BodyBoldLabel(
                            text = chatEntry.value.question
                        )
                        MediumVerticalSpacer()

                        MarkdownText(
                            markdown = chatEntry.value.answer,
                            style = GovUkTheme.typography.bodyRegular,
                            enableSoftBreakAddsNewLine = false
                        )

                        if (chatEntry.value.sources.isNotEmpty()) {
                            BodyBoldLabel(
                                text = stringResource(id = R.string.sources_header)
                            )
                            MediumVerticalSpacer()

                            chatEntry.value.sources.forEach { source ->
                                MarkdownText(
                                    markdown = source,
                                    style = GovUkTheme.typography.bodyRegular,
                                    enableSoftBreakAddsNewLine = false
                                )
                                MediumVerticalSpacer()
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier.fillMaxWidth()
        ) {
            FixedContainerDivider()
            MediumVerticalSpacer()

            if (uiState != null) {
                if (uiState.loading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = GovUkTheme.spacing.medium),
                        color = GovUkTheme.colourScheme.surfaces.primary,
                        trackColor = GovUkTheme.colourScheme.surfaces.textFieldBackground
                    )
                }
            }

            MediumVerticalSpacer()

            TextField(
                value = question,
                onValueChange = {
                    buttonDisabled = false
                    isError = false
                    question = it
                    characterCount = it.length
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.input_label)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .focusable(true)
                    .padding(horizontal = GovUkTheme.spacing.medium),
                singleLine = false,
                isError = isError,
                supportingText = {
                    if (isError) {
                        focusRequester.requestFocus()
                        val errorMessage = stringResource(id = R.string.pii_error_message)
                        BodyBoldLabel(
                            color = GovUkTheme.colourScheme.textAndIcons.textFieldError,
                            text = errorMessage
                        )
                    }
                },
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
                onClick = {
                    val currentQuestion = question
                    isError = StringCleaner.includesPII(currentQuestion)
                    if (!isError) {
                        onSubmit(currentQuestion)
                        question = ""
                    }
                },
                enabled = question.isNotBlank() && !buttonDisabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GovUkTheme.spacing.medium),
            )

            // TODO: add these to config somewhere!
            val characterLimit = 300
            val characterNotify = 250

            if (characterCount in characterNotify..<characterLimit) {
                buttonDisabled = false
                Text(
                    text = "${characterLimit - characterCount} characters remaining",
                    color = GovUkTheme.colourScheme.textAndIcons.primary,
                    style = GovUkTheme.typography.bodyBold,
                    modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
                )
            } else if (characterCount == characterLimit) {
                buttonDisabled = false
                Text(
                    text = "0 characters remaining",
                    color = GovUkTheme.colourScheme.textAndIcons.primary,
                    style = GovUkTheme.typography.bodyBold,
                    modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
                )
            } else if (characterCount > characterLimit) {
                buttonDisabled = true
                Text(
                    text = "${abs(characterLimit - characterCount)} characters too many",
                    color = GovUkTheme.colourScheme.textAndIcons.textFieldError,
                    style = GovUkTheme.typography.bodyBold,
                    modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    GovUkTheme {
        ChatScreen(
            uiState = ChatUiState(
                conversationId = "210d1a18-7b77-4418-9938-ad1b5700b9fd",
                loading = false
            ),
            onSubmit = { _ -> },
            onBack = {},
        )
    }
}

private fun conversation() = Conversation(
    id = "210d1a18-7b77-4418-9938-ad1b5700b9fd",
    answeredQuestions = listOf(
        AnsweredQuestion(
            id = "ca615e61-d1ad-4787-b0c9-ae5aed19d12b",
            answer = Answer(
                id = "9642d765-cb44-4c87-b959-0109aa5bcead",
                createdAt = "2025-06-10T15:19:58+01:00",
                message = """
                    To apply for Universal Credit, you can follow these steps:
                    1.  **Apply Online**: You can apply for Universal Credit online by
                        creating an account. You must complete your claim within 28 days of
                        creating your account, or you will have to start again. Your claim
                        starts on the date you submit it in your account. If you live with
                        your partner, both of you will need to create accounts and link them
                        together when you claim. [Apply now][1].

                    2.  **Required Information**: To apply online, you’ll need your bank,
                        building society, or credit union account details, an email address,
                        and access to a phone. You’ll also need to prove your identity using
                        documents such as a driving licence, passport, debit or credit card,
                        payslip, or P60. Additionally, you will need to provide information
                        about your housing, earnings, National Insurance number, other
                        benefits, any disability or health condition affecting your work,
                        childcare costs, and savings or investments.

                    3.  **Help with Your Claim**: If you need assistance, you can get free
                        support from trained advisers through the Help to Claim service
                        provided by Citizens Advice. This service is confidential, and they
                        will not share your personal information without your consent. You
                        can also call the Universal Credit helpline for assistance.

                    4.  **Alternative Application Methods**: If you cannot claim online, you
                        can claim by phone through the Universal Credit helpline.

                    For more detailed information on how to apply for Universal Credit,
                    visit the [GOV.UK page on Universal Credit][2].



                    [1]: https://www.universal-credit.service.gov.uk/postcode-checker
                    [2]: https://www.integration.publishing.service.gov.uk/universal-credit/how-to-claim
                """.trimIndent(),
                sources = listOf(
                    Source(
                        url = "https://www.universal-credit.service.gov.uk/postcode-checker",
                        title = "Universal Credit"
                    ),
                )
            ),
            conversationId = "210d1a18-7b77-4418-9938-ad1b5700b9fd",
            createdAt = "2025-06-10T15:18:47+01:00",
            message = "How can I apply for universal credit?"
        )
    ),
    createdAt = "2025-06-10T15:18:47+01:00",
    pendingQuestion = null
)
