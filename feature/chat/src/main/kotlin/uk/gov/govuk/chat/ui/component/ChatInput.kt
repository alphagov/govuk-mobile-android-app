package uk.gov.govuk.chat.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.TransformOrigin
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
            .semantics { isTraversalGroup = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (isFocused) {
                CharacterCountMessage(
                    charactersRemaining = uiState.charactersRemaining,
                    displayCharacterWarning = uiState.displayCharacterWarning,
                    displayCharacterError = uiState.displayCharacterError
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(horizontal = GovUkTheme.spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        if (isFocused && uiState.question.isNotEmpty()) {
                            1f
                        } else {
                            0.88f
                        }
                    )
                    .animateContentSize(
                        animationSpec = tween(durationMillis = 100)
                    )
                    .background(
                        color = GovUkTheme.colourScheme.surfaces.chatTextFieldBackground,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clip(RoundedCornerShape(24.dp))
            ) {
                TextField(
                    textStyle = TextStyle(
                        color = GovUkTheme.colourScheme.textAndIcons.primary,
                        fontSize = GovUkTheme.typography.bodyRegular.fontSize,
                        fontWeight = GovUkTheme.typography.bodyRegular.fontWeight,
                        fontFamily = GovUkTheme.typography.bodyRegular.fontFamily,
                        lineHeight = GovUkTheme.typography.bodyRegular.lineHeight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .focusable(true)
                        .onFocusChanged {
                            isFocused = it.isFocused
                        }
                        .height(IntrinsicSize.Min)
                        .semantics { this.traversalIndex = 0f },
                    value = if (isFocused) uiState.question else "",
                    shape = RoundedCornerShape(24.dp),
                    singleLine = false,
                    minLines = 1,
                    onValueChange = onQuestionUpdated,
                    placeholder = {
                        PlaceholderText(question = uiState.question)
                    },
                    isError = uiState.isPiiError,
                    colors = inputTextFieldDefaults(),
                    trailingIcon = {
                        AnimateIcon(
                            isFocused && uiState.question.isNotEmpty(),
                            {
                                SubmitIconButton(
                                    onClick = {
                                        onSubmit(uiState.question)
                                    },
                                    enabled = !uiState.displayCharacterError
                                            && !uiState.isPiiError && !uiState.isLoading
                                )
                            }
                        )
                    }
                )
            }

            AnimateIcon(
                uiState.question.isEmpty(),
                {
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
            )
        }
    }
}

@Composable
private fun PlaceholderText(
    question: String,
    modifier: Modifier = Modifier
) {
    if (question.isEmpty()) {
        Text(
            text = stringResource(id = R.string.input_label),
            color = GovUkTheme.colourScheme.textAndIcons.secondary,
            fontSize = GovUkTheme.typography.bodyRegular.fontSize,
            fontWeight = GovUkTheme.typography.bodyRegular.fontWeight,
            fontFamily = GovUkTheme.typography.bodyRegular.fontFamily,
            lineHeight = GovUkTheme.typography.bodyRegular.lineHeight,
            modifier = modifier
        )
    } else {
        Text(
            text = question,
            color = GovUkTheme.colourScheme.textAndIcons.secondary,
            fontSize = GovUkTheme.typography.bodyRegular.fontSize,
            fontWeight = GovUkTheme.typography.bodyRegular.fontWeight,
            fontFamily = GovUkTheme.typography.bodyRegular.fontFamily,
            lineHeight = GovUkTheme.typography.bodyRegular.lineHeight,
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
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxHeight()
            .padding(9.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = modifier
                .clip(RoundedCornerShape(60.dp))
                .height(36.dp)
                .width(36.dp),
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

@Composable
private fun AnimateIcon(
    visible: Boolean,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val animationSpeed = 300

    // Start at alpha = 0 and scale of 50%
    // Finish at alpha = 1 and scale of 100%

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = tween(durationMillis = animationSpeed),
            initialScale = 0.5f,
            transformOrigin = TransformOrigin.Center
        ) + fadeIn(
            animationSpec = tween(durationMillis = animationSpeed),
            initialAlpha = 0f
        ),
        exit = scaleOut(
            animationSpec = tween(durationMillis = animationSpeed),
            targetScale = 0.5f,
            transformOrigin = TransformOrigin.Center
        ) + fadeOut(
            animationSpec = tween(durationMillis = animationSpeed),
            targetAlpha = 0f
        ),
        modifier = modifier
    ) {
        icon()
    }
}
