package uk.gov.govuk.data.auth

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.appcheck.AppCheckToken
import com.google.firebase.appcheck.FirebaseAppCheck
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException

class FirebaseAttestationProviderTest {

    private val appCheck = mockk<FirebaseAppCheck>(relaxed = true)
    private val task = mockk<Task<AppCheckToken>>(relaxed = true)
    private val appCheckToken = mockk<AppCheckToken>(relaxed = true)

    @Test
    fun `Given app check failure return null`() = runTest {
        val slot = slot<OnFailureListener>()

        every { appCheck.getAppCheckToken(any()) } returns task
        every { task.addOnSuccessListener(any()) } returns task
        every { task.addOnFailureListener(capture(slot)) } returns task

        val provider = FirebaseAttestationProvider(appCheck)

        val deferred = async {
            provider.getToken()
        }

        yield()

        slot.captured.onFailure(IOException())
        assertNull(deferred.await())
    }

    @Test
    fun `Given app check success return token`() = runTest {
        val slot = slot<OnSuccessListener<AppCheckToken>>()

        every { appCheck.getAppCheckToken(any()) } returns task
        every { task.addOnSuccessListener(capture(slot)) } returns task
        every { task.addOnFailureListener(any()) } returns task
        every { appCheckToken.token } returns "token"

        val provider = FirebaseAttestationProvider(appCheck)

        val deferred = async {
            provider.getToken()
        }

        yield()

        slot.captured.onSuccess(appCheckToken)
        assertEquals("token", deferred.await())
    }
}