package uk.govuk.app.search

import uk.govuk.app.search.data.remote.model.SearchResult
import java.util.UUID

internal sealed class SearchUiState() {

    internal class Default(
        val previousSearches: List<String>
    ): SearchUiState()

    internal class Results(
        val searchTerm: String,
        val searchResults: List<SearchResult>
    ) : SearchUiState()

    internal sealed class Error(
        val uuid: UUID
    ) : SearchUiState() {
        internal class Empty(uuid: UUID, val searchTerm: String) : Error(uuid)

        internal class Offline(uuid: UUID, val searchTerm: String) : Error(uuid)

        internal class ServiceError(uuid: UUID) : Error(uuid)
    }
}
