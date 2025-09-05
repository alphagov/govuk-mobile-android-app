package uk.gov.govuk.chat.ui

import android.content.res.Configuration
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.ChatUiState
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.chat.ui.chat.ActionMenu
import uk.gov.govuk.chat.ui.chat.ChatErrorPageNoRetry
import uk.gov.govuk.chat.ui.chat.ChatErrorPageWithRetry
import uk.gov.govuk.chat.ui.chat.DisplayChatEntry
import uk.gov.govuk.chat.ui.chat.IntroMessages
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import kotlin.math.abs

internal class ChatScreenEvents(
    val onPageView: (String, String, String) -> Unit,
    val onActionItemClicked: (String, String, String) -> Unit,
    val onAboutClick: (String) -> Unit,
    val onQuestionSubmit: () -> Unit,
    val onMarkdownLinkClicked: (String, String) -> Unit,
)

internal class ChatScreenClickEvents(
    val onQuestionUpdated: (String) -> Unit,
    val onSubmit: (String) -> Unit,
    val onRetry: () -> Unit,
    val onClear: () -> Unit,
)

@Composable
internal fun ChatRoute(
    onShowOnboarding: () -> Unit,
    launchBrowser: (url: String) -> Unit,
    onClearDone: () -> Unit,
    onAuthError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ChatViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState.hasSeenOnboarding.let { seenOnboarding ->
        if (seenOnboarding == true) {
            ChatScreen(
                uiState = uiState,
                analyticsEvents = ChatScreenEvents(
                    onPageView = { klass, name, title ->
                        viewModel.onPageView(klass, name, title)
                    },
                    onActionItemClicked = { text, section, action -> viewModel.onActionItemClicked(text, section, action) },
                    onAboutClick = { text -> viewModel.onAboutClick(text) },
                    onQuestionSubmit = { viewModel.onQuestionSubmit() },
                    onMarkdownLinkClicked = { text, url -> viewModel.onMarkdownLinkClicked(text, url) },
                ),
                launchBrowser = launchBrowser,
                hasConversation = uiState.chatEntries.isNotEmpty(),
                clickEvents = ChatScreenClickEvents(
                    onQuestionUpdated = { question ->
                        viewModel.onQuestionUpdated(question)
                    },
                    onSubmit = { question ->
                        viewModel.onSubmit(question)
                    },
                    onRetry = {
                        viewModel.clearConversation()
                        onClearDone()
                    },
                    onClear = {
                        viewModel.clearConversation()
                        onClearDone()
                    }
                ),
                chatUrls = viewModel.chatUrls,
                modifier = modifier
            )
        } else if (seenOnboarding == false) {
            LaunchedEffect(Unit) {
                onShowOnboarding()
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
    uiState: ChatUiState,
    analyticsEvents: ChatScreenEvents,
    launchBrowser: (url: String) -> Unit,
    hasConversation: Boolean,
    clickEvents: ChatScreenClickEvents,
    chatUrls: ChatUrls,
    modifier: Modifier = Modifier
) {
    if (uiState.isRetryableError) {
        LaunchedEffect(Unit) {
            analyticsEvents.onPageView(
                Analytics.CHAT_ERROR_SCREEN_CLASS,
                Analytics.CHAT_ERROR_RETRY_SCREEN_NAME,
                Analytics.CHAT_ERROR_RETRY_SCREEN_TITLE,
            )
        }

        ChatErrorPageWithRetry(
            onRetry = clickEvents.onRetry,
            modifier = modifier
                .windowInsetsPadding(WindowInsets.statusBars)
        )
    } else if (uiState.isError) {
        LaunchedEffect(Unit) {
            analyticsEvents.onPageView(
                Analytics.CHAT_ERROR_SCREEN_CLASS,
                Analytics.CHAT_ERROR_SCREEN_NAME,
                Analytics.CHAT_ERROR_SCREEN_TITLE,
            )
        }

        ChatErrorPageNoRetry(
            modifier
                .windowInsetsPadding(WindowInsets.statusBars)
        )
    } else {
        LaunchedEffect(Unit) {
            analyticsEvents.onPageView(
                Analytics.CHAT_SCREEN_CLASS,
                Analytics.CHAT_SCREEN_NAME,
                Analytics.CHAT_SCREEN_TITLE
            )
        }

        ChatContent(
            uiState,
            launchBrowser = launchBrowser,
            hasConversation = hasConversation,
            clickEvents = clickEvents,
            analyticsEvents = analyticsEvents,
            chatUrls = chatUrls,
            modifier
        )
    }
}

@Composable
private fun ChatContent(
    uiState: ChatUiState,
    launchBrowser: (url: String) -> Unit,
    hasConversation: Boolean,
    clickEvents: ChatScreenClickEvents,
    analyticsEvents: ChatScreenEvents,
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

    LaunchedEffect(Unit) {
        delay(1000)
        backgroundVisible = true
    }

    Box(modifier.fillMaxSize()) {
        if (uiState.chatEntries.isEmpty()) {
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
                    IntroMessages(uiState.chatEntries.isEmpty()) // only animate if no conversation
                }

                items(chatEntries) {
                    DisplayChatEntry(
                        uiState.isLoading,
                        it.second,
                        launchBrowser = launchBrowser,
                        onMarkdownLinkClicked = analyticsEvents.onMarkdownLinkClicked,
                        onActionItemClicked = analyticsEvents.onActionItemClicked,
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
                        launchBrowser = launchBrowser,
                        hasConversation = hasConversation,
                        analyticsEvents = analyticsEvents,
                        clickEvents = clickEvents,
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
private fun ChatInput(
    uiState: ChatUiState,
    launchBrowser: (url: String) -> Unit,
    hasConversation: Boolean,
    analyticsEvents: ChatScreenEvents,
    clickEvents: ChatScreenClickEvents,
    chatUrls: ChatUrls,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(all = GovUkTheme.spacing.medium)
            .semantics { isTraversalGroup = true }
            .modifyIfPiiError(isFocused, uiState),
    ) {
        Row {
            AnimatedVisibility(!isFocused) {
                ActionMenu(
                    launchBrowser = launchBrowser,
                    hasConversation = hasConversation,
                    isLoading = uiState.isLoading,
                    onClear = clickEvents.onClear,
                    analyticsEvents = analyticsEvents,
                    chatUrls = chatUrls,
                    modifier = Modifier.semantics { this.traversalIndex = 1f }
                )
            }

            TextField(
                textStyle = TextStyle(
                    color = GovUkTheme.colourScheme.textAndIcons.primary,
                    fontSize = GovUkTheme.typography.bodyRegular.fontSize,
                    fontWeight = GovUkTheme.typography.bodyRegular.fontWeight,
                    fontFamily = GovUkTheme.typography.bodyRegular.fontFamily
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 0.dp)
                    .focusRequester(focusRequester)
                    .focusable(true)
                    .onFocusChanged {
                        isFocused = it.isFocused
                    }
                    .semantics { this.traversalIndex = 0f }
                    .modifyIfFocused(isFocused),
                value = if (isFocused) uiState.question else "",
                shape = if (isFocused)
                    RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
                else
                    RoundedCornerShape(40.dp),
                singleLine = false,
                onValueChange = {
                    clickEvents.onQuestionUpdated(it)
                },
                placeholder = {
                    PlaceholderText(isFocused = isFocused, uiState = uiState)
                },
                isError = uiState.isPiiError,
                colors = inputTextFieldDefaults()
            )
        }

        AnimatedVisibility(isFocused) {
            Row(
                modifier = Modifier
                    .background(GovUkTheme.colourScheme.surfaces.chatTextFieldBackground)
                    .border(
                        0.dp,
                        Color.Transparent,
                        RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp)
                    )
                    .fillMaxWidth()
                    .padding(
                        start = 0.dp,
                        end = GovUkTheme.spacing.small,
                        top = 0.dp,
                        bottom = GovUkTheme.spacing.small
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CharacterCountMessage(uiState)

                SubmitIconButton(
                    onClick = {
                        analyticsEvents.onQuestionSubmit()
                        clickEvents.onSubmit(uiState.question)
                    },
                    uiState = uiState
                )
            }
        }
    }
}

@Composable
private fun Modifier.modifyIfPiiError(isFocused: Boolean, uiState: ChatUiState): Modifier {
    return this.then(
        if (isFocused) {
            val color = if (uiState.isPiiError)
                GovUkTheme.colourScheme.strokes.textFieldError
            else
                GovUkTheme.colourScheme.strokes.chatTextFieldBorder

            Modifier
                .border(
                    1.dp,
                    color,
                    RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp))
        } else {
            Modifier.border(0.dp, Color.Transparent)
        }
    )
}

@Composable
private fun Modifier.modifyIfFocused(isFocused: Boolean): Modifier {
    return this.then(
        if (isFocused) {
            Modifier.padding(horizontal = 0.dp)
                .border(0.dp, Color.Transparent)
        } else {
            Modifier.padding(start = GovUkTheme.spacing.small)
                .border(
                    1.dp,
                    GovUkTheme.colourScheme.strokes.chatTextFieldBorderDisabled,
                    RoundedCornerShape(40.dp)
                )
        }
    )
}

@Composable
private fun PlaceholderText(
    isFocused: Boolean,
    uiState: ChatUiState,
    modifier: Modifier = Modifier
) {
    if (!isFocused && uiState.question.isEmpty()) {
        Text(
            text = stringResource(id = R.string.input_label),
            color = GovUkTheme.colourScheme.textAndIcons.secondary,
            modifier = modifier
        )
    } else {
        Text(
            text = uiState.question,
            color = GovUkTheme.colourScheme.textAndIcons.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
        )
    }
}

@Composable
private fun inputTextFieldDefaults() = TextFieldDefaults.colors(
    cursorColor = GovUkTheme.colourScheme.textAndIcons.primary,
    focusedTextColor = GovUkTheme.colourScheme.textAndIcons.primary,
    unfocusedTextColor = GovUkTheme.colourScheme.textAndIcons.secondary,
    disabledTextColor = GovUkTheme.colourScheme.textAndIcons.secondary,
    focusedContainerColor = GovUkTheme.colourScheme.surfaces.chatTextFieldBackground,
    unfocusedContainerColor = GovUkTheme.colourScheme.surfaces.chatTextFieldBackground,
    disabledContainerColor = GovUkTheme.colourScheme.surfaces.chatTextFieldBackground,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    errorContainerColor = GovUkTheme.colourScheme.surfaces.chatTextFieldBackground,
    errorLabelColor = GovUkTheme.colourScheme.textAndIcons.primary,
    errorCursorColor = GovUkTheme.colourScheme.textAndIcons.primary,
    errorIndicatorColor = Color.Transparent
)

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

@Composable
private fun SubmitIconButton(
    onClick: () -> Unit,
    uiState: ChatUiState,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = uiState.isSubmitEnabled && !uiState.isPiiError && !uiState.isLoading,
        colors = IconButtonColors(
            containerColor = GovUkTheme.colourScheme.surfaces.chatButtonBackgroundEnabled,
            contentColor = GovUkTheme.colourScheme.textAndIcons.chatButtonIconEnabled,
            disabledContainerColor = GovUkTheme.colourScheme.surfaces.chatButtonBackgroundDisabled,
            disabledContentColor = GovUkTheme.colourScheme.textAndIcons.chatButtonIconDisabled
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_arrow_upward_24),
            contentDescription = stringResource(id = R.string.button_alt),
        )
    }
}

@Composable
private fun CharacterCountMessage(
    uiState: ChatUiState,
    modifier: Modifier = Modifier
) {
    val charactersRemaining = abs(uiState.charactersRemaining)
    var color = GovUkTheme.colourScheme.textAndIcons.secondary
    var style = GovUkTheme.typography.subheadlineRegular
    var text = ""

    when {
        uiState.displayCharacterWarning -> {
            text = pluralStringResource(
                id = R.plurals.characterCountUnderOrAtLimit,
                count = charactersRemaining,
                charactersRemaining
            )
        }
        uiState.displayCharacterError -> {
            text = pluralStringResource(
                id = R.plurals.characterCountOverLimit,
                count = charactersRemaining,
                charactersRemaining
            )
            color = GovUkTheme.colourScheme.textAndIcons.textFieldError
            style = GovUkTheme.typography.subheadlineBold
        }
    }

    Text(
        text = text,
        color = color,
        style = style,
        modifier = modifier.padding(horizontal = GovUkTheme.spacing.medium)
    )
}

private fun analyticsEvents() = ChatScreenEvents(
    onPageView = { _, _, _ -> },
    onActionItemClicked = { _, _, _ -> },
    onAboutClick = { _ ->  },
    onQuestionSubmit = { },
    onMarkdownLinkClicked = { _, _ -> },
)

private fun clickEvents() = ChatScreenClickEvents(
    onQuestionUpdated = { _ -> },
    onSubmit = { _ -> },
    onRetry = { },
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
            uiState = ChatUiState(isLoading = false),
            analyticsEvents = analyticsEvents(),
            launchBrowser = { _ -> },
            hasConversation = false,
            chatUrls = ChatUrls("", "", "", ""),
            clickEvents = clickEvents()
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
            uiState = ChatUiState(isLoading = false),
            analyticsEvents = analyticsEvents(),
            launchBrowser = { _ -> },
            hasConversation = false,
            chatUrls = ChatUrls("", "", "", ""),
            clickEvents = clickEvents()
        )
    }
}
