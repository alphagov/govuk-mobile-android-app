package uk.govuk.app.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result.DeviceOffline
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.data.remote.model.Address
import uk.govuk.app.local.data.remote.model.LocalAuthority
import javax.inject.Inject

internal data class LocalUiState(
    var postcode: String = "",
    var slug: String? = "",
    var message: String? = "",
    var addresses: List<Address>? = emptyList(),
    var localAuthority: LocalAuthority? = null
)

@HiltViewModel
internal class LocalViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val localRepo: LocalRepo,
): ViewModel() {

    companion object {
        private const val EXPLAINER_SCREEN_CLASS = "LocalExplainerScreen"
        private const val EXPLAINER_SCREEN_NAME = "Local Explainer"
        private const val EXPLAINER_TITLE = "Local Explainer"

        private const val LOOKUP_SCREEN_CLASS = "LocalLookupScreen"
        private const val LOOKUP_SCREEN_NAME = "Local Lookup"
        private const val LOOKUP_TITLE = "Local Lookup"

        private const val SECTION = "Local"
    }

    private val _uiState: MutableStateFlow<LocalUiState> = MutableStateFlow(
        LocalUiState()
    )
    internal val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = LocalUiState()
        }
    }

    fun onExplainerPageView() {
        analyticsClient.screenView(
            screenClass = EXPLAINER_SCREEN_CLASS,
            screenName = EXPLAINER_SCREEN_NAME,
            title = EXPLAINER_TITLE
        )
    }

    fun onExplainerButtonClick(text: String) {
        analyticsClient.buttonClick(
            text = text,
            section = SECTION
        )
    }

    fun onLookupPageView() {
        analyticsClient.screenView(
            screenClass = LOOKUP_SCREEN_CLASS,
            screenName = LOOKUP_SCREEN_NAME,
            title = LOOKUP_TITLE
        )
    }

    fun onSearchPostcode(buttonText: String, postcode: String) {
        analyticsClient.buttonClick(
            text = buttonText,
            section = SECTION
        )

        clearPreviousResults()
        _uiState.value.postcode = postcode

        viewModelScope.launch {
            fetchPostcodeLookupResult(toApiPostcode(postcode))
        }
    }

    fun onSearchLocalAuthority(slug: String) {
        clearPreviousResults()
        _uiState.value.slug = slug

        viewModelScope.launch {
            fetchLocalAuthorityResult(slug)
        }
    }

    private fun clearPreviousResults() {
        _uiState.value.postcode = ""
        _uiState.value.slug = ""
        _uiState.value.message = ""
        _uiState.value.addresses = emptyList()
        _uiState.value.localAuthority = null
    }

    private fun toApiPostcode(str: String): String {
        return str.filter { !it.isWhitespace() }.uppercase()
    }

    private fun fetchPostcodeLookupResult(postcode: String) {
        viewModelScope.launch {
            when (val response = localRepo.performGetLocalPostcode(postcode)) {
                is Success -> {
                    _uiState.update { current ->
                        current.copy(
                            localAuthority = response.value.localAuthority,
                            addresses = response.value.addresses,
                            message = response.value.message
                        )
                    }

                    println("Success: $response")
                }

                is DeviceOffline -> {
                    println(response)
                }

                else -> println(response)
            }
        }
    }

    private fun fetchLocalAuthorityResult(slug: String) {
        viewModelScope.launch {
            when (val response = localRepo.performGetLocalAuthority(slug)) {
                is Success -> {
                    _uiState.update { current ->
                        current.copy(
                            localAuthority = response.value.localAuthority
                        )
                    }
                }

                is DeviceOffline -> {
                    println(response)
                }

                else -> println(response)
            }
        }
    }
}
