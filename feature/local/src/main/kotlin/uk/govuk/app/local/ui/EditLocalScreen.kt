package uk.govuk.app.local.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.CompactButton
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalUiState
import uk.govuk.app.local.LocalViewModel

@Composable
internal fun EditLocalRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LocalViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    EditLocalScreen(
        uiState = uiState,
        onPageView = { viewModel.onEditPageView() },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun EditLocalScreen(
    uiState: LocalUiState,
    onPageView: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(modifier) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth(1f)
                .padding(GovUkTheme.spacing.medium)
        ) {
            OutlinedTextField(
                value = uiState.postcode,
                onValueChange = { uiState.postcode = it },
                label = { Text(text = "Postcode") }
            )
            SmallHorizontalSpacer()
            CompactButton(
                text = "Go",
                onClick = { },
                modifier = modifier
            )
        }

        Column(
            modifier = modifier.fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            for (i in 1..16) {
                Row(
                    modifier = modifier.height(40.dp)
                        .clickable(
                            onClick = onBack
                        )
                        .padding(
                            top = GovUkTheme.spacing.small,
                            bottom = GovUkTheme.spacing.small
                        )
                ) {
                    Text(text = "$i Whitechapel High Street, ${uiState.postcode}")
                }
                SmallVerticalSpacer()
            }
        }
    }
}
