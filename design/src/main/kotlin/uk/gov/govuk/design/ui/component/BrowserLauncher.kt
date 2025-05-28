package uk.gov.govuk.design.ui.component

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsIntent.ACTIVITY_HEIGHT_FIXED
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.net.toUri

@Composable
fun rememberBrowserLauncher(): BrowserActivityLauncher {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    return remember(launcher) {
        BrowserActivityLauncher(launcher)
    }
}

class BrowserActivityLauncher(
    private val launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    fun launch(context: Context, url: String) {
        val intent = context.getCustomTabsIntent(url)
        launcher.launch(intent)
    }
}

private fun Context.getCustomTabsIntent(url: String): Intent {
    val displayMetrics = this.resources.displayMetrics
    val screenHeight = displayMetrics.heightPixels
    val customTabsIntent = CustomTabsIntent.Builder()
        .setInitialActivityHeightPx(
            screenHeight,
            ACTIVITY_HEIGHT_FIXED
        )
        .setBackgroundInteractionEnabled(false)
        .build()
    customTabsIntent.intent.data = url.toUri()
    return customTabsIntent.intent
}
