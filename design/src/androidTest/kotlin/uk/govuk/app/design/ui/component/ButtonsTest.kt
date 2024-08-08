package uk.govuk.app.design.ui.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.requestFocus
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.govuk.app.design.ui.theme.GovUkTheme

@RunWith(AndroidJUnit4::class)
class ButtonsTest {
    private val buttonText = "Hello, World!"

    @get:Rule
    val composeTestRule = createComposeRule()

//    [x] Test only the base button
//    [x] Test primary and secondary
//    [x] Test enabled and disabled
//    [x] Test externalLink
//    [ ] Test shape
//    [x] Test focused state
//    [ ] Test hovered state
//    [ ] Test pressed state

    @Test
    fun enabledButton() {
        composeTestRule.setContent {
            GovUkTheme {
                BaseButton(
                    text = buttonText,
                    onClick = {},
                    modifier = Modifier
                )
            }
        }

        composeTestRule.onNodeWithText(buttonText)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun secondaryButton() {
        composeTestRule.setContent {
            GovUkTheme {
                BaseButton(
                    text = buttonText,
                    onClick = {},
                    modifier = Modifier,
                    primary = false
                )
            }
        }

        composeTestRule.onNodeWithText(buttonText)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun disabledButton() {
        composeTestRule.setContent {
            GovUkTheme {
                BaseButton(
                    text = buttonText,
                    onClick = {},
                    modifier = Modifier,
                    enabled = false
                )
            }
        }

        composeTestRule.onNodeWithText(buttonText)
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun externalLinkButton() {
        composeTestRule.setContent {
            GovUkTheme {
                BaseButton(
                    text = buttonText,
                    onClick = {},
                    modifier = Modifier,
                    externalLink = true
                )
            }
        }

        composeTestRule.onNodeWithTag("openInNewTabIcon", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun buttonCanBeFocused() {
        composeTestRule.setContent {
            GovUkTheme {
                BaseButton(
                    text = buttonText,
                    onClick = {},
                    modifier = Modifier
                )
            }
        }

        composeTestRule.onNodeWithText(buttonText)
            .requestFocus()
            .assertIsFocused()
    }
}
