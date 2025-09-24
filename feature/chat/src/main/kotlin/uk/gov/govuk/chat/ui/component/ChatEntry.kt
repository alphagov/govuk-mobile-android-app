package uk.gov.govuk.chat.ui.component

import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.widget.ImageView
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.ui.model.ChatEntry
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer

private enum class DisplayState { Idle, Loading, Answer }

@Composable
internal fun DisplayChatEntry(
    chatEntry: ChatEntry,
    isLoading: Boolean,
    onMarkdownLinkClicked: (String, String) -> Unit,
    onSourcesExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        MediumVerticalSpacer()
        Question(question = chatEntry.question)
        MediumVerticalSpacer()
        AnimatedChatEntry(
            chatEntry = chatEntry,
            isLoading = isLoading,
            onMarkdownLinkClicked = onMarkdownLinkClicked,
            onSourcesExpanded = onSourcesExpanded
        )
    }
}

@Composable
private fun AnimatedChatEntry(
    chatEntry: ChatEntry,
    isLoading: Boolean,
    onMarkdownLinkClicked: (String, String) -> Unit,
    onSourcesExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayState = rememberSaveable(chatEntry.id) { mutableStateOf(DisplayState.Idle) }

    LaunchedEffect(isLoading, chatEntry.answer) {
        when {
            chatEntry.answer.isNotBlank() -> {
                displayState.value = DisplayState.Answer
            }

            isLoading -> {
                displayState.value = DisplayState.Loading
            }

            else -> {
                displayState.value = DisplayState.Idle
            }
        }
    }

    Crossfade(
        targetState = displayState.value,
        animationSpec = if (chatEntry.shouldAnimate) tween(durationMillis = 1000) else snap()
    ) { state ->
        when (state) {
            DisplayState.Loading -> Loading(modifier)
            DisplayState.Answer -> Answer(
                answer = chatEntry.answer,
                sources = chatEntry.sources,
                onMarkdownLinkClicked = onMarkdownLinkClicked,
                onSourcesExpanded = onSourcesExpanded
            )

            DisplayState.Idle -> {}
        }
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
        AndroidView(
            factory = {
                ImageView(it).apply {
                    val source = ImageDecoder.createSource(context.resources, R.drawable.ic_generating_answer)
                    val drawable = ImageDecoder.decodeDrawable(source)
                    setImageDrawable(drawable)
                    if (drawable is AnimatedImageDrawable) {
                        drawable.start()
                    }
                }
            },
            modifier = Modifier.size(24.dp)
        )

        SmallHorizontalSpacer()

        var dots by remember { mutableIntStateOf(0) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(1200L)
                dots = (dots + 1) % 4 // 0,1,2,3
            }
        }

        BodyRegularLabel(
            text = stringResource(R.string.loading_text) + ".".repeat(dots),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}