package uk.govuk.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.config.data.ConfigRepo
import uk.govuk.app.config.data.InvalidSignatureException
import uk.govuk.app.config.data.flags.FlagRepo
import uk.govuk.app.data.AppRepo
import javax.inject.Inject

internal data class AppUiState(
    val shouldDisplayAppUnavailable: Boolean,
    val shouldDisplayForcedUpdate: Boolean,
    val shouldDisplayRecommendUpdate: Boolean,
    val shouldDisplayAnalyticsConsent: Boolean,
    val shouldDisplayOnboarding: Boolean,
    val shouldDisplayTopicSelection: Boolean,
    val isSearchEnabled: Boolean,
    val isRecentActivityEnabled: Boolean,
    val isTopicsEnabled: Boolean
)

@HiltViewModel
internal class AppViewModel @Inject constructor(
    private val appRepo: AppRepo,
    configRepo: ConfigRepo,
    flagRepo: FlagRepo,
    private val analytics: Analytics
) : ViewModel() {

    private val _uiState: MutableStateFlow<AppUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val configResult = configRepo.initConfig()
            configResult.onSuccess {
                _uiState.value = AppUiState(
                    shouldDisplayAppUnavailable = !flagRepo.isAppAvailable(),
                    shouldDisplayForcedUpdate = flagRepo.isForcedUpdate(BuildConfig.VERSION_NAME),
                    shouldDisplayRecommendUpdate = flagRepo.isRecommendUpdate(BuildConfig.VERSION_NAME),
                    shouldDisplayAnalyticsConsent = analytics.isAnalyticsConsentRequired(),
                    shouldDisplayOnboarding = flagRepo.isOnboardingEnabled() && !appRepo.isOnboardingCompleted(),
                    shouldDisplayTopicSelection = flagRepo.isTopicsEnabled() && !appRepo.isTopicSelectionCompleted(),
                    isSearchEnabled = flagRepo.isSearchEnabled(),
                    isRecentActivityEnabled = flagRepo.isRecentActivityEnabled(),
                    isTopicsEnabled = flagRepo.isTopicsEnabled()
                )
            }
            configResult.onFailure { exception ->
                var isAppUnavailable = false
                var isForcedUpdate = false
                when (exception) {
                    is InvalidSignatureException -> isForcedUpdate = true
                    else -> isAppUnavailable = true
                }
                _uiState.value = AppUiState(
                    shouldDisplayAppUnavailable = isAppUnavailable,
                    shouldDisplayForcedUpdate = isForcedUpdate,
                    shouldDisplayRecommendUpdate = false,
                    shouldDisplayAnalyticsConsent = false,
                    shouldDisplayOnboarding = false,
                    shouldDisplayTopicSelection = false,
                    isSearchEnabled = false,
                    isRecentActivityEnabled = false,
                    isTopicsEnabled = false
                )
            }
        }
    }

    fun onboardingCompleted() {
        viewModelScope.launch {
            appRepo.onboardingCompleted()
        }
    }

    fun topicSelectionCompleted() {
        viewModelScope.launch {
            appRepo.topicSelectionCompleted()
        }
    }

    fun onWidgetClick(text: String) {
        analytics.widgetClick(text)
    }

    fun onTabClick(text: String) {
        analytics.tabClick(text)
    }
}
