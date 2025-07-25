package uk.gov.govuk.chat.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.ChatUiState
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.CentreAlignedScreen
import uk.gov.govuk.design.ui.component.LargeHorizontalSpacer
import uk.gov.govuk.design.ui.component.LargeTitleBoldLabel
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.theme.GovUkTheme
import kotlin.math.abs

@Composable
internal fun ChatRoute(
    modifier: Modifier = Modifier,
) {
    val viewModel: ChatViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    ChatScreen(
        uiState = uiState,
        onQuestionUpdated = { question ->
            viewModel.onQuestionUpdated(question)
        },
        onSubmit = { question ->
            viewModel.onSubmit(question)
        },
        modifier = modifier
    )
}

@Composable
private fun ChatScreen(
    uiState: ChatUiState,
    onQuestionUpdated: (String) -> Unit,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isError) {
        ChatErrorPage(modifier = modifier)
    } else {
        ChatContent(
            uiState,
            onQuestionUpdated,
            onSubmit,
            modifier
        )
    }
}

@Composable
private fun ChatErrorPage(
    modifier: Modifier = Modifier,
) {
    // Could should be a component like ErrorPage, but currently the need for links in text is only within Chat
    CentreAlignedScreen(
        modifier = modifier,
        screenContent = {
            Icon(
                painter = painterResource(id = uk.gov.govuk.design.R.drawable.ic_error),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.primary,
                modifier = Modifier.height(IntrinsicSize.Min)
                    .padding(all = GovUkTheme.spacing.medium)
            )

            LargeHorizontalSpacer()

            LargeTitleBoldLabel(
                text = stringResource(id = R.string.error_page_header),
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            BodyRegularLabel(
                text = stringResource(id = R.string.error_page_subtext),
                textAlign = TextAlign.Center
            )

            MediumVerticalSpacer()

            AdditionalText()
        },
        footerContent = {}
    )
}

@Composable
private fun AdditionalText(
    modifier: Modifier = Modifier
) {
    val intro = stringResource(id = R.string.error_page_additional_text_intro)
    val linkText = stringResource(id = R.string.error_page_additional_text_link_text)
    val outro = stringResource(id = R.string.error_page_additional_text_outro)
    val url = stringResource(id = R.string.error_page_additional_text_url)

    val uriHandler = LocalUriHandler.current

    val annotatedString = buildAnnotatedString {
        append(intro)
        append(" ")
        pushStringAnnotation(tag = "URL", annotation = url)
        withStyle(
            style = SpanStyle(
                color = GovUkTheme.colourScheme.textAndIcons.link
            )
        ) {
            append(linkText)
            append(" ")
        }
        pop()
        append(outro)
    }

    Text(
        text = annotatedString,
        style = GovUkTheme.typography.bodyRegular.copy(
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            textAlign = TextAlign.Center
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                annotatedString
                    .getStringAnnotations(tag = "URL", start = 0, end = annotatedString.length)
                    .firstOrNull()
                    ?.let { annotation ->
                        uriHandler.openUri(annotation.item)
                    }
            }
    )
}

@Composable
private fun ChatContent(
    uiState: ChatUiState,
    onQuestionUpdated: (String) -> Unit,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier.background(color = GovUkTheme.colourScheme.surfaces.chatBackground)
    ) {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            DisplayIntroMessages(uiState.chatEntries.isEmpty()) // only animate if no conversation
            DisplayChatEntries(uiState = uiState)
        }

        Column {
            DisplayProgressIndicator(uiState)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GovUkTheme.colourScheme.surfaces.chatBackground),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .background(GovUkTheme.colourScheme.surfaces.chatBackground)
                        .padding(all = GovUkTheme.spacing.medium)
                        .semantics { isTraversalGroup = true }
                        .modifyIfPiiError(isFocused, uiState),
                ) {
                    Row {
                        AnimatedVisibility(!isFocused) {
                            ActionMenu(modifier = Modifier.semantics { this.traversalIndex = 1f })
                        }

                        TextField(
                            textStyle = TextStyle(
                                color = GovUkTheme.colourScheme.textAndIcons.primary
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
                                onQuestionUpdated(it)
                            },
                            placeholder = {
                                DisplayPlaceholderText(isFocused = isFocused, uiState = uiState)
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
                            CharacterCountText(uiState)

                            SubmitIconButton(
                                onClick = { onSubmit(uiState.question) },
                                uiState = uiState
                            )
                        }
                    }
                }
            }

            DisplayPIIError(uiState)

            SmallVerticalSpacer()
        }
    }

    LaunchedEffect(uiState.chatEntries.size) {
        val answerCount = uiState.chatEntries.size
        if (answerCount > 0) {
            delay(150)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
}

@Composable
private fun Modifier.modifyIfPiiError(isFocused: Boolean, uiState: ChatUiState): Modifier {
    return this.then(
        if (isFocused) {
            var color = if (uiState.isPiiError)
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
private fun DisplayIntroMessages(animated: Boolean) {
    if (animated) {
        var message1Visible by remember { mutableStateOf(false) }
        var message2Visible by remember { mutableStateOf(false) }
        var message3Visible by remember { mutableStateOf(false) }

        val delay = 1000L
        val duration = 500

        LaunchedEffect(key1 = true) {
            delay(delay)

            message1Visible = true
            delay(delay)

            message2Visible = true
            delay(delay)

            message3Visible = true
        }

        MessageHeader()
        MediumVerticalSpacer()

        AnimatedVisibility(
            visible = message1Visible,
            enter = fadeIn(animationSpec = tween(durationMillis = duration)) +
                scaleIn(animationSpec = tween(durationMillis = duration))
        ) {
            Message1()
        }

        if (message1Visible) {
            MediumVerticalSpacer()
        }

        AnimatedVisibility(
            visible = message2Visible,
            enter = fadeIn(animationSpec = tween(durationMillis = duration)) +
                scaleIn(animationSpec = tween(durationMillis = duration))
        ) {
            Message2()
        }

        if (message2Visible) {
            MediumVerticalSpacer()
        }

        AnimatedVisibility(
            visible = message3Visible,
            enter = fadeIn(animationSpec = tween(durationMillis = duration)) +
                scaleIn(animationSpec = tween(durationMillis = duration))
        ) {
            Message3()
        }
    } else {
        MessageHeader()
        MediumVerticalSpacer()
        Message1()
        MediumVerticalSpacer()
        Message2()
        MediumVerticalSpacer()
        Message3()
    }
}

@Composable
private fun MessageHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = GovUkTheme.spacing.medium,
                end = GovUkTheme.spacing.medium,
                bottom = 0.dp,
                top = 48.dp
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.bot_message_availability_text),
            color = GovUkTheme.colourScheme.textAndIcons.chatBotHeaderText,
            style = GovUkTheme.typography.bodyRegular
        )
    }
}

@Composable
private fun Message1() {
    DisplayAnswer(
        answer = stringResource(id = R.string.bot_message_1),
        sources = emptyList(),
        modifier = Modifier.padding(bottom = GovUkTheme.spacing.medium)
    )
}

@Composable
private fun Message2() {
    DisplayAnswer(
        showHeader = false,
        answer = stringResource(id = R.string.bot_message_2),
        sources = emptyList(),
        modifier = Modifier.padding(vertical = GovUkTheme.spacing.medium)
    )
}

@Composable
private fun Message3() {
    DisplayAnswer(
        showHeader = false,
        answer = stringResource(id = R.string.bot_message_3),
        sources = emptyList(),
        modifier = Modifier.padding(vertical = GovUkTheme.spacing.medium)
    )
}

@Composable
private fun DisplayPlaceholderText(isFocused: Boolean, uiState: ChatUiState) {
    if (!isFocused && uiState.question.isEmpty()) {
        Text(
            text = stringResource(id = R.string.input_label),
            color = GovUkTheme.colourScheme.textAndIcons.secondary
        )
    } else {
        Text(
            text = uiState.question,
            color = GovUkTheme.colourScheme.textAndIcons.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
private fun DisplayPIIError(uiState: ChatUiState) {
    if (uiState.isPiiError) {
        Row(
            modifier = Modifier
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
}

@Composable
private fun DisplayProgressIndicator(uiState: ChatUiState) {
    if (uiState.isLoading) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = GovUkTheme.spacing.medium),
            color = GovUkTheme.colourScheme.surfaces.primary,
            trackColor = GovUkTheme.colourScheme.surfaces.textFieldBackground
        )
    }
}

@Composable
private fun DisplayChatEntries(uiState: ChatUiState) {
    if (uiState.chatEntries.isNotEmpty()) {
        uiState.chatEntries.entries.forEach { chatEntry ->
            Column {
                MediumVerticalSpacer()
                DisplayQuestion(question = chatEntry.value.question)
                MediumVerticalSpacer()
                DisplayAnswer(
                    answer = chatEntry.value.answer,
                    sources = chatEntry.value.sources
                )
            }
        }
    }
}

@Composable
private fun DisplayQuestion(question: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = GovUkTheme.colourScheme.surfaces.chatUserMessageBackground,
                contentColor = GovUkTheme.colourScheme.textAndIcons.chatUserMessageText
            ),
            border = BorderStroke(1.dp, GovUkTheme.colourScheme.strokes.chatUserMessageBorder)
        ) {
            BodyRegularLabel(
                text = question,
                modifier = Modifier.padding(GovUkTheme.spacing.medium)
            )
        }
    }
}

@Composable
private fun DisplayAnswer(
    answer: String,
    modifier: Modifier = Modifier,
    showHeader: Boolean = true,
    sources: List<String>?
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = GovUkTheme.colourScheme.surfaces.chatBotMessageBackground,
            contentColor = GovUkTheme.colourScheme.textAndIcons.chatBotMessageText
        ),
        border = BorderStroke(1.dp, GovUkTheme.colourScheme.strokes.chatBotMessageBorder),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (showHeader) {
            BodyBoldLabel(
                text = stringResource(id = R.string.bot_header_text),
                modifier = Modifier.padding(GovUkTheme.spacing.medium)
            )
        }

        DisplayMarkdownText(
            text = answer,
            talkbackText = answer,
            modifier = modifier
        )

        if (!sources.isNullOrEmpty()) {
            DisplaySources(sources = sources)
        }
    }
}

@Composable
private fun ChatDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        thickness = 1.dp,
        color = GovUkTheme.colourScheme.strokes.chatDivider,
        modifier = modifier
    )
}

@Composable
private fun DisplaySources(sources: List<String>) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val degrees by animateFloatAsState(if (expanded) 0f else -180f)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ChatDivider()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(R.drawable.baseline_info_24),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.icon,
                modifier = Modifier
                    .padding(end = GovUkTheme.spacing.small),
            )

            BodyBoldLabel(
                text = stringResource(id = R.string.bot_sources_header_text)
            )
        }

        Row(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .fillMaxWidth()
                .padding(GovUkTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BodyRegularLabel(
                text = stringResource(id = R.string.bot_sources_list_description)
            )

            Image(
                painter = painterResource(R.drawable.outline_arrow_drop_up_24),
                contentDescription = null,
                modifier = Modifier.rotate(degrees),
                colorFilter = ColorFilter.tint(GovUkTheme.colourScheme.textAndIcons.icon)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                sources.forEachIndexed { index, _ ->
                    val linkAddendumText = stringResource(id = R.string.sources_open_in_text)
                    val linkText = "${sources[index]} $linkAddendumText"

                    MediumVerticalSpacer()

                    DisplayMarkdownText(
                        text = sources[index],
                        talkbackText = linkText
                    )

                    if (index < sources.size - 1) {
                        MediumVerticalSpacer()
                        ChatDivider(
                            modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
                        )
                    }
                }

                MediumVerticalSpacer()
            }
        }
    }
}

@Composable
private fun SubmitIconButton(onClick: () -> Unit, uiState: ChatUiState) {
    IconButton(
        onClick = onClick,
        enabled = uiState.isSubmitEnabled && !uiState.isPiiError,
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
private fun ActionMenu(modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = modifier
            .background(GovUkTheme.colourScheme.surfaces.alert)
            .border(
                1.dp,
                GovUkTheme.colourScheme.surfaces.alert,
                RoundedCornerShape(GovUkTheme.spacing.extraSmall)
            )
            .width(200.dp)
    ) {
        AboutMenuItem()
        ClearMenuItem()
    }

    ActionIconButton(
        onClick = { expanded = !expanded }
    )
}

@Composable
private fun AboutMenuItem() = DropdownMenuItem(
    text = {
        Text(
            text = stringResource(id = R.string.action_about),
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            style = GovUkTheme.typography.bodyRegular,
        )
    },
    trailingIcon = {
        Icon(
            painter = painterResource(R.drawable.outline_info_24),
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.primary
        )
    },
    onClick = { },
)

@Composable
private fun ClearMenuItem() = DropdownMenuItem(
    text = {
        Text(
            text = stringResource(id = R.string.action_clear),
            color = GovUkTheme.colourScheme.textAndIcons.buttonDestructive,
            style = GovUkTheme.typography.bodyRegular,
        )
    },
    trailingIcon = {
        Icon(
            painter = painterResource(R.drawable.outline_delete_24),
            contentDescription = null,
            tint = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
        )
    },
    onClick = { }
)

@Composable
private fun ActionIconButton(onClick: () -> Unit) {
    val modifier = Modifier
        .clip(RoundedCornerShape(30.dp))
        .height(50.dp)
        .width(50.dp)

    IconButton(
        onClick = onClick,
        enabled = true,
        colors = IconButtonColors(
            containerColor = GovUkTheme.colourScheme.surfaces.chatTextFieldBackground,
            contentColor = GovUkTheme.colourScheme.surfaces.chatButtonBackgroundEnabled,
            disabledContainerColor = GovUkTheme.colourScheme.surfaces.chatTextFieldBackground,
            disabledContentColor = GovUkTheme.colourScheme.surfaces.chatButtonBackgroundEnabled
        ),
        modifier = modifier
            .border(
                1.dp,
                GovUkTheme.colourScheme.strokes.chatTextFieldBorderDisabled,
                RoundedCornerShape(30.dp)
            )
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_more_vert_24),
            contentDescription = stringResource(id = R.string.action_alt),
            modifier = modifier.padding(all = GovUkTheme.spacing.small)
        )
    }
}

@Composable
private fun markdownTextStyle() = TextStyle(
    color = GovUkTheme.colourScheme.textAndIcons.primary,
    fontSize = GovUkTheme.typography.bodyRegular.fontSize,
    fontFamily = GovUkTheme.typography.bodyRegular.fontFamily,
    fontWeight = GovUkTheme.typography.bodyRegular.fontWeight
)

@Composable
private fun DisplayMarkdownText(
    text: String,
    talkbackText: String,
    modifier: Modifier = Modifier
) {
    MarkdownText(
        markdown = text,
        linkColor = GovUkTheme.colourScheme.textAndIcons.link,
        style = markdownTextStyle(),
        enableSoftBreakAddsNewLine = false,
        enableUnderlineForLink = false,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = GovUkTheme.spacing.medium)
            .semantics {
                contentDescription = talkbackText
            }
    )
}

@Composable
private fun CharacterCountText(
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun LightModeChatScreenPreview() {
    GovUkTheme {
        ChatScreen(
            uiState = ChatUiState(isLoading = false),
            onQuestionUpdated = { _ -> },
            onSubmit = { _ -> }
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
            onQuestionUpdated = { _ -> },
            onSubmit = { _ -> }
        )
    }
}
