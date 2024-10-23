package uk.govuk.app.visited

import org.junit.Assert.assertEquals
import org.junit.Test
import uk.govuk.app.visited.data.capitaliseMonth
import uk.govuk.app.visited.data.localDateFormatter
import java.time.LocalDate

class DateFormatterTest {
    @Test
    fun `Given a date in epoch days, then you get back a formatted date string`() {
        val dayInMillis = LocalDate.of(2023, 10, 15).toEpochDay()

        val actual = localDateFormatter(dayInMillis)
        val expected = "15 October 2023"

        assertEquals(expected, actual)
    }

    @Test
    fun `Given an uppercase month, then you get back a capitalised month`() {
        val month = "OCTOBER"

        val actual = capitaliseMonth(month)
        val expected = "October"

        assertEquals(expected, actual)
    }

    @Test
    fun `Given a lower month, then you get back a capitalised month`() {
        val month = "october"

        val actual = capitaliseMonth(month)
        val expected = "October"

        assertEquals(expected, actual)
    }
}
