package uk.gov.govuk.topics.ui

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import uk.gov.govuk.design.ui.component.ChildPageHeader
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.topics.R
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
internal fun ExampleContentItemRoute(
    contentItemUrl: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    launchBrowser: (url: String) -> Unit
) {
    // TODO: this assumes the content item is of type "transaction"
    // TODO: loading state
    // TODO: error state

    val context = LocalContext.current
    val cssVariables = """
    :root {
      --background-colour: ${hex(GovUkTheme.colourScheme.surfaces.background)};
      --govspeak-text-colour: ${hex(GovUkTheme.colourScheme.textAndIcons.primary)};
      --govspeak-text-colour-inverse: ${hex(GovUkTheme.colourScheme.textAndIcons.primary)};
      --govuk-border-colour: ${hex(GovUkTheme.colourScheme.strokes.fixedContainer)};
      --govuk-focus-colour: ${hex(GovUkTheme.colourScheme.textAndIcons.buttonCompactFocused)};
      --govuk-focus-colour: ${hex(GovUkTheme.colourScheme.textAndIcons.buttonPrimaryFocused)};
      --govuk-focus-text-colour: ${hex(GovUkTheme.colourScheme.textAndIcons.buttonPrimaryFocused)};
      --govuk-link-active-colour: ${hex(GovUkTheme.colourScheme.textAndIcons.link)};
      --govuk-link-active-colour-inverse: ${hex(GovUkTheme.colourScheme.textAndIcons.link)};
      --govuk-link-colour: ${hex(GovUkTheme.colourScheme.textAndIcons.link)};
      --govuk-link-colour-inverse: ${hex(GovUkTheme.colourScheme.textAndIcons.link)};
      --govuk-link-hover-colour: ${hex(GovUkTheme.colourScheme.textAndIcons.link)};
      --govuk-link-hover-colour-inverse: ${hex(GovUkTheme.colourScheme.textAndIcons.link)};
      --govuk-link-visited-colour: ${hex(GovUkTheme.colourScheme.textAndIcons.link)};
      --govuk-link-visited-colour-inverse: ${hex(GovUkTheme.colourScheme.textAndIcons.link)};
      --govspeak-info-notice-border-colour: ${hex(GovUkTheme.colourScheme.strokes.govspeakInfoCalloutBorder)};
    }
    """.trimIndent()
    val cssFontFace = """
        @font-face {
          font-family: "GDS Transport";
          font-style: normal;
          font-weight: normal;
          src: url("file:///android_asset/fonts/transport_light.ttf");
          font-display: fallback;
        }
        @font-face {
          font-family: "GDS Transport";
          font-style: normal;
          font-weight: bold;
          src: url("file:///android_asset/fonts/transport_bold.ttf");
          font-display: fallback;
        }
    """.trimIndent()

    var title by remember { mutableStateOf("") }
    var startButtonText by remember { mutableStateOf("") }
    var transactionStartLink by remember { mutableStateOf("") }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var webViewHeight by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()

    LaunchedEffect(contentItemUrl) {
        withContext(Dispatchers.IO) {
            val cssStream = context.resources.openRawResource(R.raw.govspeak)
            val cssReader = BufferedReader(InputStreamReader(cssStream))
            val cssStringBuilder = StringBuilder()
            cssStringBuilder.append(cssVariables)
            cssStringBuilder.append(cssFontFace)
            var line = cssReader.readLine()
            while (line != null) {
                cssStringBuilder.append(line)
                cssStringBuilder.append("\n")
                line = cssReader.readLine()
            }
            cssReader.close()
            cssStream.close()
            val css = cssStringBuilder.toString()

            val json = fetchJson(contentItemUrl)
            title = json.getString("title")
            val details = json.getJSONObject("details")
            startButtonText = details.getString("start_button_text")
            transactionStartLink = details.getString("transaction_start_link")
            val introParagraph = details.getString("introductory_paragraph")
            webView!!.post {
                webView!!.loadDataWithBaseURL(
                    null,
                    """
                        <style>
                        $css
                        </style>
                        <div class="gem-c-govspeak govuk-govspeak">
                        $introParagraph
                        <div>
                    """.trimIndent(),
                    "text/html; charset=utf-8",
                    "utf-8",
                    null)
            }
        }
    }
    Column(modifier.fillMaxSize().verticalScroll(scrollState)) {
        ChildPageHeader(
            text = title,
            onBack = onBack
        )
        AndroidView(factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        super.onPageFinished(view, url)

                        evaluateJavascript(
                            "(function() { return document.body.scrollHeight }())"
                        ) { value ->
                            val contentHeight = value.toFloatOrNull() ?: 0f
                            val density = resources.displayMetrics.density
                            webViewHeight = (contentHeight * density).toInt()
                        }
                    }
                }
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                webView = this
                loadDataWithBaseURL(
                    null,
                    "",
                    "text/html; charset=utf-8",
                    "utf-8",
                    null
                )
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .height(with(LocalDensity.current) { webViewHeight.toDp() }))
        PrimaryButton(
            text = startButtonText,
            onClick = {
                launchBrowser(transactionStartLink)
            },
            externalLink = true,
        )
        MediumVerticalSpacer()
    }
}

private fun fetchJson(urlString: String): JSONObject {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url(urlString)
        .build();

    val response = client.newCall(request).execute()
    return JSONObject(response.body()!!.string())
}
private fun hex(color: Color): String {
    return String.format("#%06X", (0xFFFFFF and color.toArgb()))
}
