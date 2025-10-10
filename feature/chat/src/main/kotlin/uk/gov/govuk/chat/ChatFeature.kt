package uk.gov.govuk.chat

import kotlinx.coroutines.flow.Flow

interface ChatFeature {

    suspend fun clear()

    fun hasOptedIn(): Flow<Boolean>

    suspend fun shouldDisplayOptIn(
        isChatOptInEnabled: Boolean,
        isChatTestActive: Boolean
    ): Boolean

    suspend fun shouldDisplayTestEnded(
        isChatTestActive: Boolean
    ): Boolean

}
