package uk.gov.govuk.chat

import kotlinx.coroutines.flow.Flow

interface ChatFeature {

    suspend fun clear()

    fun hasOptedIn(): Flow<Boolean>

    suspend fun userHasNotYetChosen(): Boolean

}
