package uk.govuk.app.local.domain

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StatusCodeTest {
    @Test
    fun `isErrorStatus with valid error status INVALID POSTCODE`() {
        assertTrue(StatusCode.isErrorStatus(400))
    }

    @Test
    fun `isErrorStatus with valid error status POSTCODE NOT FOUND`() {
        assertTrue(StatusCode.isErrorStatus(404))
    }

    @Test
    fun `isErrorStatus with valid error status NO POSTCODE GIVEN`() {
        assertTrue(StatusCode.isErrorStatus(418))
    }

    @Test
    fun `isErrorStatus with valid success status`() {
        assertFalse(StatusCode.isErrorStatus(200))
    }

    @Test
    fun `isErrorStatus with null status`() {
        assertFalse(StatusCode.isErrorStatus(null))
    }

    @Test
    fun `isErrorStatus with negative status`() {
        assertFalse(StatusCode.isErrorStatus(-400))
    }
}
