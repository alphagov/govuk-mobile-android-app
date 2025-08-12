package uk.gov.govuk.chat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import uk.gov.govuk.chat.BuildConfig
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.ui.ChatScreenEvents
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun ActionMenu(
    launchBrowser: (url: String) -> Unit,
    hasConversation: Boolean,
    onClear: () -> Unit,
    analyticsEvents: ChatScreenEvents,
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
        AboutMenuItem(
            launchBrowser = launchBrowser,
            onLinkClicked = { expanded = false },
            analyticsEvents = analyticsEvents
        )

        if (hasConversation) {
            ClearMenuItem(
                onClear = onClear,
                onClearActioned = { expanded = false },
                analyticsEvents = analyticsEvents
            )
        }
    }

    ActionIconButton(
        onClick = {
            analyticsEvents.onMenuOpen()
            expanded = !expanded
        }
    )
}

@Composable
private fun AboutMenuItem(
    launchBrowser: (url: String) -> Unit,
    onLinkClicked: () -> Unit,
    analyticsEvents: ChatScreenEvents,
    modifier: Modifier = Modifier
) {
    val buttonText = stringResource(id = R.string.action_about)

    DropdownMenuItem(
        text = {
            Text(
                text = buttonText,
                color = GovUkTheme.colourScheme.textAndIcons.primary,
                style = GovUkTheme.typography.bodyRegular,
            )
        },
        onClick = {
            analyticsEvents.onAboutClick(buttonText)
            onLinkClicked()
            launchBrowser(BuildConfig.ABOUT_APP_URL)
        },
        modifier = modifier,
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.outline_info_24),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.primary
            )
        }
    )
}

@Composable
private fun ClearMenuItem(
    onClear: () -> Unit,
    onClearActioned: () -> Unit,
    analyticsEvents: ChatScreenEvents,
    modifier: Modifier = Modifier
) {
    val openDialog = rememberSaveable { mutableStateOf(false) }
    val buttonText = stringResource(id = R.string.action_clear)

    DropdownMenuItem(
        text = {
            Text(
                text = buttonText,
                color = GovUkTheme.colourScheme.textAndIcons.buttonDestructive,
                style = GovUkTheme.typography.bodyRegular,
            )
        },
        onClick = {
            openDialog.value = true
            analyticsEvents.onClearClick(buttonText)
        },
        modifier = modifier,
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.outline_delete_24),
                contentDescription = null,
                tint = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
            )
        }
    )

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            shape = RoundedCornerShape(10.dp),
            text = {
                BodyBoldLabel(
                    text = stringResource(id = R.string.clear_dialog_title),
                    color = GovUkTheme.colourScheme.textAndIcons.primary
                )
            },
            confirmButton = {
                val buttonText = stringResource(id = R.string.clear_dialog_positive_button)

                TextButton(
                    onClick = {
                        analyticsEvents.onClearYesClick(buttonText)
                        onClear()
                        openDialog.value = false
                        onClearActioned()
                    }
                ) {
                    BodyBoldLabel(
                        text = buttonText,
                        color = GovUkTheme.colourScheme.textAndIcons.buttonDestructive
                    )
                }
            },
            dismissButton = {
                val buttonText = stringResource(id = R.string.clear_dialog_negative_button)
                TextButton(
                    onClick = {
                        analyticsEvents.onClearNoClick(buttonText)
                        openDialog.value = false
                        onClearActioned()
                    }
                ) {
                    BodyRegularLabel(
                        text = buttonText,
                        color = GovUkTheme.colourScheme.textAndIcons.link
                    )
                }
            },
            containerColor = GovUkTheme.colourScheme.surfaces.alert
        )
    }
}

@Composable
private fun ActionIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            .clip(RoundedCornerShape(30.dp))
            .height(50.dp)
            .width(50.dp)
            .border(
                1.dp,
                GovUkTheme.colourScheme.strokes.chatTextFieldBorderDisabled,
                RoundedCornerShape(30.dp)
            )
    ) {
        Icon(
            painter = painterResource(R.drawable.outline_more_vert_24),
            contentDescription = stringResource(id = R.string.action_alt),
            modifier = Modifier.padding(all = GovUkTheme.spacing.small)
        )
    }
}
