package uk.gov.govuk.chat.ui

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.chat.ChatUiState
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.ui.model.ChatEntry
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
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
            viewModel.clearConversation()
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
    if (uiState.isRetryableError) {
        ChatErrorPageWithRetry(
            onRetry = onRetry,
            modifier = modifier
        )
    } else if (uiState.isError) {
        ChatErrorPageNoRetry(modifier)
    } else {
        ChatContent(
            uiState,
            onQuestionUpdated,
            onSubmit,
            onClear = onRetry,
            modifier
        )
    }
}

@Composable
private fun ChatContent(
    uiState: ChatUiState,
    onQuestionUpdated: (String) -> Unit,
    onSubmit: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val chatEntries = uiState.chatEntries.toList()

    Column(
        modifier.background(color = GovUkTheme.colourScheme.surfaces.chatBackground)
    ) {
        LazyColumn (
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            item {
                DisplayIntroMessages(uiState.chatEntries.isEmpty()) // only animate if no conversation
            }

            items(chatEntries) {
                DisplayChatEntry(uiState.isLoading, it.second)
            }
        }

        Column {
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
                            ActionMenu(
                                onClear = onClear,
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

    if (chatEntries.isNotEmpty()) {
        LaunchedEffect(chatEntries.last().second.answer) {
            listState.animateScrollToItem(chatEntries.size)
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
private fun DisplayChatEntry(isLoading: Boolean, chatEntry: ChatEntry) {
    Column {
        MediumVerticalSpacer()
        DisplayQuestion(question = chatEntry.question)

        MediumVerticalSpacer()
        if (isLoading && chatEntry.answer.isEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_circle_24),
                    contentDescription = null,
                    tint = GovUkTheme.colourScheme.textAndIcons.chatLoadingIcon,
                    modifier = Modifier
                        .padding(end = GovUkTheme.spacing.small),
                )

                LoadingText(
                    text = stringResource(id = R.string.loading_text),
                    modifier = Modifier
                )
            }
        } else {
            DisplayAnswer(
                answer = chatEntry.answer,
                sources = chatEntry.sources
            )
        }
    }
}

@Composable
fun LoadingText(
    text: String,
    modifier: Modifier = Modifier,
    animationSpec: DurationBasedAnimationSpec<Float> = tween(1000, 500, LinearEasing)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LoadingTextTransition")

    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animationSpec),
        label = "LoadingTextProgress"
    )

    val colorDark = GovUkTheme.colourScheme.textAndIcons.chatLoadingTextDark
    val colorLight = GovUkTheme.colourScheme.textAndIcons.chatLoadingTextLight

    val brush = remember(shimmerProgress, colorDark, colorLight) {
        LoadingTextShimmerBrush(
            shimmerProgress = shimmerProgress,
            colorDark = colorDark,
            colorLight = colorLight
        )
    }

    Text(
        text = text,
        modifier = modifier,
        style = GovUkTheme.typography.bodyRegular.copy(brush = brush)
    )
}

private class LoadingTextShimmerBrush(
    private val shimmerProgress: Float,
    private val colorDark: Color,
    private val colorLight: Color
): ShaderBrush() {

    override fun createShader(size: Size): Shader {
        val initialXOffset = -size.width
        val totalSweepDistance = size.width * 2
        val currentPosition = initialXOffset + totalSweepDistance * shimmerProgress

        return LinearGradientShader(
            colors = listOf(colorDark, colorLight),
            from = Offset(currentPosition, 0f),
            to = Offset(currentPosition + size.width, 0f)
        )
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
private fun ActionMenu(
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        ClearMenuItem(
            onClear = onClear,
            onClearActioned = { expanded = false }
        )
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
private fun ClearMenuItem(
    onClear: () -> Unit,
    onClearActioned: () -> Unit
) {
    val openDialog = rememberSaveable { mutableStateOf(false) }

    DropdownMenuItem(
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
        onClick = { openDialog.value = true }
    )

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            shape = RoundedCornerShape(10.dp),
            text = {
                BodyBoldLabel(
                    text = "Do you want to clear your chat history?",
                    color = GovUkTheme.colourScheme.textAndIcons.primary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClear()
                        openDialog.value = false
                        onClearActioned()
                    }
                ) {
                    BodyBoldLabel(
                        text = "Yes, clear chat",
                        color = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        onClearActioned()
                    }
                ) {
                    BodyRegularLabel(
                        text = "No, not now",
                        color = GovUkTheme.colourScheme.textAndIcons.link
                    )
                }
            },
            containerColor = GovUkTheme.colourScheme.surfaces.alert
        )
    }
}

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
