package uk.govuk.app.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.data.model.Result.Success
import uk.govuk.app.local.data.LocalRepo
import javax.inject.Inject

internal data class LocalUiState(
    var postcode: String = "",
    var localAuthorityName: String = "",
    var localAuthorityUrl: String = "",
    var localCustodianCode: Int = 0,
    var binsUrl: String = "",
    var taxUrl: String = ""
)

@HiltViewModel
internal class LocalViewModel @Inject constructor(
    private val localRepo: LocalRepo
): ViewModel() {

    private val _uiState: MutableStateFlow<LocalUiState> = MutableStateFlow(LocalUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = LocalUiState()
        }
    }

    fun updatePostcode(postcode: String) {
        _uiState.value.postcode = postcode

        viewModelScope.launch {
            val result = localRepo.performLocationsApiCall(postcode)
            if (result is Success) {
                _uiState.value = LocalUiState(
                    postcode = postcode,
                    localCustodianCode = result.value.localCustodianCode,
                )
            }
        }
    }

    fun updateLocalAuthority() {
        viewModelScope.launch {
            val result =
                localRepo.performLocalAuthorityApiCall(_uiState.value.localCustodianCode.toString())
            if (result is Success) {
                _uiState.value = LocalUiState(
                    postcode = _uiState.value.postcode,
                    localCustodianCode = _uiState.value.localCustodianCode,
                    localAuthorityName = result.value.name,
                    localAuthorityUrl = result.value.homepageUrl,
                )
            }
        }
    }

    fun updateServices() {
        viewModelScope.launch {
            updateBinService()
            updateTaxService()
        }
    }

    private fun updateBinService() {
        viewModelScope.launch {
            val bins = localRepo.performLinkApiCall(
                _uiState.value.localCustodianCode.toString(), 524.toString(), 8.toString()
            )

            println(bins.toString())

            if (bins is Success) {
                _uiState.value = LocalUiState(
                    postcode = _uiState.value.postcode,
                    localCustodianCode = _uiState.value.localCustodianCode,
                    localAuthorityName = _uiState.value.localAuthorityName,
                    localAuthorityUrl = _uiState.value.localAuthorityUrl,
                    binsUrl = bins.value.url,
                    taxUrl = _uiState.value.taxUrl,
                )
            }
        }
    }

    private fun updateTaxService() {
        viewModelScope.launch {
            val tax = localRepo.performLinkApiCall(
                _uiState.value.localCustodianCode.toString(), 58.toString(), 8.toString()
            )

            println(tax.toString())

            if (tax is Success) {
                _uiState.value = LocalUiState(
                    postcode = _uiState.value.postcode,
                    localCustodianCode = _uiState.value.localCustodianCode,
                    localAuthorityName = _uiState.value.localAuthorityName,
                    localAuthorityUrl = _uiState.value.localAuthorityUrl,
                    binsUrl = _uiState.value.binsUrl,
                    taxUrl = tax.value.url,
                )
            }
        }
    }
}
