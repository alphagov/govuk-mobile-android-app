package uk.gov.govuk

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TimeoutManager @Inject constructor(

) {
    private var job: Job? = null
    private var lastInteractionTime: Long = 0L

    fun onUserInteraction(
        interactionTime: Long = SystemClock.elapsedRealtime(),
        timeoutInterval: Long = 15 * 60 * 1000L, // 15 mins
        onTimeout: () -> Unit,
    ) {
        if (interactionTime - lastInteractionTime >= 1000) {
            lastInteractionTime = interactionTime
            job?.cancel()
            job = CoroutineScope(Dispatchers.Main).launch {
                delay(timeoutInterval)
                onTimeout()
            }
        }
    }
}