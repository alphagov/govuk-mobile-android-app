package uk.govuk.app.search.domain

sealed class ResultStatus {
    data object Success : ResultStatus()
    data object Empty : ResultStatus()
    data object DeviceOffline : ResultStatus()
    data object ServiceNotResponding : ResultStatus()
    data class Error(val message: String) : ResultStatus()
}
