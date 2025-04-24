package uk.gov.govuk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.InvalidSignature
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.login.LoginFeature
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.ui.model.HomeWidget
import uk.govuk.app.local.LocalFeature
import javax.inject.Inject

@HiltViewModel
internal class AppViewModel @Inject constructor(
    private val appRepo: AppRepo,
    private val configRepo: ConfigRepo,
    private val flagRepo: FlagRepo,
    loginFeature: LoginFeature,
    private val topicsFeature: TopicsFeature,
    localFeature: LocalFeature,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    private val _uiState: MutableStateFlow<AppUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    private val _homeWidgets: MutableStateFlow<List<HomeWidget>?> = MutableStateFlow(null)
    internal val homeWidgets = _homeWidgets.asStateFlow()

    private val configRetryTrigger = MutableSharedFlow<Unit>(replay = 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val config = configRetryTrigger
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow {
                emit(configRepo.initConfig())
            }
        }

    init {
        viewModelScope.launch {
            combine(
                config,
                appRepo.suppressedHomeWidgets,
                localFeature.hasLocalAuthority()
            ) { configResult, suppressedHomeWidgets, hasLocalAuthority  ->
                Triple(configResult, suppressedHomeWidgets, hasLocalAuthority)
            }.collect {
                _uiState.value = when (it.first) {
                    is Success -> {
                        if (!flagRepo.isAppAvailable()) {
                            AppUiState.AppUnavailable
                        } else if (flagRepo.isForcedUpdate(BuildConfig.VERSION_NAME)) {
                            AppUiState.ForcedUpdate
                        } else {
                            updateHomeWidgets(it.second, it.third)

                            val topicsInitSuccess = topicsFeature.init()

                            AppUiState.Default(
                                shouldDisplayRecommendUpdate = flagRepo.isRecommendUpdate(BuildConfig.VERSION_NAME),
                                shouldDisplayAnalyticsConsent = analyticsClient.isAnalyticsConsentRequired(),
                                shouldDisplayOnboarding = flagRepo.isOnboardingEnabled() && !appRepo.isOnboardingCompleted(),
                                shouldDisplayBiometricOnboarding = loginFeature.isAuthenticationEnabled(),
                                shouldDisplayTopicSelection = flagRepo.isTopicsEnabled()
                                        && !appRepo.isTopicSelectionCompleted()
                                        && topicsInitSuccess,
                                shouldDisplayNotificationsOnboarding = flagRepo.isNotificationsEnabled()
                            )
                        }
                    }
                    is InvalidSignature -> AppUiState.ForcedUpdate
                    is DeviceOffline -> AppUiState.DeviceOffline
                    else -> AppUiState.AppUnavailable
                }
            }
        }
    }

    fun onTryAgain() {
        _uiState.value = AppUiState.Loading
        viewModelScope.launch {
            configRetryTrigger.emit(Unit)
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

    private fun updateHomeWidgets(
        suppressedWidgets: Set<String>,
        hasLocalAuthority: Boolean
    ) {
        viewModelScope.launch {
            with(flagRepo) {
                val widgets = mutableListOf<HomeWidget>()
                if (isNotificationsEnabled()
                    && !suppressedWidgets.contains(HomeWidget.NOTIFICATIONS.serializedName)
                ) {
                    widgets.add(HomeWidget.NOTIFICATIONS)
                }
                if (isSearchEnabled()) {
                    widgets.add(HomeWidget.SEARCH)
                }
                if (isLocalServicesEnabled() && !hasLocalAuthority) {
                    widgets.add(HomeWidget.LOCAL)
                }
                if (isRecentActivityEnabled()) {
                    widgets.add(HomeWidget.RECENT_ACTIVITY)
                }
                if (isTopicsEnabled()) {
                    widgets.add(HomeWidget.TOPICS)
                }
                if (isLocalServicesEnabled() && hasLocalAuthority) {
                    widgets.add(HomeWidget.LOCAL)
                }
                _homeWidgets.value = widgets
            }
        }
    }

    fun onWidgetClick(
        text: String,
        url: String? = null,
        external: Boolean,
        section: String
    ) {
        analyticsClient.widgetClick(
            text,
            url,
            external,
            section
        )
    }

    fun onSuppressWidgetClick(
        text: String,
        section: String,
        widget: HomeWidget
    ) {
        viewModelScope.launch {
            appRepo.suppressHomeWidget(widget)
        }
        analyticsClient.suppressWidgetClick(
            text,
            section
        )
    }

    fun onTabClick(text: String) {
        analyticsClient.tabClick(text)
    }

    fun onDeepLinkReceived(hasDeepLink: Boolean, url: String) {
        analyticsClient.deepLinkEvent(
            hasDeepLink,
            url
        )
    }
}
