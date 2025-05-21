package uk.gov.govuk

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class TimeoutManager @Inject constructor() {
    private val handler = Handler(Looper.getMainLooper())
    private var lastInteractionTime: Long = 0L

    fun onUserInteraction(
        interactionTime: Long = SystemClock.elapsedRealtime(),
        timeoutInterval: Long = 15 * 60 * 1000L, // 15 mins
        onTimeout: () -> Unit,
    ) {
        if (interactionTime - lastInteractionTime > 1000) {
            lastInteractionTime = interactionTime
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed(
                {
                    onTimeout()
                },
                timeoutInterval
            )
        }
    }

}