package uk.govuk.app.local.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.PrimaryButton
import uk.govuk.app.design.ui.component.SecondaryButton
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalUiState
import uk.govuk.app.local.LocalViewModel

@Composable
internal fun LocalRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LocalViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LocalScreen(
        uiState = uiState,
        onPostcodeSubmit = { postcode -> viewModel.updatePostcode(postcode) },
        onLocalAuthoritySubmit = { viewModel.updateLocalAuthority() },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun LocalScreen(
    uiState: LocalUiState,
    onPostcodeSubmit: (String) -> Unit,
    onLocalAuthoritySubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var postcode by remember { mutableStateOf(uiState.postcode) }

    Column(modifier) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            OutlinedTextField(
                value = postcode,
                onValueChange = { postcode = it },
                label = { Text(text = "Postcode") }
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            PrimaryButton(
                text = "Get Local Custodian Code",
                onClick = { onPostcodeSubmit(postcode) },
                modifier = modifier
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            BodyRegularLabel(
                text = uiState.localCustodianCode.toString(),
                modifier = Modifier
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            PrimaryButton(
                text = "Get Local Authority",
                onClick = { onLocalAuthoritySubmit() },
                modifier = modifier
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            val context = LocalContext.current
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(uiState.localAuthorityUrl)

            TextButton(
                onClick = { context.startActivity(intent) }
            ) {
                BodyRegularLabel(
                    text = uiState.localAuthorityName,
                    color = GovUkTheme.colourScheme.textAndIcons.link,
                    modifier = Modifier
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            SecondaryButton(
                text = "Back",
                onClick = onBack,
                modifier = modifier,
            )
        }
    }
}
