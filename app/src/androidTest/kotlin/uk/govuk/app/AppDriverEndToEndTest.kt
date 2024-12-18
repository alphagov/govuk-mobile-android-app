package uk.govuk.app

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.Espresso
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import uk.govuk.app.analytics.navigation.ANALYTICS_GRAPH_ROUTE
import uk.govuk.app.analytics.navigation.analyticsGraph
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.home.navigation.HOME_GRAPH_ROUTE
import uk.govuk.app.home.navigation.homeGraph
import uk.govuk.app.onboarding.navigation.ONBOARDING_GRAPH_ROUTE
import uk.govuk.app.onboarding.navigation.onboardingGraph
import uk.govuk.app.search.navigation.SEARCH_GRAPH_ROUTE
import uk.govuk.app.search.navigation.searchGraph
import uk.govuk.app.search.ui.widget.SearchWidget
import uk.govuk.app.settings.BuildConfig.PRIVACY_POLICY_URL
import uk.govuk.app.settings.navigation.SETTINGS_GRAPH_ROUTE
import uk.govuk.app.settings.navigation.settingsGraph
import uk.govuk.app.topics.navigation.TOPICS_ALL_ROUTE
import uk.govuk.app.topics.navigation.navigateToTopic
import uk.govuk.app.topics.navigation.navigateToTopicsAll
import uk.govuk.app.topics.navigation.navigateToTopicsEdit
import uk.govuk.app.topics.navigation.topicsGraph
import uk.govuk.app.topics.ui.widget.TopicsWidget
import uk.govuk.app.visited.navigation.VISITED_GRAPH_ROUTE
import uk.govuk.app.visited.navigation.VISITED_ROUTE
import uk.govuk.app.visited.navigation.visitedGraph
import uk.govuk.app.visited.ui.widget.VisitedWidget

class AppDriverEndToEndTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {
        composeRule.activity.setContent {
            GovUkTheme {
                navController = rememberNavController()

                val selectedWidgets = mutableListOf<@Composable (Modifier) -> Unit>()
                selectedWidgets.add { modifier ->
                    SearchWidget(
                        onClick = { navController.navigate(SEARCH_GRAPH_ROUTE) },
                        modifier = modifier
                    )
                }
                selectedWidgets.add { modifier ->
                    VisitedWidget(
                        onClick = { navController.navigate(VISITED_GRAPH_ROUTE) },
                        modifier = modifier
                    )
                }
                selectedWidgets.add { modifier ->
                    TopicsWidget(
                        onTopicClick = { ref, _ -> navController.navigateToTopic(ref) },
                        onEditClick = { navController.navigateToTopicsEdit() },
                        onAllClick = { navController.navigateToTopicsAll() },
                        modifier = modifier
                    )
                }

                // We need to setup a NavHost with all the routes we need
                // to test - or it will not work! And we cannot use the one
                // already in the app. On the plus side this is the only
                // duplication of code from the app.
                NavHost(
                    navController = navController,
                    startDestination = HOME_GRAPH_ROUTE
                ) {
                    analyticsGraph(
                        privacyPolicyUrl = PRIVACY_POLICY_URL,
                        analyticsConsentCompleted = {
                            navController.popBackStack()
                        }
                    )
                    onboardingGraph(
                        onboardingCompleted = {
                            navController.popBackStack()
                        }
                    )
                    topicsGraph(
                        navController = navController,
                        topicSelectionCompleted = {
                            navController.popBackStack()
                        }
                    )
                    homeGraph(
                        widgets = selectedWidgets
                    )
                    settingsGraph(appVersion = BuildConfig.VERSION_NAME)
                    searchGraph(navController = navController)
                    visitedGraph(navController = navController)
                }
            }
        }
    }

    @Test
    fun route_navigation_driverTest() {
        val context = composeRule.activity.applicationContext

        composeRule.waitForIdle() // needed to init the navController

        // The home page is first shown as this is set as the startDestination in the NavHost.

        // Topics are not always shown
        // BottomNav is not shown

        analytics(context)
        onboarding(context)
        search(context)
        settings(context)
        topics(context)
        visited(context)
        home(context)
    }

    private fun home(context: Context) {
        val searchWidgetTitle = context.getString(uk.govuk.app.search.R.string.search_widget_title)
        val visitedWidgetTitle = context.getString(uk.govuk.app.visited.R.string.visited_items_title)
        val topicsWidgetTitle = context.getString(uk.govuk.app.topics.R.string.topicsWidgetTitle)
//        val homeTitle = context.getString(R.string.home)
//        val settingsTitle = context.getString(R.string.settings)

        composeRule.activity.runOnUiThread {
            navController.navigate(HOME_GRAPH_ROUTE)
        }

        // Widgets...
        composeRule.onNodeWithText(searchWidgetTitle).assertIsDisplayed()
        composeRule.onNodeWithText(visitedWidgetTitle).assertIsDisplayed()
        composeRule.onNodeWithText(topicsWidgetTitle).assertIsDisplayed()

        // Bottom nav...
//        composeRule.onNodeWithText(homeTitle).assertIsDisplayed()
//        composeRule.onNodeWithText(settingsTitle).assertIsDisplayed()
    }

    private fun analytics(context: Context) {
        val title = context.getString(uk.govuk.app.analytics.R.string.analytics_consent_title)

        composeRule.activity.runOnUiThread {
            navController.navigate(ANALYTICS_GRAPH_ROUTE)
        }

        composeRule.onNodeWithText(title).assertIsDisplayed()
    }

    private fun onboarding(context: Context) {
        val getThingsDoneScreenTitle =
            context.getString(uk.govuk.app.onboarding.R.string.getThingsDoneScreenTitle)
        val backToPreviousScreenTitle =
            context.getString(uk.govuk.app.onboarding.R.string.backToPreviousScreenTitle)
        val tailoredToYouScreenTitle =
            context.getString(uk.govuk.app.onboarding.R.string.tailoredToYouScreenTitle)
        val continueButton = context.getString(uk.govuk.app.onboarding.R.string.continueButton)
        val doneButton = context.getString(uk.govuk.app.onboarding.R.string.doneButton)

        composeRule.activity.runOnUiThread {
            navController.navigate(ONBOARDING_GRAPH_ROUTE)
        }

        composeRule.onNodeWithText(getThingsDoneScreenTitle).assertIsDisplayed()
        composeRule.onNodeWithText(continueButton).performClick()
//        composeRule.onNodeWithText(backToPreviousScreenTitle).assertIsDisplayed()
//        composeRule.onNodeWithText(continueButton).performClick()
//        composeRule.onNodeWithText(tailoredToYouScreenTitle).assertIsDisplayed()
//        composeRule.onNodeWithText(doneButton).performClick()
    }

    private fun search(context: Context) {
        val title = context.getString(uk.govuk.app.search.R.string.search_placeholder)
        val backButton = context.getString(uk.govuk.app.design.R.string.content_desc_back)

        composeRule.activity.runOnUiThread {
            navController.navigate(SEARCH_GRAPH_ROUTE)
        }

        composeRule.onNodeWithText(title).assertIsDisplayed()

        composeRule.onNodeWithText(title).performTextInput("micropig")
        composeRule.onNodeWithText("micropig").assertIsDisplayed()

//        TODO: Press enter to search and check for results
    }

    private fun settings(context: Context) {
        val screenTitle = context.getString(uk.govuk.app.settings.R.string.screen_title)
        val aboutTitle = context.getString(uk.govuk.app.settings.R.string.about_title)
        val privacyTitle = context.getString(uk.govuk.app.settings.R.string.privacy_title)
        val versionSetting = context.getString(uk.govuk.app.settings.R.string.version_setting)
        val shareSetting = context.getString(uk.govuk.app.settings.R.string.share_setting)
        val ossLicensesTitle = context.getString(uk.govuk.app.settings.R.string.oss_licenses_title)
        val accessibilityTitle =
            context.getString(uk.govuk.app.settings.R.string.accessibility_title)
        val termsAndConditionsTitle =
            context.getString(uk.govuk.app.settings.R.string.terms_and_conditions_title)
        val privacyPolicyTitle =
            context.getString(uk.govuk.app.settings.R.string.privacy_policy_title)
        val helpAndFeedbackTitle =
            context.getString(uk.govuk.app.settings.R.string.help_and_feedback_title)

        composeRule.activity.runOnUiThread {
            navController.navigate(SETTINGS_GRAPH_ROUTE)
        }

        composeRule
            .onNodeWithText(screenTitle)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(aboutTitle)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(privacyTitle)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(versionSetting)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(accessibilityTitle)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(termsAndConditionsTitle)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(privacyPolicyTitle)
            .assertIsDisplayed()
        composeRule
            .onNodeWithText(helpAndFeedbackTitle)
            .assertIsDisplayed()

        composeRule
            .onNodeWithText(shareSetting)
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        composeRule
            .onNodeWithText(ossLicensesTitle)
            .assertIsDisplayed()
            .performClick()

        Espresso.pressBack()
    }

    private fun topics(context: Context) {
        val allTopicsTitle = context.getString(uk.govuk.app.topics.R.string.allTopicsTitle)
//        val topicsWidgetTitle = context.getString(uk.govuk.app.topics.R.string.topicsWidgetTitle)
//        val stepByStepGuidesTitle = context.getString(uk.govuk.app.topics.R.string.stepByStepGuidesTitle)

        composeRule.activity.runOnUiThread {
            navController.navigate(TOPICS_ALL_ROUTE)
        }

        composeRule.onNodeWithText(allTopicsTitle).assertIsDisplayed()

//        composeRule.activity.runOnUiThread {
//            navController.navigate(TOPICS_GRAPH_ROUTE)
//        }
//        composeRule.onNodeWithText(title).assertIsDisplayed()


//        TOPIC_ROUTE

//        composeRule.activity.runOnUiThread {
//            navController.navigate(TOPICS_ALL_STEP_BY_STEPS_ROUTE)
//        }
//        composeRule.onNodeWithText(title).assertIsDisplayed()
    }

    private fun visited(context: Context) {
        val title = context.getString(uk.govuk.app.visited.R.string.visited_items_title)

        composeRule.activity.runOnUiThread {
            navController.navigate(VISITED_ROUTE)
        }

        composeRule.onNodeWithText(title).assertIsDisplayed()

//        composeRule.activity.runOnUiThread {
//            navController.navigate(VISITED_GRAPH_ROUTE)
//        }
//        val title = context.getString(uk.govuk.app.visited.R.string.visited_items_title)
//        composeRule.onNodeWithText(title).assertIsDisplayed()
    }
}
