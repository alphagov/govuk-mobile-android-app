package uk.govuk.app.search

import uk.govuk.app.search.data.remote.model.Result

internal sealed class SearchUiState(
    val searchTerm: String = "",
    val searchResults: List<Result> = listOf()
) {
    internal class Default(
        searchTerm: String,
        searchResults: List<Result>
    ) : SearchUiState(
        searchTerm = searchTerm,
        searchResults = searchResults
    )

    internal data object Offline : SearchUiState()

    internal data object ServiceError : SearchUiState()
}
