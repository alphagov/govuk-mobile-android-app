package uk.govuk.app.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.search.api.SearchRepo
import uk.govuk.app.search.api_result.Result
import uk.govuk.app.search.domain.ResultStatus
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val analytics: Analytics,
    private val repository: SearchRepo
): ViewModel() {
    private val _results = MutableLiveData<List<Result>>()
    private val _resultStatus = MutableLiveData<ResultStatus>()

    val searchResults: LiveData<List<Result>> = _results
    val resultStatus: LiveData<ResultStatus> = _resultStatus
    var searchTerm: String = ""

    private fun fetchSearchResults(searchTerm: String) {
        viewModelScope.launch {
            val (status, results) = repository.performSearch(searchTerm)
            _resultStatus.value = status
            _results.value = results.results
        }
    }

    companion object {
        private const val SCREEN_CLASS = "SearchScreen"
        private const val SCREEN_NAME = "Search"
        private const val TITLE = "Search"
    }

    fun onPageView() {
        analytics.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = TITLE
        )
    }

    fun onSearch(searchTerm: String) {
        this.searchTerm = searchTerm
        fetchSearchResults(searchTerm)
        analytics.search(searchTerm)
    }

    fun onSearchResultClicked(title: String, url: String) {
        analytics.searchResultClick(text = title, url = url)
    }
}
