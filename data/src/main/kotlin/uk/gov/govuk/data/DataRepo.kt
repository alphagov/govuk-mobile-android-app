package uk.gov.govuk.data

import uk.gov.govuk.data.local.DataDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepo @Inject constructor(
    private val dataDataStore: DataDataStore
) {
    internal suspend fun getRealmKey() = dataDataStore.getRealmKey()

    internal suspend fun saveRealmKey(key: String) = dataDataStore.saveRealmKey(key)

    internal suspend fun getRealmIv() = dataDataStore.getRealmIv()

    internal suspend fun saveRealmIv(iv: String) = dataDataStore.saveRealmIv(iv)

    internal suspend fun getSubId() = dataDataStore.getSubId()

    internal suspend fun saveSubId(subId: String) = dataDataStore.saveSubId(subId)
}
