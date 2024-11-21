package uk.govuk.app.search

import uk.govuk.app.search.data.remote.model.Result

internal sealed class SearchUiState(
    val searchTerm: String
) {
    internal class Default(
        searchTerm: String,
        val searchResults: List<Result>
    ) : SearchUiState(searchTerm = searchTerm)

    internal class Offline(searchTerm: String) : SearchUiState(searchTerm = searchTerm)

    internal class ServiceError(searchTerm: String) : SearchUiState(searchTerm = searchTerm)
}
