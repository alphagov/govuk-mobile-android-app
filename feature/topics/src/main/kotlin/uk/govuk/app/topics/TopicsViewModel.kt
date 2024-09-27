package uk.govuk.app.topics

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TopicsViewModel @Inject constructor(
    private val topicsRepo: TopicsRepo
): ViewModel() {

    init {
        viewModelScope.launch {
            val topics = topicsRepo.getTopics()
            Log.d("Topics", "$topics")
        }
    }

}