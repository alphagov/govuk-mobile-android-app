package uk.govuk.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.config.data.ConfigRepo
import uk.govuk.app.config.data.InvalidSignatureException
import uk.govuk.app.config.data.flags.FlagRepo
import uk.govuk.app.data.AppRepo
import uk.govuk.app.networking.domain.DeviceOfflineException
import uk.govuk.app.topics.TopicsFeature
import javax.inject.Inject

@HiltViewModel
internal class AppViewModel @Inject constructor(
    private val appRepo: AppRepo,
    private val configRepo: ConfigRepo,
    private val flagRepo: FlagRepo,
    private val topicsFeature: TopicsFeature,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    private val _uiState: MutableStateFlow<AppUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        fetchConfig()
    }

    fun onTryAgain() {
        _uiState.value = AppUiState.Loading
        fetchConfig()
    }

    private fun fetchConfig() {
        viewModelScope.launch {
            val configResult = configRepo.initConfig()
            configResult.onSuccess {
                _uiState.value = if (!flagRepo.isAppAvailable()) {
                    AppUiState.AppUnavailable
                } else if (flagRepo.isForcedUpdate(BuildConfig.VERSION_NAME)) {
                    AppUiState.ForcedUpdate
                } else {
                    val topicsInitSuccess = topicsFeature.init()

                    AppUiState.Default(
                        shouldDisplayRecommendUpdate = flagRepo.isRecommendUpdate(BuildConfig.VERSION_NAME),
                        shouldDisplayAnalyticsConsent = analyticsClient.isAnalyticsConsentRequired(),
                        shouldDisplayOnboarding = flagRepo.isOnboardingEnabled() && !appRepo.isOnboardingCompleted(),
                        shouldDisplayTopicSelection = flagRepo.isTopicsEnabled()
                                && !appRepo.isTopicSelectionCompleted()
                                && topicsInitSuccess,
                        isSearchEnabled = flagRepo.isSearchEnabled(),
                        isRecentActivityEnabled = flagRepo.isRecentActivityEnabled(),
                        isTopicsEnabled = flagRepo.isTopicsEnabled()
                    )
                }
            }
            configResult.onFailure { exception ->
                _uiState.value = when (exception) {
                    is InvalidSignatureException -> AppUiState.ForcedUpdate
                    is DeviceOfflineException -> AppUiState.DeviceOffline
                    else -> AppUiState.AppUnavailable
                }
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

    fun onWidgetClick(
        text: String,
        external: Boolean,
        section: String
    ) {
        analyticsClient.widgetClick(
            text,
            external,
            section
        )
    }

    fun onTabClick(text: String) {
        analyticsClient.tabClick(text)
    }
}
