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

    // Todo - this will probably come from JSON file or remote config etc
    private val _uiState: MutableStateFlow<OnboardingUiState> = MutableStateFlow(
        OnboardingUiState(
            listOf(
                OnboardingPage(
                    analyticsAlias = "ONBOARDING_A",
                    title = R.string.getThingsDoneScreenTitle,
                    body = R.string.getThingsDoneScreenBody,
                    image = R.drawable.image_1
                ),
                OnboardingPage(
                    analyticsAlias = "ONBOARDING_B",
                    title = R.string.backToPreviousScreenTitle,
                    body = R.string.backToPreviousScreenBody,
                    image = R.drawable.image_2
                ),
                OnboardingPage(
                    analyticsAlias = "ONBOARDING_C",
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
            screenClass = "OnboardingScreen",
            alias = page.analyticsAlias,
            title = context.getString(page.title)
        )
    }

    fun onContinue(pageIndex: Int, cta: String) {
        logButtonClick(
            pageIndex = pageIndex,
            cta = cta,
            action = "continue"
        )
    }

    fun onSkip(pageIndex: Int, cta: String) {
        logButtonClick(
            pageIndex = pageIndex,
            cta = cta,
            action = "skip"
        )
    }

    fun onDone(pageIndex: Int, cta: String) {
        logButtonClick(
            pageIndex = pageIndex,
            cta = cta,
            action = "done"
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