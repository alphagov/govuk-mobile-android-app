package uk.govuk.app.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.govuk.app.local.data.LocalRepo
import javax.inject.Inject

@HiltViewModel
internal class LocalConfirmationViewModel @Inject constructor(
    private val localRepo: LocalRepo
): ViewModel() {

    internal val localAuthority = localRepo.cachedLocalAuthority

    fun onDone() {
        viewModelScope.launch {
            localRepo.selectLocalAuthority()
        }
    }

}