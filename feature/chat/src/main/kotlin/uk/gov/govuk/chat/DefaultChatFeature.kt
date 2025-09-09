package uk.gov.govuk.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import uk.gov.govuk.chat.data.ChatRepo
import uk.gov.govuk.chat.data.local.ChatDataStore
import javax.inject.Inject

internal class DefaultChatFeature @Inject constructor(
    private val chatRepo: ChatRepo,
    private val dataStore: ChatDataStore
): ChatFeature {

    override suspend fun clear() {
        chatRepo.clear()
    }

    override fun hasOptedIn(): Flow<Boolean> = dataStore.hasOptedIn

    override suspend fun shouldDisplayOptIn(
        isChatOptInEnabled: Boolean,
        isChatTestActive: Boolean
    ): Boolean {
        return isChatOptInEnabled
                && isChatTestActive
                && dataStore.isChatOptInNull()
    }

    override suspend fun shouldDisplayTestEnded(isChatTestActive: Boolean): Boolean {
        return when {
            isChatTestActive -> false
            dataStore.hasOptedIn.firstOrNull() == true -> true
            else -> {
                dataStore.clearChatOptIn()
                false
            }
        }
    }
}
