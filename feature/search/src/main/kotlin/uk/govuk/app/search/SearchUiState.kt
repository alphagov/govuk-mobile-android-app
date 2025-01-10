package uk.govuk.app.search

import uk.govuk.app.search.data.remote.model.Result
import java.util.UUID

internal sealed class SearchUiState(
    val uuid: UUID,
    val searchTerm: String
) {
    internal class Default(
        uuid: UUID,
        searchTerm: String,
        val searchResults: List<Result>
    ) : SearchUiState(uuid = uuid, searchTerm = searchTerm)

    internal class Empty(uuid: UUID, searchTerm: String) : SearchUiState(uuid = uuid, searchTerm = searchTerm)

    internal class Offline(uuid: UUID, searchTerm: String) : SearchUiState(uuid = uuid, searchTerm = searchTerm)

    internal class ServiceError(uuid: UUID, searchTerm: String) : SearchUiState(uuid = uuid, searchTerm = searchTerm)
}
