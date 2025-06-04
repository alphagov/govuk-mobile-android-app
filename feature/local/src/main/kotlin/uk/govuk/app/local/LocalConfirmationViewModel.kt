package uk.govuk.app.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.local.data.LocalRepo
import uk.govuk.app.local.domain.model.LocalAuthority
import javax.inject.Inject

@HiltViewModel
internal class LocalConfirmationViewModel @Inject constructor(
    private val localRepo: LocalRepo
): ViewModel() {

    private val _localAuthority: MutableStateFlow<LocalAuthority> = MutableStateFlow(localRepo.cachedLocalAuthority)
    internal val localAuthority = _localAuthority.asStateFlow()

    fun onDone() {
        viewModelScope.launch {
            localRepo.selectLocalAuthority()
        }
    }

}