package uk.govuk.app

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import uk.govuk.app.config.ReleaseFlagsService
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.ui.GovUkApp

@RunWith(AndroidJUnit4::class)
class ReleaseFlagsServiceTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun feature_flag_restricts_access() {
//        GIVEN a user is in the GOV.UK app
//        WHEN the feature flag is set to restrict access
//        THEN the user cannot see or access that feature

        val mockReleaseFlagsService = mockk<ReleaseFlagsService>()
        every { mockReleaseFlagsService.isSearchEnabled() } returns false

        composeTestRule.setContent {
            GovUkTheme {
                GovUkApp()
            }
        }

        composeTestRule.onNodeWithText("Find government services and information").assertDoesNotExist()
    }

    @Test
    fun feature_flag_grants_access() {
//        GIVEN a user is in the GOV.UK app
//        WHEN the feature flag is set to grant access
//        THEN the user can see and access that feature

        val mockReleaseFlagsService = mockk<ReleaseFlagsService>()
        every { mockReleaseFlagsService.isSearchEnabled() } returns true

        composeTestRule.setContent {
            GovUkTheme {
                GovUkApp()
            }
        }

        composeTestRule.onNodeWithText("Find government services and information").assertExists()
    }
}
