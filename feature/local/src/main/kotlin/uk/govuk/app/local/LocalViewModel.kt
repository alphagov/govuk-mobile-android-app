package uk.govuk.app.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import javax.inject.Inject

internal data class LocalUiState(
    var postcode: String,
    val localAuthorityName: String,
    val localAuthorityUrl: String,
)

@HiltViewModel
internal class LocalViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
): ViewModel() {

    companion object {
        private const val EDIT_BUTTON = "Edit"
        private const val EDIT_SCREEN_CLASS = "EditLocalScreen"
        private const val SCREEN_CLASS = "LocalScreen"
        private const val SCREEN_NAME = "Local"
        private const val TITLE = "Local"
    }

    private val _uiState: MutableStateFlow<LocalUiState> = MutableStateFlow(
        LocalUiState(
            postcode = "E1 8QS",
            localAuthorityName = "London Borough of Tower Hamlets",
            localAuthorityUrl = "https://www.towerhamlets.gov.uk",
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = LocalUiState(
                postcode = "E1 8QS",
                localAuthorityName = "London Borough of Tower Hamlets",
                localAuthorityUrl = "https://www.towerhamlets.gov.uk",
            )
        }
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onEditClick() {
        analyticsClient.buttonFunction(
            text = "",
            section = SCREEN_NAME,
            action = EDIT_BUTTON
        )
    }

    fun onEditPageView() {
        analyticsClient.screenView(
            screenClass = EDIT_SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onLocalAuthorityClicked(name: String, url: String) {
//        TODO:...
//        analyticsClient.localAuthorityClick(text = title, url = url)
    }
}
