package uk.gov.govuk.ui

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

internal enum class Presentation {
    FULL_SCREEN,
    PARTIAL_SCREEN
}

@Composable
internal fun rememberBrowserLauncher(shouldShowInAppBrowser: Boolean): BrowserActivityLauncher {
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    return remember(launcher) {
        if (shouldShowInAppBrowser) {
            BrowserActivityLauncher.InApp(launcher)
        } else {
            BrowserActivityLauncher.External(launcher)
        }
    }
}

internal sealed class BrowserActivityLauncher(
    val launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {
    abstract fun launch(
        context: Context,
        url: String,
        presentation: Presentation = Presentation.FULL_SCREEN
    )

    internal class External(
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
    ) : BrowserActivityLauncher(launcher) {
        override fun launch(context: Context, url: String, presentation: Presentation) {
            Intent(Intent.ACTION_VIEW).run {
                data = url.toUri()
                launcher.launch(this)
            }
        }
    }

    internal class InApp(
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
    ) : BrowserActivityLauncher(launcher) {
        override fun launch(context: Context, url: String, presentation: Presentation) {
            val customTabsIntent = when (presentation) {
                Presentation.FULL_SCREEN -> CustomTabsIntent.Builder().build()
                Presentation.PARTIAL_SCREEN -> context.getPartialCustomTabsIntent()
            }
            customTabsIntent.intent.data = url.toUri()
            launcher.launch(customTabsIntent.intent)
        }
    }
}

private fun Context.getPartialCustomTabsIntent(): CustomTabsIntent {
    val displayMetrics = this.resources.displayMetrics
    val screenHeight = displayMetrics.heightPixels
    return CustomTabsIntent.Builder()
        .setInitialActivityHeightPx(
            screenHeight,
            ACTIVITY_HEIGHT_FIXED
        )
        .setBackgroundInteractionEnabled(false)
        .build()
}
