package uk.govuk.app.design.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ButtonsTest {
    private val buttonText = "Hello, World!"

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun canCreateAnEnabledPrimaryButton() {
        composeTestRule.setContent { PrimaryButton(buttonText, {}, modifier = Modifier) }
        val button = composeTestRule.onNodeWithText(buttonText)
        button.assertIsDisplayed()
        button.assertIsEnabled()
    }

    @Test
    fun canCreateADisabledPrimaryButton() {
        composeTestRule.setContent { PrimaryButton(buttonText, {}, modifier = Modifier, enabled = false) }
        val button = composeTestRule.onNodeWithText(buttonText)
        button.assertIsDisplayed()
        button.assertIsNotEnabled()
    }

    @Test
    fun canCreateAnEnabledSecondaryButton() {
        composeTestRule.setContent { SecondaryButton(buttonText, {}, modifier = Modifier) }
        val button = composeTestRule.onNodeWithText(buttonText)
        button.assertIsDisplayed()
        button.assertIsEnabled()
    }

    @Test
    fun canCreateADisabledSecondaryButton() {
        composeTestRule.setContent { SecondaryButton(buttonText, {}, modifier = Modifier, enabled = false) }
        val button = composeTestRule.onNodeWithText(buttonText)
        button.assertIsDisplayed()
        button.assertIsNotEnabled()
    }
}
