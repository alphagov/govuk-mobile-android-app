package uk.govuk.app.visited

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.govuk.app.analytics.AnalyticsClient
import uk.govuk.app.visited.data.VisitedRepo
import uk.govuk.app.visited.data.store.VisitedLocalDataSource
import uk.govuk.app.visited.data.transformVisitedItems
import uk.govuk.app.visited.ui.model.VisitedUi
import java.time.LocalDate
import javax.inject.Inject

internal data class VisitedUiState(
    var visited: Map<String, List<VisitedUi>>?,
    var hasSelectedItems: Boolean = false,
    var hasAllSelectedItems: Boolean = false
)

@HiltViewModel
internal class VisitedViewModel @Inject constructor(
    private val visitedRepo: VisitedRepo,
    private val visitedLocalDataSource: VisitedLocalDataSource,
    private val visited: Visited,
    private val analyticsClient: AnalyticsClient
) : ViewModel() {

    companion object {
        private const val EDIT_SCREEN_CLASS = "EditVisitedScreen"
        private const val VIEW_SCREEN_CLASS = "VisitedScreen"
        private const val SCREEN_NAME = "Pages you've visited"
        private const val SCREEN_TITLE = "Pages you've visited"
        private const val REMOVE_ACTION = "Remove"
        private const val EDIT_BUTTON = "Edit"
        private const val REMOVE_BUTTON = "Remove"
        private const val SELECT_ALL_BUTTON = "Select all"
        private const val DESELECT_ALL_BUTTON = "Deselect all"
        private const val DONE_BUTTON = "Done"
    }

    private val _uiState: MutableStateFlow<VisitedUiState?> = MutableStateFlow(null)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            visitedRepo.visitedItems.collect { visitedItems ->
                val allVisitedItems = transformVisitedItems(visitedItems, LocalDate.now())
                _uiState.value = VisitedUiState(
                    visited = allVisitedItems,
                    hasSelectedItems = false,
                    hasAllSelectedItems = false
                )
            }
        }
    }

    fun onPageView() {
        analyticsClient.screenView(
            screenClass = VIEW_SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = SCREEN_TITLE
        )
    }

    fun onEditPageView() {
        analyticsClient.screenView(
            screenClass = EDIT_SCREEN_CLASS,
            screenName = SCREEN_NAME,
            title = SCREEN_TITLE
        )
    }

    fun onRemoveVisitedItem(title: String) {
        analyticsClient.buttonFunction(
            text = title,
            section = SCREEN_NAME,
            action = REMOVE_ACTION
        )
    }

    fun onEditClick() {
        analyticsClient.buttonFunction(
            text = "",
            section = SCREEN_NAME,
            action = EDIT_BUTTON
        )
    }

    fun onRemoveClick() {
        analyticsClient.buttonFunction(
            text = "",
            section = SCREEN_NAME,
            action = REMOVE_BUTTON
        )
    }

    fun onSelectAllClick() {
        analyticsClient.buttonFunction(
            text = "",
            section = SCREEN_NAME,
            action = SELECT_ALL_BUTTON
        )
    }

    fun onDeselectAllClick() {
        analyticsClient.buttonFunction(
            text = "",
            section = SCREEN_NAME,
            action = DESELECT_ALL_BUTTON
        )
    }

    fun onDoneClick() {
        analyticsClient.buttonFunction(
            text = "",
            section = SCREEN_NAME,
            action = DONE_BUTTON
        )
    }

    fun onVisitedItemClicked(title: String, url: String) {
        viewModelScope.launch {
            visited.visitableItemClick(title = title, url = url)
        }
        analyticsClient.visitedItemClick(text = title, url = url)
    }

    fun onRemove() {
        _uiState.value?.visited?.forEach { (_, visitedItems) ->
            visitedItems.forEach { visitedItem ->
                if (visitedItem.isSelected) {
                    viewModelScope.launch {
                        visitedLocalDataSource.remove(visitedItem.title, visitedItem.url)
                        onRemoveVisitedItem(visitedItem.title)
                    }
                }
            }
        }
    }

    fun onSelectAll() {
        _uiState.value = _uiState.value?.copy(
            visited = _uiState.value?.visited?.mapValues { (_, visitedItems) ->
                visitedItems.map { it.copy(isSelected = true) }
            },
            hasSelectedItems = true,
            hasAllSelectedItems = true
        )
    }

    fun onDeselectAll() {
        _uiState.value = _uiState.value?.copy(
            visited = _uiState.value?.visited?.mapValues { (_, visitedItems) ->
                visitedItems.map { it.copy(isSelected = false) }
            },
            hasSelectedItems = false,
            hasAllSelectedItems = false
        )
    }

    fun onSelect(title: String, url: String) {
        _uiState.value = _uiState.value?.let { currentState ->
            val updatedVisited = currentState.visited?.mapValues { (_, visitedItems) ->
                visitedItems.map {
                    if (it.title == title && it.url == url) {
                        it.copy(isSelected = !it.isSelected)
                    } else {
                        it
                    }
                }
            }

            val hasAllSelectedItems = updatedVisited?.all { (_, visitedItems) ->
                visitedItems.all { it.isSelected }
            } ?: false

            val hasSelectedItems = updatedVisited?.any { (_, visitedItems) ->
                visitedItems.any { it.isSelected }
            } ?: false

            currentState.copy(
                visited = updatedVisited,
                hasAllSelectedItems = hasAllSelectedItems,
                hasSelectedItems = hasSelectedItems
            )
        }
    }
}
