package uk.gov.govuk.chat.ui.component

import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.widget.ImageView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallHorizontalSpacer

@Composable
internal fun ChatEntry(
    chatEntry: ChatEntry,
    onMarkdownLinkClicked: (String, String) -> Unit,
    onSourcesExpanded: () -> Unit,
    animationDuration: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LargeVerticalSpacer()
        Question(question = chatEntry.question)
        LargeVerticalSpacer()
        AnimatedChatEntry(
            chatEntry = chatEntry,
            onMarkdownLinkClicked = onMarkdownLinkClicked,
            animationDuration = animationDuration,
            onSourcesExpanded = onSourcesExpanded
        )
    }
}

@Composable
private fun AnimatedChatEntry(
    chatEntry: ChatEntry,
    onMarkdownLinkClicked: (String, String) -> Unit,
    onSourcesExpanded: () -> Unit,
    animationDuration: Int,
    modifier: Modifier = Modifier
) {
    var showLoading by rememberSaveable(chatEntry.id) { mutableStateOf(false) }
    var showAnswer by rememberSaveable(chatEntry.id) { mutableStateOf(false) }

    LaunchedEffect(chatEntry.answer) {
        if (chatEntry.answer.isNotBlank()) {
            if (showLoading && chatEntry.shouldAnimate) {
                showLoading = false
                delay(animationDuration.toLong())
            }
            showAnswer = true
        } else {
            showAnswer = false
            delay(animationDuration.toLong())
            showLoading = true
        }
    }

    Column(modifier = modifier) {
        if (chatEntry.shouldAnimate) {
            AnimatedVisibility(
                visible = showLoading,
                enter = fadeIn(animationSpec = tween(animationDuration)),
                exit = fadeOut(animationSpec = tween(animationDuration))
            ) {
                Loading()
            }

            AnimatedVisibility(
                visible = showAnswer,
                enter = fadeIn(animationSpec = tween(animationDuration)),
                exit = fadeOut(animationSpec = tween(animationDuration))
            ) {
                Answer(
                    answer = chatEntry.answer,
                    sources = chatEntry.sources,
                    onMarkdownLinkClicked = onMarkdownLinkClicked,
                    onSourcesExpanded = onSourcesExpanded
                )
            }
        } else {
            if (showLoading) Loading()
            if (showAnswer) Answer(
                answer = chatEntry.answer,
                sources = chatEntry.sources,
                onMarkdownLinkClicked = onMarkdownLinkClicked,
                onSourcesExpanded = onSourcesExpanded
            )
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
