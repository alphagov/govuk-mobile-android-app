package uk.govuk.app.home

import junit.framework.TestCase.assertNotNull
import org.junit.Test

class HomeWidgetTest {

    @Test
    fun `Given we have a home widget enum class, check it's entries are not null`() {
        assertNotNull(HomeWidget.FEEDBACK_PROMPT)
        assertNotNull(HomeWidget.NOTIFICATIONS)
        assertNotNull(HomeWidget.RECENT_ACTIVITY)
        assertNotNull(HomeWidget.SEARCH)
        assertNotNull(HomeWidget.TOPICS)
    }
}
