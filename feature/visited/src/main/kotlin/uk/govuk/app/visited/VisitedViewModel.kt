package uk.govuk.app.visited

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import javax.inject.Inject

internal data class VisitedUiState(
    val visited: List<VisitedUi>
)

internal data class VisitedUi(
    val title: String,
    val url: String,
    val lastVisited: String,
)

@HiltViewModel
internal class VisitedViewModel @Inject constructor(
    private val analytics: Analytics
): ViewModel() {

    companion object {
        private const val SCREEN_CLASS = "VisitedScreen"
        private const val SCREEN_NAME = "Pages you've visited"
        private const val SCREEN_TITLE = "Pages you've visited"
    }

    private val _uiState: MutableStateFlow<VisitedUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value =
                VisitedUiState(
                    listOf(
                        VisitedUi("GOV.UK", "https://www.gov.uk", "24 July 2024"),
                        VisitedUi("Amazon UK", "https://www.amazon.co.uk", "24 July 2024"),
                        VisitedUi("BBC", "https://www.bbc.co.uk", "10 July 2024"),
                        VisitedUi("Slack", "https://slack.com", "2 October 2023"),
                        VisitedUi("Trello", "https://trello.com", "1 October 2023"),
                        VisitedUi("Google", "https://google.com", "1 October 2023")
                    )
                )
        }
    }

    fun onPageView() {
        analytics.screenView(
            screenClass = SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = SCREEN_TITLE
        )
    }

    fun onVisitedItemClicked(title: String, url: String) {
        analytics.visitedItemClick(text = title, url = url)
    }
}
