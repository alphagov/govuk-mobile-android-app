package uk.gov.govuk.chat

import kotlinx.coroutines.flow.Flow
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.data.local.ChatDataStore
import javax.inject.Inject

internal class DefaultChatFeature @Inject constructor(
    private val chatRepo: ChatRepo,
    private val dataStore: ChatDataStore
): ChatFeature {

    override suspend fun clear() {
        chatRepo.clearConversation()
    }

    override fun hasOptedIn(): Flow<Boolean> = dataStore.hasOptedIn

    override suspend fun userHasNotYetChosen(): Boolean = dataStore.isChatOptInNull()
}
