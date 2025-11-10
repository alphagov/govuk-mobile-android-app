package uk.gov.govuk.design.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation

private const val ALLOW_LIST_REGEX = "[^a-zA-Z0-9\\s,.!?]"

@Composable
fun SanitisedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    supportingText: @Composable (() -> Unit)? = null
) {
    val allowlistRegex = remember { Regex(ALLOW_LIST_REGEX) }

    TextField(
        value = value,
        onValueChange = { newValue ->
            val sanitisedText = newValue.replace(allowlistRegex, "")
            onValueChange(sanitisedText)
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        shape = shape,
        colors = colors,
        supportingText = supportingText
    )
}

@Composable
fun SanitisedBasicTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null,
    decorationBox: @Composable ((innerTextField: @Composable (() -> Unit)) -> Unit) = @Composable { innerTextField ->
        innerTextField()
    },
    cursorBrush: Brush,
) {
    val allowlistRegex = remember { Regex(ALLOW_LIST_REGEX) }

    BasicTextField(
        value = value,
        onValueChange = { newValue ->
            val originalText = newValue.text
            val sanitisedText = originalText.replace(allowlistRegex, "")

            if (originalText == sanitisedText) {
                onValueChange(newValue)
                return@BasicTextField
            }

            val originalCursorPos = newValue.selection.start
            val charsRemovedBeforeCursor = originalText.take(originalCursorPos)
                .count { allowlistRegex.matches(it.toString()) }

            val newCursorPos = (originalCursorPos - charsRemovedBeforeCursor).coerceAtLeast(0)

            onValueChange(
                newValue.copy(
                    text = sanitisedText,
                    selection = TextRange(newCursorPos)
                )
            )
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = decorationBox
    )
}
