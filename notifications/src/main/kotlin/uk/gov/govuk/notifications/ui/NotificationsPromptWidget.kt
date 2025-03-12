package uk.gov.govuk.notifications.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.HomeNavigationCard
import uk.gov.govuk.notifications.NotificationsPromptWidgetViewModel
import uk.gov.govuk.notifications.R

@Composable
fun NotificationsPromptWidget(
    onClick: (String) -> Unit,
    onSuppressClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: NotificationsPromptWidgetViewModel = hiltViewModel()
    val title = stringResource(R.string.prompt_widget_title)

    HomeNavigationCard(
        title = title,
        onClick = {
            viewModel.onClick()
            onClick(title)
        },
        modifier = modifier,
        onSuppressClick = { onSuppressClick(title) },
        icon = R.drawable.notifications_bell
    )
}
