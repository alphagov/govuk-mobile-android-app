package uk.gov.govuk.design.ui.extension

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsIntent.ACTIVITY_HEIGHT_FIXED
import androidx.core.net.toUri

fun Context.areAnimationsEnabled(): Boolean {
    val animatorDurationScale = Settings.Global.getFloat(
        this.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f
    )
    return animatorDurationScale > 0f
}

fun Context.getCustomTabsIntent(url: String): Intent {
    val displayMetrics = resources.displayMetrics
    val screenHeight = displayMetrics.heightPixels
    val customTabsIntent = CustomTabsIntent.Builder().setInitialActivityHeightPx(
        screenHeight,
        ACTIVITY_HEIGHT_FIXED
    ).build()
    customTabsIntent.intent.data = url.toUri()
    return customTabsIntent.intent
}
