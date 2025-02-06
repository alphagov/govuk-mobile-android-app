package uk.govuk.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.config.data.ConfigRepo
import uk.govuk.app.config.data.flags.FlagRepo
import uk.govuk.app.data.AppRepo
import uk.govuk.app.data.model.Result.*
import uk.govuk.app.notifications.NotificationsClient
import uk.govuk.app.topics.TopicsFeature
import javax.inject.Inject

@HiltViewModel
internal class AppViewModel @Inject constructor(
    private val appRepo: AppRepo,
    private val configRepo: ConfigRepo,
    private val flagRepo: FlagRepo,
    private val topicsFeature: TopicsFeature,
    private val analyticsClient: AnalyticsClient,
    private val notificationsClient: NotificationsClient
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
            val result = configRepo.initConfig()
            _uiState.value = when (result) {
                is Success -> {
                    if (!flagRepo.isAppAvailable()) {
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
                            shouldDisplayNotificationsPermission = flagRepo.isNotificationsEnabled()
                                    && !notificationsClient.permissionDetermined(),
                            isSearchEnabled = flagRepo.isSearchEnabled(),
                            isRecentActivityEnabled = flagRepo.isRecentActivityEnabled(),
                            isTopicsEnabled = flagRepo.isTopicsEnabled()
                        )
                    }
                }
                is InvalidSignature -> AppUiState.ForcedUpdate
                is DeviceOffline -> AppUiState.DeviceOffline
                else -> AppUiState.AppUnavailable
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
