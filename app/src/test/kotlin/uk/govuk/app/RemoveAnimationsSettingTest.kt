package uk.govuk.app

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import uk.govuk.app.ui.areAnimationsDisabled

class RemoveAnimationsSettingTest {
    private val context = mockk<Context>()

    @Before
    fun setup() {
        mockkStatic(Settings.Global::class)
        every { context.contentResolver } returns mockk()
    }

    @Test
    fun `Given animations are allowed, then animations will not be disabled`() {
        every {
            Settings.Global.getFloat(any<ContentResolver>(), Settings.Global.ANIMATOR_DURATION_SCALE, any<Float>())
        } returns 1f

        runTest {
            assertFalse(areAnimationsDisabled(context))
        }
    }

    @Test
    fun `Given animations are not allowed, then animations will be disabled`() {
        every {
            Settings.Global.getFloat(any<ContentResolver>(), Settings.Global.ANIMATOR_DURATION_SCALE, any<Float>())
        } returns 0f

        runTest {
            assertTrue(areAnimationsDisabled(context))
        }
    }
}
