package uk.govuk.app.visited

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.Analytics
import uk.govuk.app.visited.data.VisitedItemsTransformer
import uk.govuk.app.visited.data.VisitedRepo
import uk.govuk.app.visited.ui.model.VisitedUi
import java.time.LocalDate
import javax.inject.Inject


internal data class VisitedUiState(
    val visited: Map<String, List<VisitedUi>>
)

@HiltViewModel
internal class VisitedViewModel @Inject constructor(
    private val visitedRepo: VisitedRepo,
    private val visited: Visited,
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
            visitedRepo.visitedItems.collect { visitedItems ->
                val transformer = VisitedItemsTransformer(visitedItems, LocalDate.now())
                val allVisitedItems = transformer.transform()

                _uiState.value = VisitedUiState(visited = allVisitedItems)
            }
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
        viewModelScope.launch {
            visited.visitableItemClick(title = title, url = url)
        }
        analytics.visitedItemClick(text = title, url = url)
    }
}
