package uk.govuk.app.onboarding

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import javax.inject.Inject

internal data class OnboardingUiState(
    val pages: List<OnboardingPage>
)

@HiltViewModel
internal class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyticsClient: AnalyticsClient
): ViewModel() {

    companion object {
        private const val ONBOARDING_SCREEN_1_NAME = "Onboarding_A"
        private const val ONBOARDING_SCREEN_2_NAME = "Onboarding_B"
        private const val ONBOARDING_SCREEN_3_NAME = "Onboarding_C"

        private const val SCREEN_CLASS = "OnboardingScreen"
    }

    // Todo - this will probably come from JSON file or remote config etc
    private val _uiState: MutableStateFlow<OnboardingUiState> = MutableStateFlow(
        OnboardingUiState(
            listOf(
                OnboardingPage(
                    screenName = ONBOARDING_SCREEN_1_NAME,
                    title = R.string.getThingsDoneScreenTitle,
                    body = R.string.getThingsDoneScreenBody,
                    image = R.drawable.image_1,
                ),
                OnboardingPage(
                    screenName = ONBOARDING_SCREEN_2_NAME,
                    title = R.string.backToPreviousScreenTitle,
                    body = R.string.backToPreviousScreenBody,
                    image = R.drawable.image_2
                ),
                OnboardingPage(
                    screenName = ONBOARDING_SCREEN_3_NAME,
                    title = R.string.tailoredToYouScreenTitle,
                    body = R.string.tailoredToYouScreenBody,
                    image = R.drawable.image_3,
                    animation = R.raw.onboarding_personalisation
                ),
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onPageView(pageIndex: Int) {
        val page = uiState.value.pages[pageIndex]
        viewModelScope.launch {
            analyticsClient.screenView(
                screenClass = SCREEN_CLASS,
                screenName = page.screenName,
                title = context.getString(page.title)
            )
        }
    }

    fun onButtonClick(text: String) {
        viewModelScope.launch {
            analyticsClient.buttonClick(text)
        }
    }

    fun onPagerIndicatorClick() {
        viewModelScope.launch {
            analyticsClient.pageIndicatorClick()
        }
    }
}