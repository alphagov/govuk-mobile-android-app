package uk.govuk.app.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.search.api.SearchResultsRepository
import uk.govuk.app.search.api_result.Result
import uk.govuk.app.search.api_result.ResultStatus
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val analytics: Analytics
): ViewModel() {
    private val repository = SearchResultsRepository()
    private val _results = MutableLiveData<List<Result>>()
    private val _resultStatus = MutableLiveData<ResultStatus>()

    val searchResults: LiveData<List<Result>> = _results
    val resultStatus: LiveData<ResultStatus> = _resultStatus
    var searchTerm: String = ""

    private fun fetchSearchResults(searchTerm: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSearchResults(searchTerm)

                if (response.total == 0) {
                    _resultStatus.value = ResultStatus.NO_RESULTS_FOUND
                } else {
                    _resultStatus.value = ResultStatus.SUCCESS
                    _results.value = response.results
                }
            } catch (deviceOfflineException: java.net.UnknownHostException) {
                _resultStatus.value = ResultStatus.DEVICE_OFFLINE
            } catch (serviceNotRespondingException: retrofit2.HttpException) {
                _resultStatus.value = ResultStatus.SERVICE_NOT_RESPONDING
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
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
