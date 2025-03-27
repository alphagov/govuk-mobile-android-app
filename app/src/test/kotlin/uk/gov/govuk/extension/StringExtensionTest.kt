package uk.gov.govuk.extension

import org.junit.Assert.assertEquals
import org.junit.Test

class StringExtensionTest {
    @Test
    fun `Given a deep link path, When asDeepLinks() called on it, should return a list of deep links`() {
        val deepLinks = "/path".asDeepLinks()

        assertEquals(3, deepLinks.size)
        assertEquals("govuk://app.gov.uk/path", deepLinks[0].uriPattern)
        assertEquals("govuk://gov.uk/path", deepLinks[1].uriPattern)
        assertEquals("https://app.gov.uk/path", deepLinks[2].uriPattern)
    }
}
