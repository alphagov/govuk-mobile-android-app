package uk.govuk.app.local

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.gov.govuk.analytics.AnalyticsClient
import uk.gov.govuk.data.model.Result.Success
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.data.remote.model.Address
import uk.govuk.app.local.data.remote.model.LocalAuthorityResult
import uk.govuk.app.local.data.remote.model.RemoteLocalAuthority
import javax.inject.Inject

internal sealed class LocalUiState {
    class LocalAuthority(
        val localAuthority: RemoteLocalAuthority
    ): LocalUiState() // Todo - should probably be mapping data layer objects to domain layer objects

    class Addresses(
        val addresses: List<Address>
    ): LocalUiState() // Todo - should probably be mapping data layer objects to domain layer objects

    class Error(@StringRes val message: Int): LocalUiState()
}

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

    private val _uiState: MutableStateFlow<LocalUiState?> = MutableStateFlow(null)
    internal val uiState = _uiState.asStateFlow()

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

    fun onErrorStatus(message: String) {
        analyticsClient.screenView(
            screenClass = LOOKUP_SCREEN_CLASS,
            screenName = LOOKUP_SCREEN_NAME,
            title = message
        )
    }

    fun onSearchPostcode(buttonText: String, postcode: String) {
        analyticsClient.buttonClick(
            text = buttonText,
            section = SECTION
        )

        viewModelScope.launch {
            fetchPostcodeLookupResult(toApiPostcode(postcode))
        }
    }

    fun onSearchLocalAuthority(slug: String) {
        viewModelScope.launch {
            fetchLocalAuthorityResult(slug)
        }
    }

    private fun toApiPostcode(str: String): String {
        return str.filter { !it.isWhitespace() }.uppercase()
    }

    private fun fetchPostcodeLookupResult(postcode: String) {
        if (postcode.isEmpty()) {
            _uiState.value = LocalUiState.Error(R.string.local_no_postcode_message)
        } else {
            viewModelScope.launch {
                when (val response = localRepo.performGetLocalPostcode(postcode)) {
                    is Success -> {
                        emitUiState(response.value)
                    }
                    else -> println(response)
                }
            }
        }
    }

    private fun fetchLocalAuthorityResult(slug: String) {
        viewModelScope.launch {
            when (val response = localRepo.performGetLocalAuthority(slug)) {
                is Success -> {
                    emitUiState(response.value)
                }
                else -> println(response)
            }
        }
    }

    private fun emitUiState(result: LocalAuthorityResult) {
        _uiState.value = when (result) {
            is LocalAuthorityResult.LocalAuthority -> LocalUiState.LocalAuthority(result.localAuthority)
            is LocalAuthorityResult.Addresses -> LocalUiState.Addresses(result.addresses)
            is LocalAuthorityResult.InvalidPostcode -> LocalUiState.Error(R.string.local_invalid_postcode_message)
            is LocalAuthorityResult.PostcodeNotFound -> LocalUiState.Error(R.string.local_not_found_postcode_message)
            is LocalAuthorityResult.PostcodeEmptyOrNull -> LocalUiState.Error(R.string.local_no_postcode_message)
        }
    }
}
