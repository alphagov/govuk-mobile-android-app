package uk.govuk.app.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.govuk.app.onboarding.analytics.OnboardingAnalytics
import javax.inject.Inject

data class OnboardingUiState(
    val pages: List<OnboardingPage>
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val onboardingAnalytics: OnboardingAnalytics
): ViewModel() {

    companion object {
        private const val ONBOARDING_SCREEN_1_ALIAS = "ONBOARDING_A"
        private const val ONBOARDING_SCREEN_2_ALIAS = "ONBOARDING_B"
        private const val ONBOARDING_SCREEN_3_ALIAS = "ONBOARDING_C"

        private const val SCREEN_CLASS = "OnboardingScreen"
        private const val CONTINUE_ACTION = "continue"
        private const val SKIP_ACTION = "skip"
        private const val DONE_ACTION = "done"
    }

    // Todo - this will probably come from JSON file or remote config etc
    private val _uiState: MutableStateFlow<OnboardingUiState> = MutableStateFlow(
        OnboardingUiState(
            listOf(
                OnboardingPage(
                    analyticsAlias = ONBOARDING_SCREEN_1_ALIAS,
                    title = R.string.getThingsDoneScreenTitle,
                    body = R.string.getThingsDoneScreenBody,
                    image = R.drawable.image_1
                ),
                OnboardingPage(
                    analyticsAlias = ONBOARDING_SCREEN_2_ALIAS,
                    title = R.string.backToPreviousScreenTitle,
                    body = R.string.backToPreviousScreenBody,
                    image = R.drawable.image_2
                ),
                OnboardingPage(
                    analyticsAlias = ONBOARDING_SCREEN_3_ALIAS,
                    title = R.string.tailoredToYouScreenTitle,
                    body = R.string.tailoredToYouScreenBody,
                    image = R.drawable.image_3
                ),
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onPageView(pageIndex: Int) {
        val page = uiState.value.pages[pageIndex]
        onboardingAnalytics.onboardingScreenView(
            screenClass = SCREEN_CLASS,
            alias = page.analyticsAlias,
            title = context.getString(page.title)
        )
    }

    fun onContinue(pageIndex: Int, cta: String) {
        logButtonClick(
            pageIndex = pageIndex,
            cta = cta,
            action = CONTINUE_ACTION
        )
    }

    fun onSkip(pageIndex: Int, cta: String) {
        logButtonClick(
            pageIndex = pageIndex,
            cta = cta,
            action = SKIP_ACTION
        )
    }

    fun onDone(pageIndex: Int, cta: String) {
        logButtonClick(
            pageIndex = pageIndex,
            cta = cta,
            action = DONE_ACTION
        )
    }

    private fun logButtonClick(
        pageIndex: Int,
        cta: String,
        action: String
    ) {
        val page = uiState.value.pages[pageIndex]
        onboardingAnalytics.onboardingButtonClick(
            screenName = page.analyticsAlias,
            cta = cta,
            action = action
        )
    }

}