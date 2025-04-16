package uk.govuk.app.local

import kotlinx.coroutines.flow.Flow

fun interface LocalFeature {

    fun hasLocalAuthority(): Flow<Boolean>

}