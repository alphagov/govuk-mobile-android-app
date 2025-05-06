package uk.govuk.app.local

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    data class LocalAuthority(
        val localAuthority: RemoteLocalAuthority
    ): LocalUiState() // Todo - should probably be mapping data layer objects to domain layer objects

    data class Addresses(
        val addresses: List<Address>
    ): LocalUiState() // Todo - should probably be mapping data layer objects to domain layer objects

    data class Error(@StringRes val message: Int): LocalUiState()
}

@HiltViewModel
internal class LocalViewModel @Inject constructor(
    private val analyticsClient: AnalyticsClient,
    private val localRepo: LocalRepo,
    @ApplicationContext private val context: Context
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

    fun onLookupPageView(title: String = LOOKUP_TITLE) {
        analyticsClient.screenView(
            screenClass = LOOKUP_SCREEN_CLASS,
            screenName = LOOKUP_SCREEN_NAME,
            title = title
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

    fun onPostcodeChange() {
         _uiState.value = null
    }

    private fun toApiPostcode(str: String): String {
        return str.filter { !it.isWhitespace() }.uppercase()
    }

    private fun fetchPostcodeLookupResult(postcode: String) {
        if (postcode.isBlank()) {
            _uiState.value = createAndLogError(R.string.local_no_postcode_message)
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

    private suspend fun emitUiState(result: LocalAuthorityResult) {
        _uiState.value = when (result) {
            is LocalAuthorityResult.LocalAuthority -> LocalUiState.LocalAuthority(result.localAuthority)
            is LocalAuthorityResult.Addresses -> {
                localRepo.cacheAddresses(result.addresses)
                LocalUiState.Addresses(result.addresses)
            }
            is LocalAuthorityResult.InvalidPostcode -> createAndLogError(R.string.local_invalid_postcode_message)
            is LocalAuthorityResult.PostcodeNotFound -> createAndLogError(R.string.local_not_found_postcode_message)
            is LocalAuthorityResult.PostcodeEmptyOrNull -> createAndLogError(R.string.local_no_postcode_message)
        }
    }

    private fun createAndLogError(@StringRes errorMessage: Int): LocalUiState.Error {
        onLookupPageView(context.getString(errorMessage))
        return LocalUiState.Error(errorMessage)
    }
}
