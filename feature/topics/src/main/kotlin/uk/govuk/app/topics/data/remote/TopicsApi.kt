package uk.govuk.app.topics.data.remote

import retrofit2.Response
import retrofit2.http.GET
import uk.govuk.app.topics.data.remote.model.RemoteTopicItem

internal interface TopicsApi {
    @GET("list")
    suspend fun getTopics(): Response<List<RemoteTopicItem>>
}