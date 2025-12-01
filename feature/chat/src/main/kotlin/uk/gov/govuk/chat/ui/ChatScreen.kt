package uk.gov.govuk.chat.ui

import android.content.res.Configuration
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.ChatUiState
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.chat.ui.component.ChatEntry
import uk.gov.govuk.chat.ui.component.ChatInput
import uk.gov.govuk.chat.ui.component.IntroMessages
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme

internal class AnalyticsEvents(
    val onPageView: (String, String, String) -> Unit,
    val onNavigationActionItemClicked: (String, String) -> Unit,
    val onFunctionActionItemClicked: (String, String, String) -> Unit,
    val onQuestionSubmit: () -> Unit,
    val onMarkdownLinkClicked: (String, String) -> Unit,
    val onSourcesExpanded: () -> Unit
)

internal class UiEvents(
    val onQuestionUpdated: (String) -> Unit,
    val onSubmit: (String) -> Unit,
    val onClear: () -> Unit,
)

@Composable
internal fun ChatRoute(
    onShowOnboarding: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    onAuthError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ChatViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val onPageView: (String, String, String) -> Unit = { klass, name, title ->
        viewModel.onPageView(
            screenClass = klass,
            screenName = name,
            title = title
        )
    }

    uiState?.let {
        when (it) {
            ChatUiState.Onboarding -> onShowOnboarding()
            is ChatUiState.Error -> {
                ErrorScreen(
                    canRetry = it.canRetry,
                    onPageView = onPageView,
                    onRetry = { viewModel.clearConversation() },
                    modifier = modifier
                )
            }
            is ChatUiState.Default -> {
                ChatScreen(
                    uiState = it,
                    analyticsEvents = AnalyticsEvents(
                        onPageView = onPageView,
                        onNavigationActionItemClicked = { text, url ->
                            viewModel.onNavigationActionItemClicked(text, url)
                        },
                        onFunctionActionItemClicked = { text, section, action ->
                            viewModel.onFunctionActionItemClicked(text, section, action)
                        },
                        onQuestionSubmit = { viewModel.onQuestionSubmit() },
                        onMarkdownLinkClicked = { text, url -> viewModel.onMarkdownLinkClicked(text, url) },
                        onSourcesExpanded = { viewModel.onSourcesExpanded() }
                    ),
                    launchBrowser = launchBrowser,
                    hasConversation = it.chatEntries.isNotEmpty(),
                    uiEvents = UiEvents(
                        onQuestionUpdated = { question ->
                            viewModel.onQuestionUpdated(question)
                        },
                        onSubmit = { question ->
                            viewModel.onSubmit(question)
                        },
                        onClear = {
                            viewModel.clearConversation()
                        }
                    ),
                    chatUrls = viewModel.chatUrls,
                    modifier = modifier
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.authError.collect {
            onAuthError()
        }
    }
}

@Composable
private fun ChatScreen(
    uiState: ChatUiState.Default,
    launchBrowser: (url: String) -> Unit,
    hasConversation: Boolean,
    uiEvents: UiEvents,
    analyticsEvents: AnalyticsEvents,
    chatUrls: ChatUrls,
    modifier: Modifier = Modifier
) {
    var heightPx by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()
    val chatEntries = uiState.chatEntries.toList()
    var backgroundVisible  by remember { mutableStateOf(false) }
    val brush = Brush.verticalGradient(
        colorStops = calculateStops(heightPx, 20.dp)
    )
    val animationDuration = 500

    LaunchedEffect(Unit) {
        analyticsEvents.onPageView(
            Analytics.CHAT_SCREEN_CLASS,
            Analytics.CHAT_SCREEN_NAME,
            Analytics.CHAT_SCREEN_TITLE
        )
    }

    LaunchedEffect(Unit) {
        delay(1000)
        backgroundVisible = true
    }

    Box(modifier.fillMaxSize()) {
        if (chatEntries.isEmpty()) {
            AnimatedVisibility(
                visible = backgroundVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 2000))
            ) {
                BackgroundGradient()
            }
        } else {
            BackgroundGradient()
        }

        Column(
            Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .onSizeChanged { heightPx = it.height }
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = brush,
                            blendMode = BlendMode.DstIn
                        )
                    }
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = GovUkTheme.spacing.medium)
            ) {
                item {
                    IntroMessages(chatEntries.isEmpty()) // only animate if no conversation
                }

                items(chatEntries) {
                    ChatEntry(
                        chatEntry = it.second,
                        onMarkdownLinkClicked = { text, url ->
                            launchBrowser(url)
                            analyticsEvents.onMarkdownLinkClicked(text, url)
                        },
                        animationDuration = animationDuration,
                        onSourcesExpanded = analyticsEvents.onSourcesExpanded,
                        listState = listState
                    )
                }

                item {
                    Spacer(Modifier.height(20.dp))
                }
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChatInput(
                        uiState,
                        hasConversation = hasConversation,
                        onNavigationActionItemClicked = { text, url ->
                            launchBrowser(url)
                            analyticsEvents.onNavigationActionItemClicked(text, url)
                        },
                        onFunctionActionItemClicked = { text, section, action ->
                            analyticsEvents.onFunctionActionItemClicked(text, section, action)
                        },
                        onClear = uiEvents.onClear,
                        onQuestionUpdated = uiEvents.onQuestionUpdated,
                        onSubmit = { question ->
                            uiEvents.onSubmit(question)
                            analyticsEvents.onQuestionSubmit()
                        },
                        chatUrls = chatUrls
                    )
                }

                if (uiState.isPiiError) {
                    PiiErrorMessage()
                }

                SmallVerticalSpacer()
            }
        }
    }

    if (chatEntries.isNotEmpty()) {
        LaunchedEffect(chatEntries.last().second.answer) {
            delay(animationDuration.toLong())
            listState.animateScrollToItem(chatEntries.size)
        }
    }
}

@Composable
private fun calculateStops(
    heightPx: Int,
    fadeDp: Dp,
): Array<Pair<Float, Color>> {
    val density = LocalDensity.current
    val topFadePx = with(density) { fadeDp.toPx() }
    val bottomFadePx = with(density) { fadeDp.toPx() }

    if (heightPx == 0) {
        // Before layout pass, fallback to full opacity to avoid division by zero
        return arrayOf(0f to Color.White, 1f to Color.White)
    }

    val topEnd = (topFadePx / heightPx).coerceIn(0f, 1f)
    val bottomStart = ((heightPx - bottomFadePx) / heightPx).coerceIn(0f, 1f)

    return arrayOf(
        0f to Color.Transparent,
        topEnd to Color.White,
        bottomStart to Color.White,
        1f to Color.Transparent
    )
}

@Composable
private fun BackgroundGradient(
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            FrameLayout(context).apply {
                setBackgroundResource(R.drawable.background_chat)
            }
        },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
private fun PiiErrorMessage(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GovUkTheme.spacing.medium)
    ) {
        val errorMessage = stringResource(id = R.string.pii_error_message)
        BodyBoldLabel(
            color = GovUkTheme.colourScheme.textAndIcons.textFieldError,
            text = errorMessage
        )
    }
}

private fun analyticsEvents() = AnalyticsEvents(
    onPageView = { _, _, _ -> },
    onFunctionActionItemClicked = { _, _, _ -> },
    onNavigationActionItemClicked = { _, _ ->  },
    onQuestionSubmit = { },
    onMarkdownLinkClicked = { _, _ -> },
    onSourcesExpanded = { }
)

private fun clickEvents() = UiEvents(
    onQuestionUpdated = { _ -> },
    onSubmit = { _ -> },
    onClear = { }
)

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun LightModeChatScreenPreview() {
    GovUkTheme {
        ChatScreen(
            uiState = ChatUiState.Default(isLoading = false),
            analyticsEvents = analyticsEvents(),
            launchBrowser = { _ -> },
            hasConversation = false,
            chatUrls = ChatUrls("", "", "", ""),
            uiEvents = clickEvents()
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DarkModeChatScreenPreview() {
    GovUkTheme {
        ChatScreen(
            uiState = ChatUiState.Default(isLoading = false),
            analyticsEvents = analyticsEvents(),
            launchBrowser = { _ -> },
            hasConversation = false,
            chatUrls = ChatUrls("", "", "", ""),
            uiEvents = clickEvents()
        )
    }
}
