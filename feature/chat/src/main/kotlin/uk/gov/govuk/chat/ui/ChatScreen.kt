package uk.gov.govuk.chat.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.ChatUiState
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.ErrorPage
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
        onRetry = {
            viewModel.loadConversation()
        },
        modifier = modifier
    )
}

@Composable
private fun ChatScreen(
    uiState: ChatUiState,
    onQuestionUpdated: (String) -> Unit,
    onSubmit: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.isError) {
        ErrorPage(
            headerText = "Header",
            subText = "Subtext",
            buttonText = "Retry",
            onBack = { onRetry() },
            modifier = modifier,
            additionalText = "Additional text"
        )
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
            DisplayChatEntries(uiState = uiState)
        }

        Column {
            DisplayProgressIndicator(uiState = uiState)

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
                        .then(
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
                        ),
                ) {
                    Row {
                        AnimatedVisibility(!isFocused) {
                            ActionMenu()
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
                                .then(
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
                                ),
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
                                if (!isFocused) {
                                    DisplayPlaceholderText(uiState = uiState)
                                }
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
                                enabled = uiState.isSubmitEnabled
                            )
                        }
                    }
                }
            }

            DisplayPIIError(uiState = uiState)
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
private fun DisplayPlaceholderText(uiState: ChatUiState) {
    if (uiState.question.isEmpty()) {
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
        MediumVerticalSpacer()

        uiState.chatEntries.entries.forEach { chatEntry ->
            Column {
                BodyBoldLabel(
                    text = chatEntry.value.question
                )
                MediumVerticalSpacer()
                DisplayMarkdownText(text = chatEntry.value.answer)

                chatEntry.value.sources?.let { sources ->
                    if (sources.isNotEmpty()) {
                        BodyBoldLabel(
                            text = stringResource(id = R.string.sources_header)
                        )
                        MediumVerticalSpacer()

                        sources.forEach { source ->
                            DisplayMarkdownText(text = source)
                            MediumVerticalSpacer()
                        }
                    }
                }

                MediumVerticalSpacer()
            }
        }
    }
}

@Composable
private fun SubmitIconButton(onClick: () -> Unit, enabled: Boolean) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        colors = IconButtonColors(
            containerColor = GovUkTheme.colourScheme.surfaces.chatButtonBackgroundEnabled,
            contentColor = GovUkTheme.colourScheme.textAndIcons.chatButtonIconEnabled,
            disabledContainerColor = GovUkTheme.colourScheme.surfaces.chatButtonBackgroundDisabled,
            disabledContentColor = GovUkTheme.colourScheme.textAndIcons.chatButtonIconDisabled
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_arrow_upward_24),
            contentDescription = null
        )
    }
}

@Composable
private fun ActionMenu() {
    var expanded by rememberSaveable { mutableStateOf(false) }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
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
    onClick = { /* TODO: Handle action */ },
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
    onClick = { /* TODO: Handle action */ }
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
            contentDescription = null,
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
private fun DisplayMarkdownText(text: String) {
    MarkdownText(
        markdown = text,
        linkColor = GovUkTheme.colourScheme.textAndIcons.link,
        style = markdownTextStyle(),
        enableSoftBreakAddsNewLine = false
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
            onSubmit = { _ -> },
            onRetry = { }
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
            onSubmit = { _ -> },
            onRetry = { }
        )
    }
}
