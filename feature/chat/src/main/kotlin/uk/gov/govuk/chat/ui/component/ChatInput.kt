package uk.gov.govuk.chat.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import uk.gov.govuk.chat.ChatUiState
import uk.gov.govuk.chat.R
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.design.ui.theme.GovUkTheme
import kotlin.math.abs

@Composable
internal fun ChatInput(
    uiState: ChatUiState.Default,
    hasConversation: Boolean,
    onNavigationActionItemClicked: (String, String) -> Unit,
    onFunctionActionItemClicked: (String, String, String) -> Unit,
    onClear: () -> Unit,
    onQuestionUpdated: (String) -> Unit,
    onSubmit: (String) -> Unit,
    chatUrls: ChatUrls,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(all = GovUkTheme.spacing.medium)
            .semantics { isTraversalGroup = true }
            .modifyIfPiiError(
                isFocused = isFocused,
                isPiiError = uiState.isPiiError
            )
    ) {
        Row {
            AnimatedVisibility(!isFocused) {
                ActionMenu(
                    hasConversation = hasConversation,
                    isLoading = uiState.isLoading,
                    onClear = onClear,
                    onNavigationItemClicked = { text, url ->
                        onNavigationActionItemClicked(text, url)
                    },
                    onFunctionItemClicked = { text, section, action ->
                        onFunctionActionItemClicked(text, section, action)
                    },
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
                onValueChange = onQuestionUpdated,
                placeholder = {
                    PlaceholderText(
                        isFocused = isFocused,
                        question = uiState.question
                    )
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
                CharacterCountMessage(
                    charactersRemaining = uiState.charactersRemaining,
                    displayCharacterWarning = uiState.displayCharacterWarning,
                    displayCharacterError = uiState.displayCharacterError
                )

                SubmitIconButton(
                    onClick = {
                        onSubmit(uiState.question)
                    },
                    enabled = uiState.question.isNotBlank() && !uiState.displayCharacterError
                            && !uiState.isPiiError && !uiState.isLoading
                )
            }
        }
    }
}

@Composable
private fun Modifier.modifyIfPiiError(
    isFocused: Boolean,
    isPiiError: Boolean
): Modifier {
    return this.then(
        if (isFocused) {
            val color = if (isPiiError)
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
    question:String,
    modifier: Modifier = Modifier
) {
    if (!isFocused && question.isEmpty()) {
        Text(
            text = stringResource(id = R.string.input_label),
            color = GovUkTheme.colourScheme.textAndIcons.secondary,
            modifier = modifier
        )
    } else {
        Text(
            text = question,
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
private fun SubmitIconButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
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
            contentDescription = stringResource(id = R.string.button_alt),
        )
    }
}

@Composable
private fun CharacterCountMessage(
    charactersRemaining: Int,
    displayCharacterWarning: Boolean,
    displayCharacterError: Boolean,
    modifier: Modifier = Modifier
) {
    val charactersRemaining = abs(charactersRemaining)
    var color = GovUkTheme.colourScheme.textAndIcons.secondary
    var style = GovUkTheme.typography.subheadlineRegular
    var text = ""

    when {
        displayCharacterWarning -> {
            text = pluralStringResource(
                id = R.plurals.characterCountUnderOrAtLimit,
                count = charactersRemaining,
                charactersRemaining
            )
        }
        displayCharacterError -> {
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