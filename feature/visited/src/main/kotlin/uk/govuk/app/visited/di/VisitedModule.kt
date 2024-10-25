package uk.govuk.app.visited.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.govuk.app.visited.Visited
import uk.govuk.app.visited.VisitedClient
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal class VisitedModule {
    @Provides
    @Singleton
    fun provideVisited(visitedClient: VisitedClient): Visited = visitedClient
}
