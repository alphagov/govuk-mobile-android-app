package uk.gov.govuk.data.remote

import org.junit.Assert.assertEquals
import org.junit.Test

class ApiCallTest {
    @Test
    fun `Given an empty string, then return an empty string`() {
        val string = ""

        assertEquals(string, HtmlCleaner.toPlainText(string))
    }

    @Test
    fun `Given a string that has no HTML tags, then return the same string`() {
        val string = "This string has no HTML tags"

        assertEquals(string, HtmlCleaner.toPlainText(string))
    }

    @Test
    fun `Given a string that has HTML tags, then return the string minus the HTML tags`() {
        val string =
            """
            <p>Hello <b>World</b>!</p>
            <a href="https://example.com">Safe Link</a>
            <a href="javascript:alert('XSS Attack')">Malicious Link</a>
            <script>alert('XSS Attack')</script>
            """.trimIndent()

        assertEquals(
            "Hello World!\n" +
                "Safe Link\n" +
                "Malicious Link\n",
            HtmlCleaner.toPlainText(string)
        )
    }
}
