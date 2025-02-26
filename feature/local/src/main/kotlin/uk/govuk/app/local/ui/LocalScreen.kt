package uk.govuk.app.local.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ChildPageHeader
import uk.govuk.app.design.ui.component.FootnoteRegularLabel
import uk.govuk.app.design.ui.component.SmallHorizontalSpacer
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalUiState
import uk.govuk.app.local.LocalViewModel
import uk.govuk.app.local.R

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
        onLinkSubmit = { viewModel.updateServices() },
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
private fun LocalScreen(
    uiState: LocalUiState,
    onPostcodeSubmit: (String) -> Unit,
    onLocalAuthoritySubmit: () -> Unit,
    onLinkSubmit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var postcode by remember { mutableStateOf(uiState.postcode) }

    Column(
        modifier.padding(0.dp)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(GovUkTheme.spacing.small)
        ) {
            item {
                ChildPageHeader(
                    text = stringResource(R.string.local_widget_title),
                    onBack = onBack,
                    modifier = modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.fillMaxWidth()
                        .padding(
                            horizontal = GovUkTheme.spacing.medium,
                            vertical = GovUkTheme.spacing.small
                        )
                ) {
                    BodyRegularLabel(
                        text = stringResource(R.string.local_description),
                        modifier = Modifier
                    )
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.fillMaxWidth()
                        .padding(
                            horizontal = GovUkTheme.spacing.medium,
                            vertical = GovUkTheme.spacing.small
                        )
                ) {
                    OutlinedTextField(
                        value = postcode,
                        onValueChange = { postcode = it.uppercase() },
                        label = { Text(text = stringResource(R.string.local_postcode)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters
                        )
                    )
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.fillMaxWidth()
                        .padding(
                            horizontal = GovUkTheme.spacing.medium,
                            vertical = GovUkTheme.spacing.small
                        )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_help_outline_24),
                        contentDescription = stringResource(R.string.local_postcode_usage),
                        tint = GovUkTheme.colourScheme.textAndIcons.trailingIcon,
                        modifier = Modifier.height(18.dp)
                    )
                    SmallHorizontalSpacer()
                    FootnoteRegularLabel(
                        text = stringResource(R.string.local_postcode_usage),
                        color = GovUkTheme.colourScheme.textAndIcons.trailingIcon,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.fillMaxWidth()
                        .padding(GovUkTheme.spacing.small)
                ) {
                    TextButton(
                        onClick = { onPostcodeSubmit(postcode) }
                    ) {
                        BodyRegularLabel(
                            text = stringResource(R.string.local_custodian_button),
                            color = GovUkTheme.colourScheme.textAndIcons.link,
                            modifier = Modifier
                        )
                    }
                }

                if (uiState.localCustodianCode != 0) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier.fillMaxWidth()
                            .padding(GovUkTheme.spacing.small)
                    ) {
                        FootnoteRegularLabel(
                            text = uiState.localCustodianCode.toString(),
                            modifier = Modifier.padding(start = GovUkTheme.spacing.medium)
                        )
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.fillMaxWidth()
                        .padding(GovUkTheme.spacing.small)
                ) {
                    TextButton(
                        onClick = { onLocalAuthoritySubmit() }
                    ) {
                        BodyRegularLabel(
                            text = stringResource(R.string.local_authority_button),
                            color = GovUkTheme.colourScheme.textAndIcons.link,
                            modifier = Modifier
                        )
                    }
                }

                if (uiState.localAuthorityName.isNotEmpty() && uiState.localAuthorityUrl.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier.fillMaxWidth()
                            .padding(GovUkTheme.spacing.small)
                    ) {
                        val context = LocalContext.current
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(uiState.localAuthorityUrl)

                        TextButton(
                            onClick = { context.startActivity(intent) },
                            modifier = Modifier.padding(start = GovUkTheme.spacing.medium)
                        ) {
                            FootnoteRegularLabel(
                                text = uiState.localAuthorityName,
                                color = GovUkTheme.colourScheme.textAndIcons.link,
                                modifier = Modifier
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.fillMaxWidth()
                        .padding(GovUkTheme.spacing.small)
                ) {
                    TextButton(
                        onClick = { onLinkSubmit() }
                    ) {
                        BodyRegularLabel(
                            text = stringResource(R.string.local_services_button),
                            color = GovUkTheme.colourScheme.textAndIcons.link,
                            modifier = Modifier
                        )
                    }
                }

                if (uiState.binsUrl.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier.fillMaxWidth()
                            .padding(GovUkTheme.spacing.small)
                    ) {
                        val context = LocalContext.current
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(uiState.binsUrl)

                        TextButton(
                            onClick = { context.startActivity(intent) },
                            modifier = Modifier.padding(start = GovUkTheme.spacing.medium)
                        ) {
                            FootnoteRegularLabel(
                                text = "Bin collection",
                                color = GovUkTheme.colourScheme.textAndIcons.link,
                                modifier = Modifier
                            )
                        }
                    }
                }

                if (uiState.taxUrl.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier.fillMaxWidth()
                            .padding(GovUkTheme.spacing.small)
                    ) {
                        val context = LocalContext.current
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(uiState.taxUrl)

                        TextButton(
                            onClick = { context.startActivity(intent) },
                            modifier = Modifier.padding(start = GovUkTheme.spacing.medium)
                        ) {
                            FootnoteRegularLabel(
                                text = "Council tax",
                                color = GovUkTheme.colourScheme.textAndIcons.link,
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        }
    }
}
