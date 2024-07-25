package uk.govuk.app.design.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ButtonsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun buttonClickShouldUpdateTheButtonText() {
        composeTestRule.setContent { TestButton {} }
        composeTestRule.onNodeWithText("Not Clicked").performClick()
        composeTestRule.onNodeWithText("Clicked").assertIsDisplayed()
    }

    @Test
    fun buttonClickShouldNotRemainTheSame() {
        composeTestRule.setContent { TestButton {} }
        composeTestRule.onNodeWithText("Not Clicked").performClick()
        composeTestRule.onNodeWithText("Not Clicked").assertIsNotDisplayed()
    }
}
