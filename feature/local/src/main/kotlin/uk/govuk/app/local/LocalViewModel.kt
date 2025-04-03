package uk.govuk.app.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.data.remote.model.Address
import uk.govuk.app.local.data.remote.model.ApiResponse
import uk.govuk.app.local.data.remote.model.LocalAuthority
import javax.inject.Inject

internal data class LocalUiState(
    var postcode: String = "",
    var slug: String = "",
    var message: String = "",
    var addresses: List<Address> = emptyList(),
    var localAuthority: LocalAuthority? = null
)

@HiltViewModel
internal class LocalViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val localRepo: LocalRepo,
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "LocalScreen"
        private const val SCREEN_NAME = "Local"
        private const val TITLE = "Local"
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

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onEditPageView() {
        analyticsClient.screenView(
            screenClass = "LocalEntryScreen",
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onSearchPostcode(postcode: String) {
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
                    when (val result = response.value) {
                        is ApiResponse.LocalAuthorityResponse -> {
                            _uiState.update { current ->
                                current.copy(
                                    localAuthority = result.localAuthority
                                )
                            }
                        }
                        is ApiResponse.AddressListResponse -> {
                            _uiState.update { current ->
                                current.copy(
                                    addresses = result.addresses
                                )
                            }
                       }
                        is ApiResponse.MessageResponse -> {
                            _uiState.update { current ->
                                current.copy(
                                    message = result.message
                                )
                            }
                        }
                    }
                }

                is Result.DeviceOffline -> {
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

                is Result.DeviceOffline -> {
                    println(response)
                }
                else -> println(response)
            }
        }
    }
}
