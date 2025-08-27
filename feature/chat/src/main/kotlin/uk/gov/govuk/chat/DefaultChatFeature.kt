package uk.gov.govuk.chat

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

    override suspend fun isEnabled(): Boolean {
        return dataStore.isChatOptIn()
    }
}
