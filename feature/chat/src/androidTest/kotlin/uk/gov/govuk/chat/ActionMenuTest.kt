package uk.gov.govuk.chat

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.govuk.chat.ui.AnalyticsEvents
import uk.gov.govuk.chat.ui.ChatScreen
import uk.gov.govuk.chat.ui.UiEvents
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.design.ui.theme.GovUkTheme
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ActionMenuTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun the_about_menu_item_is_always_shown() {
        setupChatScreen()

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText("About").assertIsDisplayed()
    }

    @Test
    fun the_privacy_notice_menu_item_is_always_shown() {
        setupChatScreen()

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText("Privacy notice").assertIsDisplayed()
    }

    @Test
    fun the_give_feedback_menu_item_is_always_shown() {
        setupChatScreen()

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText("Give feedback").assertIsDisplayed()
    }

    @Test
    fun when_there_is_an_active_conversation_the_clear_chat_menu_item_is_shown() {
        setupChatScreen(hasConversation = true)

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText("Clear chat").assertIsDisplayed()
    }

    @Test
    fun when_there_is_no_active_conversation_the_clear_chat_menu_item_is_not_shown() {
        setupChatScreen(hasConversation = false)

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText("Clear chat").assertDoesNotExist()
    }

    @Test
    fun when_talkBack_is_active_the_close_menu_item_is_shown() {
        setupChatScreen(isTalkBackActive = true)

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText("Close menu").assertIsDisplayed()
    }

    @Test
    fun when_talkBack_is_inactive_the_close_menu_item_is_not_shown() {
        setupChatScreen(isTalkBackActive = false)

        openMoreOptionsMenu()

        composeTestRule.onNodeWithText("Close menu").assertDoesNotExist()
    }

    private fun setupChatScreen(
        isTalkBackActive: Boolean = Random.nextBoolean(),
        hasConversation: Boolean = Random.nextBoolean()
    ) {
        composeTestRule.setContent {
            GovUkTheme {
                ChatScreen(
                    uiState = ChatUiState.Default(isLoading = false),
                    analyticsEvents = AnalyticsEvents(
                        onPageView = { _, _, _ -> },
                        onNavigationActionItemClicked = { _, _ -> },
                        onFunctionActionItemClicked = { _, _, _ -> },
                        onQuestionSubmit = { },
                        onMarkdownLinkClicked = { _, _ -> },
                        onSourcesExpanded = { }
                    ),
                    launchBrowser = { _ -> },
                    hasConversation = hasConversation,
                    chatUrls = ChatUrls("", "", "", ""),
                    uiEvents = UiEvents(
                        onQuestionUpdated = { _ -> },
                        onSubmit = { _ -> },
                        onClear = { }
                    ),
                    isTalkBackActive = isTalkBackActive
                )
            }
        }
    }

    private fun openMoreOptionsMenu() {
        composeTestRule.onNodeWithContentDescription("More options").performClick()
    }
}
