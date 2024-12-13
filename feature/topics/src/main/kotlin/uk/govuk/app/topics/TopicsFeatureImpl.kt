package uk.govuk.app.topics

import uk.govuk.app.topics.data.TopicsRepo
import javax.inject.Inject
import javax.inject.Singleton

// Todo - better naming!!!
@Singleton
internal class TopicsFeatureImpl @Inject constructor(
    private val topicsRepo: TopicsRepo
) : TopicsFeature {

    override suspend fun init(): Boolean {
        return topicsRepo.sync()
    }
}