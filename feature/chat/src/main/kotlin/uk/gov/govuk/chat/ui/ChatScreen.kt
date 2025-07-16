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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.delay
import uk.gov.govuk.chat.ChatUiState
import uk.gov.govuk.chat.ChatViewModel
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.domain.StringCleaner
import uk.gov.govuk.design.ui.component.BodyBoldLabel
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
        onSubmit = { question ->
            viewModel.onSubmit(question)
        },
        modifier = modifier
    )
}

@Composable
private fun ChatScreen(
    uiState: ChatUiState?,
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var question by rememberSaveable { mutableStateOf("") }
    var scrollPosition by remember { mutableIntStateOf(0) }
    var isError by rememberSaveable { mutableStateOf(false) }
    var characterCount by rememberSaveable { mutableIntStateOf(0) }
    var buttonDisabled by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var isFocused by rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState?.chatEntries?.size) {
        val answerCount = uiState?.chatEntries?.size ?: 0
        if (answerCount > 0) {
            delay(150)
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Column(
        Modifier.background(color = GovUkTheme.colourScheme.surfaces.chatBackground)
    ) {
        Column(
            Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = GovUkTheme.spacing.medium)
        ) {
            if (uiState?.chatEntries != null && uiState.chatEntries.isNotEmpty()) {
                MediumVerticalSpacer()

                uiState.chatEntries.entries.forEachIndexed { index, chatEntry ->
                    val isLastQuestion = index == uiState.chatEntries.size - 1
                    Column(
                        modifier = if (isLastQuestion) {
                            modifier.onGloballyPositioned { layoutCoordinates ->
                                val positionInScrollableContent =
                                    layoutCoordinates.positionInParent().y
                                scrollPosition = positionInScrollableContent.toInt()
                            }
                        } else {
                            modifier
                        }
                    ) {
                        BodyBoldLabel(
                            text = chatEntry.value.question
                        )
                        MediumVerticalSpacer()

                        MarkdownText(
                            markdown = chatEntry.value.answer,
                            linkColor = GovUkTheme.colourScheme.textAndIcons.link,
                            style = GovUkTheme.typography.bodyRegular,
                            enableSoftBreakAddsNewLine = false
                        )

                        chatEntry.value.sources?.let { sources ->
                            if (sources.isNotEmpty()) {
                                BodyBoldLabel(
                                    text = stringResource(id = R.string.sources_header)
                                )
                                MediumVerticalSpacer()

                                sources.forEach { source ->
                                    MarkdownText(
                                        markdown = source,
                                        linkColor = GovUkTheme.colourScheme.textAndIcons.link,
                                        style = GovUkTheme.typography.bodyRegular,
                                        enableSoftBreakAddsNewLine = false
                                    )
                                    MediumVerticalSpacer()
                                }
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
        ) {
            if (uiState != null) {
                if (uiState.loading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = GovUkTheme.spacing.medium),
                        color = GovUkTheme.colourScheme.surfaces.primary,
                        trackColor = GovUkTheme.colourScheme.surfaces.textFieldBackground
                    )
                }
            }

            Row(
                modifier = if (isError)
                {
                    Modifier
                        .fillMaxWidth()
                        .background(GovUkTheme.colourScheme.surfaces.chatBackground)
                } else {
                    modifier
                        .fillMaxWidth()
                        .background(GovUkTheme.colourScheme.surfaces.chatBackground)
                },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .background(GovUkTheme.colourScheme.surfaces.chatBackground)
                        .padding(all = GovUkTheme.spacing.medium)
                        .then(
                            if (isFocused)
                                if (isError)
                                    Modifier
                                        .border(
                                            1.dp,
                                            GovUkTheme.colourScheme.strokes.textFieldError,
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clip(RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp))
                                else
                                    Modifier
                                        .border(
                                            1.dp,
                                            GovUkTheme.colourScheme.strokes.chatTextFieldBorder,
                                            RoundedCornerShape(20.dp)
                                        )
                                        .clip(RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp))
                            else
                                Modifier.border(0.dp, Color.Transparent)
                        ),
                ) {
                    Row {
                        AnimatedVisibility(!isFocused) {
                            var expanded by remember { mutableStateOf(false) }

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
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "About",
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
                                    onClick = {},
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Clear chat",
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
                                    onClick = {}
                                )
                            }

                            IconButton(
                                onClick = { expanded = !expanded },
                                enabled = true,
                                colors = IconButtonColors(
                                    containerColor = GovUkTheme.colourScheme.surfaces.chatTextFieldBackground,
                                    contentColor = GovUkTheme.colourScheme.surfaces.chatButtonBackgroundEnabled,
                                    disabledContainerColor = GovUkTheme.colourScheme.surfaces.chatTextFieldBackground,
                                    disabledContentColor = GovUkTheme.colourScheme.surfaces.chatButtonBackgroundEnabled
                                ),
                                modifier = Modifier
                                    .border(
                                        1.dp,
                                        GovUkTheme.colourScheme.strokes.chatTextFieldBorderDisabled,
                                        RoundedCornerShape(30.dp)
                                    )
                                    .clip(RoundedCornerShape(30.dp))
                                    .height(50.dp)
                                    .width(50.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.outline_more_vert_24),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(all = GovUkTheme.spacing.small)
                                        .clip(RoundedCornerShape(30.dp))
                                        .height(50.dp)
                                        .width(50.dp)
                                )
                            }
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
                                    if (isFocused)
                                        Modifier.padding(horizontal = 0.dp)
                                            .border(0.dp, Color.Transparent)
                                    else
                                        Modifier.padding(start = GovUkTheme.spacing.small)
                                            .border(
                                                1.dp,
                                                GovUkTheme.colourScheme.strokes.chatTextFieldBorderDisabled,
                                                RoundedCornerShape(40.dp)
                                            )
                                ),
                            value = if (isFocused) question else "",
                            shape = if (isFocused)
                                RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
                            else
                                RoundedCornerShape(40.dp),
                            singleLine = false,
                            onValueChange = {
                                buttonDisabled = false
                                isError = false
                                question = it
                                characterCount = it.length
                            },
                            placeholder = {
                                if (!isFocused) {
                                    Text(text = "Type your message here")
                                }
                            },
                            isError = isError,
                            colors = TextFieldDefaults.colors(
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
                            buttonDisabled = characterCountText(
                                characterCount = characterCount,
                                buttonDisabled = buttonDisabled,
                                modifier = Modifier.weight(1f)
                                    .padding(all = 0.dp)
                            )

                            IconButton(
                                onClick = {
                                    val currentQuestion = question
                                    isError = StringCleaner.includesPII(currentQuestion)
                                    if (!isError) {
                                        onSubmit(currentQuestion)
                                        question = ""
                                        characterCount = 0
                                    }
                                },
                                enabled = question.isNotBlank() && !buttonDisabled,
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
                    }
                }
            }

            if (isError) {
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

            SmallVerticalSpacer()
        }
    }
}

@Composable
private fun characterCountText(
    characterCount: Int,
    buttonDisabled: Boolean,
    modifier: Modifier = Modifier
): Boolean {
    val characterLimit = 30
    val characterNotify = 25

    var color = GovUkTheme.colourScheme.textAndIcons.secondary
    var style = GovUkTheme.typography.subheadlineRegular
    var disabled = buttonDisabled
    var text = ""

    if (characterCount in characterNotify..<characterLimit) {
        val characterDifference = (characterLimit - characterCount)
        text = pluralStringResource(
            id = R.plurals.characterCountUnderOrAtLimit,
            count = characterDifference,
            characterDifference
        )
        disabled = false
    } else if (characterCount == characterLimit) {
        val characterDifference = 0
        text = pluralStringResource(
            id = R.plurals.characterCountUnderOrAtLimit,
            count = characterDifference,
            characterDifference
        )
        disabled = false
    } else if (characterCount > characterLimit) {
        val characterDifference = abs(characterLimit - characterCount)
        text = pluralStringResource(
            id = R.plurals.characterCountOverLimit,
            count = characterDifference,
            characterDifference
        )
        color = GovUkTheme.colourScheme.textAndIcons.textFieldError
        style = GovUkTheme.typography.subheadlineBold
        disabled = true
    }

    Text(
        text = text,
        color = color,
        style = style,
        modifier = Modifier.padding(horizontal = GovUkTheme.spacing.medium)
    )
    return disabled
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun LightModeChatScreenPreview() {
    GovUkTheme {
        ChatScreen(
            uiState = ChatUiState(loading = false),
            onSubmit = { _ -> },
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
            uiState = ChatUiState(
                loading = false
            ),
            onSubmit = { _ -> },
        )
    }
}
