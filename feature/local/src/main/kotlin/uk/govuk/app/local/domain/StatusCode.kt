package uk.govuk.app.local.domain

object StatusCode {
    const val INVALID_POSTCODE = 400
    const val POSTCODE_NOT_FOUND = 404
    const val NO_POSTCODE_GIVEN = 418

    fun isErrorStatus(status: Int?): Boolean {
        return status == INVALID_POSTCODE || status == POSTCODE_NOT_FOUND || status == NO_POSTCODE_GIVEN
    }
}
