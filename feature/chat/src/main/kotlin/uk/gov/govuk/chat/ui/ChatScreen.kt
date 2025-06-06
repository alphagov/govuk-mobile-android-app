package uk.gov.govuk.chat.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.ui.model.ConversationUi
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.FixedContainerDivider
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun ChatRoute(
    modifier: Modifier = Modifier,
) {
    val viewModel: ChatViewModel = hiltViewModel()
    val conversation by viewModel.conversation.collectAsState()

    conversation?.let {
        ChatScreen(
            conversation = it,
            modifier = modifier
        )
    }
}

@Composable
private fun ChatScreen(
    conversation: ConversationUi,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            MediumVerticalSpacer()

            conversation.answeredQuestions.forEach { question ->
                BodyBoldLabel(
                    text = question.message,
                    modifier = modifier
                )
                MediumVerticalSpacer()
                BodyRegularLabel(
                    text = question.answer.message,
                    modifier = modifier
                )
                MediumVerticalSpacer()
            }
        }

        Column(modifier.fillMaxWidth()) {
            FixedContainerDivider()
            MediumVerticalSpacer()
            TextField(
                value = "",
                onValueChange = { },
                label = {
                    BodyRegularLabel(
                        text = "Ask your question here..."
                    )
                },
                placeholder = { },
                singleLine = false,
                modifier = Modifier.fillMaxWidth()
                    .padding(GovUkTheme.spacing.medium)
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
            conversation = ConversationUi(
                id = "1234567890",
                answeredQuestions = emptyList(),
                createdAt = "2022-01-01T00:00:00Z"
            )
        )
    }
}
