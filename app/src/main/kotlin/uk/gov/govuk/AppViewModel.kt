package uk.gov.govuk

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.chat.ChatFeature
import uk.gov.govuk.config.data.ConfigRepo
import uk.gov.govuk.config.data.flags.FlagRepo
import uk.gov.govuk.data.AppRepo
import uk.gov.govuk.data.auth.AuthRepo
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.InvalidSignature
import uk.gov.govuk.data.model.Result.Success
import uk.gov.govuk.login.data.LoginRepo
import uk.gov.govuk.navigation.AppNavigation
import uk.gov.govuk.search.SearchFeature
import uk.gov.govuk.topics.TopicsFeature
import uk.gov.govuk.widgets.model.HomeWidget
import uk.gov.govuk.visited.Visited
import uk.govuk.app.local.LocalFeature
import javax.inject.Inject

@HiltViewModel
internal class AppViewModel @Inject constructor(
    private val timeoutManager: TimeoutManager,
    private val appRepo: AppRepo,
    private val loginRepo: LoginRepo,
    private val configRepo: ConfigRepo,
    private val flagRepo: FlagRepo,
    private val authRepo: AuthRepo,
    private val topicsFeature: TopicsFeature,
    private val localFeature: LocalFeature,
    private val searchFeature: SearchFeature,
    private val visitedFeature: Visited,
    private val chatFeature: ChatFeature,
    private val analyticsClient: AnalyticsClient,
    val appNavigation: AppNavigation
) : ViewModel() {

    private val _uiState: MutableStateFlow<AppUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    private val _homeWidgets: MutableStateFlow<List<HomeWidget>?> = MutableStateFlow(null)
    internal val homeWidgets = _homeWidgets.asStateFlow()

    init {
        analyticsClient.isUserSessionActive = { authRepo.isUserSessionActive() }

        viewModelScope.launch {
            initWithConfig()
        }
    }

    private suspend fun initWithConfig() {
        val configResult = configRepo.initConfig()
        when (configResult) {
            is Success -> {
                if (!flagRepo.isAppAvailable()) {
                    _uiState.value = AppUiState.AppUnavailable
                } else if (flagRepo.isForcedUpdate(BuildConfig.VERSION_NAME)) {
                    _uiState.value = AppUiState.ForcedUpdate
                } else {
                    topicsFeature.init()

                    combine(
                        appRepo.suppressedHomeWidgets,
                        localFeature.hasLocalAuthority(),
                        chatFeature.hasOptedIn()
                    ) { suppressedWidgets, localAuthority, chatHasOptedIn ->
                        Triple(suppressedWidgets, localAuthority, chatHasOptedIn)
                    }.collect {
                        _uiState.value = AppUiState.Default(
                            shouldDisplayRecommendUpdate = flagRepo.isRecommendUpdate(BuildConfig.VERSION_NAME),
                            shouldShowExternalBrowser = flagRepo.isExternalBrowserEnabled(),
                            isChatEnabled = flagRepo.isChatEnabled() &&
                                    flagRepo.isChatTestActiveEnabled() &&
                                    it.third,
                            alertBanner = configRepo.config.alertBanner
                        )

                        updateHomeWidgets(it.first, it.second)
                    }
                }
            }
            is InvalidSignature -> _uiState.value = AppUiState.ForcedUpdate
            is DeviceOffline -> _uiState.value = AppUiState.DeviceOffline
            else -> _uiState.value = AppUiState.AppUnavailable
        }
    }

    fun onTryAgain() {
        _uiState.value = AppUiState.Loading
        viewModelScope.launch {
            initWithConfig()
        }
    }

    fun onUserInteraction(
        navController: NavController,
        interactionTime: Long = SystemClock.elapsedRealtime()
    ) {
        timeoutManager.onUserInteraction(interactionTime) {
            if (authRepo.isUserSessionActive()) {
                authRepo.endUserSession()
                viewModelScope.launch {
                    appNavigation.onSignOut(navController)
                }
            }
        }
    }

    fun onLogin(navController: NavController) {
        viewModelScope.launch {
            if (authRepo.isDifferentUser()) {
                authRepo.clear()
                appRepo.clear()
                loginRepo.clear()
                topicsFeature.clear()
                localFeature.clear()
                searchFeature.clear()
                visitedFeature.clear()
                chatFeature.clear()
                analyticsClient.clear()
            }
            appNavigation.onNext(navController)
        }
    }

    suspend fun topicSelectionCompleted() {
        appRepo.topicSelectionCompleted()
    }

    private fun updateHomeWidgets(
        suppressedWidgets: Set<String>,
        hasLocalAuthority: Boolean
    ) {
        viewModelScope.launch {
            with(flagRepo) {
                val widgets = mutableListOf<HomeWidget>()
                if (isSearchEnabled()) {
                    widgets.add(HomeWidget.Search)
                }
                configRepo.config.alertBanner?.let { alertBanner ->
                    if (!suppressedWidgets.contains(alertBanner.id)) {
                        widgets.add(HomeWidget.Alert(alertBanner = alertBanner))
                    }
                }
                if (isLocalServicesEnabled() && !hasLocalAuthority) {
                    widgets.add(HomeWidget.Local)
                }
                if (isRecentActivityEnabled()) {
                    widgets.add(HomeWidget.RecentActivity)
                }
                if (isTopicsEnabled()) {
                    widgets.add(HomeWidget.Topics)
                }
                if (isLocalServicesEnabled() && hasLocalAuthority) {
                    widgets.add(HomeWidget.Local)
                }
                configRepo.config.userFeedbackBanner?.let { userFeedbackBanner ->
                    widgets.add(HomeWidget.UserFeedback(userFeedbackBanner = userFeedbackBanner))
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
        id: String,
        section: String
    ) {
        viewModelScope.launch {
            appRepo.suppressHomeWidget(id)
        }
        analyticsClient.suppressWidgetClick(
            id,
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
