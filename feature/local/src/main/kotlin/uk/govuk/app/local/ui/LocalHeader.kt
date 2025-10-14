package uk.govuk.app.local.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.model.HeaderStyle
import uk.govuk.app.local.R

@Composable
internal fun LocalFullScreenHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onCancel: (() -> Unit)? = null
) {
    val style = onCancel?.let {
        HeaderStyle.ActionButton(
            title = stringResource(R.string.local_cancel_button),
            onClick = onCancel
        )
    } ?: run {
        HeaderStyle.Default
    }

    FullScreenHeader(
        modifier = modifier
            .semantics {
                isTraversalGroup = true
                traversalIndex = -1f
            },
        onBack = { onBack() },
        style = style
    )
}
