package uk.govuk.app.search

import uk.govuk.app.search.data.remote.model.Result
import java.util.UUID

internal sealed class SearchUiState() {
    internal class Results(
        val searchTerm: String,
        val searchResults: List<Result>
    ) : SearchUiState()

    internal sealed class Error(
        val uuid: UUID
    ) : SearchUiState() {
        internal class Empty(uuid: UUID, val searchTerm: String) : Error(uuid)

        internal class Offline(uuid: UUID, val searchTerm: String) : Error(uuid)

        internal class ServiceError(uuid: UUID) : Error(uuid)
    }
}
