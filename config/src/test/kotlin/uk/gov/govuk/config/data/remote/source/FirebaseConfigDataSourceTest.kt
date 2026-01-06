package uk.gov.govuk.config.data.remote.source

import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FirebaseConfigDataSourceTest {

    private val firebaseRemoteConfig = mockk<FirebaseRemoteConfig>()
    private val dataSource = FirebaseConfigDataSource(firebaseRemoteConfig)

    private fun <T> mockFailureTask(exception: Exception): Task<T> {
        val task = mockk<Task<T>>()
        every { task.isComplete } returns true
        every { task.isCanceled } returns false
        every { task.exception } returns exception
        return task
    }

    @Test
    fun `Given fetch succeeds, when fetch is called, then return true`() = runTest {
        val mockVoidTask = mockk<Task<Void>> {
            every { isComplete } returns true
            every { isCanceled } returns false
            every { exception } returns null
            every { result } returns null
        }
        every { firebaseRemoteConfig.fetch() } returns mockVoidTask

        val result = dataSource.fetch()
        assertTrue(result)
    }

    @Test
    fun `Given fetch task fails, when fetch is called, then return false`() = runTest {
        every { firebaseRemoteConfig.fetch() } returns mockFailureTask(Exception("Exception"))

        val result = dataSource.fetch()
        assertFalse(result)
    }

    @Test
    fun `Given fetch throws immediate exception, when fetch is called, then return false`() =
        runTest {
            every { firebaseRemoteConfig.fetch() } throws Exception("Exception")

            val result = dataSource.fetch()
            assertFalse(result)
        }

    @Test
    fun `Given activate succeeds, when activate is called, then return true`() = runTest {
        val mockSuccessTask = mockk<Task<Boolean>> {
            every { isComplete } returns true
            every { isCanceled } returns false
            every { exception } returns null
            every { result } returns true
        }
        every { firebaseRemoteConfig.activate() } returns mockSuccessTask

        val result = dataSource.activate()
        assertTrue(result)
    }

    @Test
    fun `Given activate task fails, when activate is called, then return false`() = runTest {
        every { firebaseRemoteConfig.activate() } returns mockFailureTask(Exception("Exception"))

        val result = dataSource.activate()
        assertFalse(result)
    }

    @Test
    fun `Given activate throws exception, when activate is called, then return false`() = runTest {
        every { firebaseRemoteConfig.activate() } throws Exception("Exception")

        val result = dataSource.activate()
        assertFalse(result)
    }
}