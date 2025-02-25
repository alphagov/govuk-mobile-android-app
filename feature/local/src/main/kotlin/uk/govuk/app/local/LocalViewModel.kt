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
}
