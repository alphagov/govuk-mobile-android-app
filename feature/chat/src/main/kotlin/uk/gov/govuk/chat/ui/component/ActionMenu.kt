package uk.gov.govuk.chat.ui.component

import androidx.annotation.DrawableRes
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
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
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
import uk.gov.govuk.chat.R
import uk.gov.govuk.chat.domain.Analytics
import uk.gov.govuk.chat.ui.AnalyticsEvents
import uk.gov.govuk.config.data.remote.model.ChatUrls
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme

@Composable
internal fun ActionMenu(
    launchBrowser: (url: String) -> Unit,
    hasConversation: Boolean,
    isLoading: Boolean,
    onClear: () -> Unit,
    analyticsEvents: AnalyticsEvents,
    chatUrls: ChatUrls,
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
        val aboutText = stringResource(R.string.action_about)
        MenuItem(
            onClick = {
                launchBrowser(chatUrls.about)
                analyticsEvents.onActionItemNavigationClicked(aboutText, chatUrls.about)
                expanded = false
            },
            buttonText = aboutText,
            icon = R.drawable.outline_info_24,
            modifier = modifier
        )

        val privacyText = stringResource(R.string.action_privacy)
        MenuItem(
            onClick = {
                launchBrowser(chatUrls.privacyNotice)
                analyticsEvents.onActionItemNavigationClicked(privacyText, chatUrls.privacyNotice)
                expanded = false
            },
            buttonText = privacyText,
            icon = R.drawable.outline_privacy_24,
            modifier = modifier
        )

        val feedbackText = stringResource(R.string.action_feedback)
        MenuItem(
            onClick = {
                launchBrowser(chatUrls.feedback)
                analyticsEvents.onActionItemNavigationClicked(feedbackText, chatUrls.feedback)
                expanded = false
            },
            buttonText = feedbackText,
            icon = R.drawable.outline_feedback_24,
            modifier = modifier
        )

        if (hasConversation) {
            ClearMenuItem(
                enabled = !isLoading,
                onClear = onClear,
                onClearActioned = { expanded = false },
                analyticsEvents = analyticsEvents
            )
        }
    }

    val buttonText = stringResource(id = R.string.action_alt)
    ActionIconButton(
        onClick = {
            expanded = !expanded
            if (expanded) {
                analyticsEvents.onActionItemFunctionClicked(
                    buttonText,
                    Analytics.ACTION_MENU,
                    Analytics.ACTION_MENU_ACTION
                )
            }
        }
    )
}

@Composable
private fun ClearMenuItem(
    enabled: Boolean,
    onClear: () -> Unit,
    onClearActioned: () -> Unit,
    analyticsEvents: AnalyticsEvents,
    modifier: Modifier = Modifier
) {
    val openDialog = rememberSaveable { mutableStateOf(false) }
    val buttonText = stringResource(id = R.string.action_clear)

    val colours = MenuDefaults.itemColors().copy(
        textColor = GovUkTheme.colourScheme.textAndIcons.buttonDestructive,
        trailingIconColor = GovUkTheme.colourScheme.textAndIcons.buttonDestructive,
        disabledTextColor = GovUkTheme.colourScheme.textAndIcons.buttonRemoveDisabled,
        disabledTrailingIconColor = GovUkTheme.colourScheme.textAndIcons.buttonRemoveDisabled
    )

    MenuItem(
        onClick = {
            openDialog.value = true
            analyticsEvents.onActionItemFunctionClicked(
                buttonText,
                Analytics.ACTION_MENU,
                Analytics.ACTION_MENU_CLEAR_ACTION
            )
        },
        buttonText = buttonText,
        icon = R.drawable.outline_delete_24,
        modifier = modifier,
        enabled = enabled,
        colours = colours
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
                        analyticsEvents.onActionItemFunctionClicked(
                            buttonText,
                            Analytics.ACTION_MENU,
                            Analytics.ACTION_MENU_CLEAR_YES
                        )
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
                        analyticsEvents.onActionItemFunctionClicked(
                            buttonText,
                            Analytics.ACTION_MENU,
                            Analytics.ACTION_MENU_CLEAR_NO
                        )
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
private fun MenuItem(
    onClick: () -> Unit,
    buttonText: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colours: MenuItemColors = MenuDefaults.itemColors().copy(
        textColor = GovUkTheme.colourScheme.textAndIcons.primary,
        trailingIconColor = GovUkTheme.colourScheme.textAndIcons.primary,
    )
) {
    DropdownMenuItem(
        text = {
            Text(
                text = buttonText,
                style = GovUkTheme.typography.bodyRegular,
            )
        },
        onClick = onClick,
        modifier = modifier,
        trailingIcon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
            )
        },
        enabled = enabled,
        colors = colours
    )
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
