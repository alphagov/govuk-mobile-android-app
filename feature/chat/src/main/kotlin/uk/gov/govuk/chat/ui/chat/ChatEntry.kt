package uk.gov.govuk.chat.ui.chat

import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.widget.ImageView
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.ui.model.ChatEntry
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

private enum class DisplayState { Idle, Loading, Answer }

@Composable
internal fun DisplayChatEntry(
    chatEntry: ChatEntry,
    isLoading: Boolean,
    shouldAnimate: Boolean,
    launchBrowser: (url: String) -> Unit,
    onMarkdownLinkClicked: (String, String) -> Unit,
    onSourcesExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        MediumVerticalSpacer()
        Question(question = chatEntry.question)
        MediumVerticalSpacer()

        if (shouldAnimate) {
            AnimatedChatEntry(
                chatEntry = chatEntry,
                isLoading = isLoading,
                launchBrowser = launchBrowser,
                onMarkdownLinkClicked = onMarkdownLinkClicked,
                onSourcesExpanded = onSourcesExpanded
            )
        } else {
            InstantChatEntry(
                chatEntry = chatEntry,
                isLoading = isLoading,
                launchBrowser = launchBrowser,
                onMarkdownLinkClicked = onMarkdownLinkClicked,
                onSourcesExpanded = onSourcesExpanded
            )
        }
    }
}

@Composable
private fun AnimatedChatEntry(
    chatEntry: ChatEntry,
    isLoading: Boolean,
    launchBrowser: (url: String) -> Unit,
    onMarkdownLinkClicked: (String, String) -> Unit,
    onSourcesExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayState = remember(chatEntry.id) { mutableStateOf(DisplayState.Idle) }

    LaunchedEffect(isLoading, chatEntry.answer) {
        when {
            isLoading && chatEntry.answer.isEmpty() -> {
                displayState.value = DisplayState.Loading
            }

            chatEntry.answer.isNotEmpty() -> {
                displayState.value = DisplayState.Answer
            }

            else -> {
                displayState.value = DisplayState.Idle
            }
        }
    }

    Crossfade(
        targetState = displayState.value,
        animationSpec = tween(durationMillis = 1000)
    ) { state ->
        when (state) {
            DisplayState.Loading -> Loading(modifier)
            DisplayState.Answer -> Answer(
                answer = chatEntry.answer,
                sources = chatEntry.sources,
                launchBrowser = launchBrowser,
                onMarkdownLinkClicked = onMarkdownLinkClicked,
                onSourcesExpanded = onSourcesExpanded
            )

            DisplayState.Idle -> {}
        }
    }
}

@Composable
private fun InstantChatEntry(
    chatEntry: ChatEntry,
    isLoading: Boolean,
    launchBrowser: (url: String) -> Unit,
    onMarkdownLinkClicked: (String, String) -> Unit,
    onSourcesExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading && chatEntry.answer.isEmpty()) {
        Loading(modifier)
    } else {
        Answer(
            answer = chatEntry.answer,
            sources = chatEntry.sources,
            launchBrowser = launchBrowser,
            onMarkdownLinkClicked = onMarkdownLinkClicked,
            onSourcesExpanded = onSourcesExpanded,
            modifier = modifier
        )

    }
}

@Composable
private fun Loading(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AndroidView(factory = {
            ImageView(it).apply {
                val source = ImageDecoder.createSource(context.resources, R.drawable.ic_generating_answer)
                val drawable = ImageDecoder.decodeDrawable(source)
                setImageDrawable(drawable)
                if (drawable is AnimatedImageDrawable) {
                    drawable.start()
                }
            }
        })

        SmallHorizontalSpacer()

        var dots by remember { mutableIntStateOf(0) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(1200L)
                dots = (dots + 1) % 4 // 0,1,2,3
            }
        }

        Text(
            text = stringResource(R.string.loading_text) + ".".repeat(dots),
            color = GovUkTheme.colourScheme.textAndIcons.chatLoadingTextDark,
            style = GovUkTheme.typography.bodyRegular
        )
    }
}