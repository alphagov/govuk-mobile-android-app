package uk.govuk.app.notifications.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.HomeNavigationCard
import uk.govuk.app.notifications.NotificationsPromptWidgetViewModel
import uk.govuk.app.notifications.R

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
        onSuppressClick = { onSuppressClick(title) }
    )
}
