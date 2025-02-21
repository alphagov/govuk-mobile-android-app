package uk.govuk.app.local.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.govuk.app.design.ui.component.BodyBoldLabel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.SecondaryButton
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalUiState
import uk.govuk.app.local.LocalViewModel
import uk.govuk.app.local.navigation.navigateToEditLocal

@Composable
internal fun LocalRoute(
    navController: NavController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: LocalViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LocalScreen(
        uiState = uiState,
        onPageView = { viewModel.onPageView() },
        onLocalAuthorityClick = { name, url -> viewModel.onLocalAuthorityClicked(name, url) },
        onEditClick = {
            viewModel.onEditClick()
            navController.navigateToEditLocal()
        },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun LocalScreen(
    uiState: LocalUiState,
    onPageView: () -> Unit,
    onLocalAuthorityClick: (String, String) -> Unit,
    onEditClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name = uiState.localAuthorityName
    val url = uiState.localAuthorityUrl

    val context = LocalContext.current
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)

    var postcode by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        onPageView()
    }

    Column(modifier) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
                .padding(GovUkTheme.spacing.medium)
        ) {
            BodyRegularLabel(text = uiState.postcode)
            SmallHorizontalSpacer()
            TextButton(
                onClick = onEditClick
            ) {
                BodyRegularLabel(
                    text = "Edit",
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
            TextButton(
                onClick = {
                    onLocalAuthorityClick(name, url)
                    context.startActivity(intent)
                }
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
