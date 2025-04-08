package uk.govuk.app.local.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalUiState
import uk.govuk.app.local.LocalViewModel
import uk.govuk.app.local.R

@Composable
internal fun LocalEntryRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: LocalViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LocalEntryScreen(
        uiState = uiState,
        onBack = onBack,
        onPageView = { viewModel.onEditPageView() },
        onPostcodeLookup = { postcode ->
            viewModel.onSearchPostcode(postcode)
        },
        modifier = modifier
    )
}

@Composable
private fun LocalEntryScreen(
    uiState: LocalUiState,
    onBack: () -> Unit,
    onPageView: () -> Unit,
    onPostcodeLookup: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var postcode by remember { mutableStateOf(uiState.postcode) }

    LaunchedEffect(Unit) {
        onPageView()
    }

    Scaffold(
        containerColor = GovUkTheme.colourScheme.surfaces.background,
        modifier = modifier.fillMaxWidth(),

        topBar = {
            FullScreenHeader(onBack = { onBack() })
        },
        bottomBar = {
            BottomNavBar(
                postcode = postcode,
                onPostcodeLookup = onPostcodeLookup,
                modifier = modifier
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .fillMaxWidth()
        ) {
            SmallVerticalSpacer()
            Title1BoldLabel(
                text = stringResource(R.string.local_whats_your_postcode),
                modifier = modifier,
            )
            SmallVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(R.string.local_postcode_example),
                modifier = modifier
            )
            SmallVerticalSpacer()
            TextField(
                value = postcode,
                onValueChange = { postcode = it.uppercase() },
                label = {
                    Text(
                        text = stringResource(R.string.local_postcode_default_text)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters
                ),
                singleLine = true,
                colors = TextFieldDefaults.colors(
//                        TODO: we'll need more colours for error states, so leaving these colours here for now
                    cursorColor = GovUkTheme.colourScheme.strokes.textFieldCursor,
//                        errorContainerColor = Color.Red,
//                        errorCursorColor = Color.Red,
//                        errorIndicatorColor = Color.Yellow,
//                        errorLabelColor = Color.Red,
//                        errorPrefixColor = Color.Red,
//                        errorPlaceholderColor = Color.Red,
//                        errorSuffixColor = Color.Red
//                        errorSupportingTextColor = Color.Red,
//                        errorTextColor = Color.Red,
                    focusedContainerColor = GovUkTheme.colourScheme.surfaces.textFieldBackground,
                    focusedIndicatorColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    focusedLabelColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    focusedPlaceholderColor = GovUkTheme.colourScheme.textAndIcons.secondary,
//                        focusedPrefixColor = GovUkTheme.colourScheme.textAndIcons.secondary,
//                        focusedSuffixColor = Color.Blue,
//                        focusedSupportingTextColor = Color.Green,
                    focusedTextColor = GovUkTheme.colourScheme.textAndIcons.primary,
                    selectionColors = TextSelectionColors(
                        handleColor = GovUkTheme.colourScheme.surfaces.highlightedTextField,
                        backgroundColor = GovUkTheme.colourScheme.surfaces.highlightedTextField
                    ),
                    unfocusedContainerColor = GovUkTheme.colourScheme.surfaces.textFieldBackground,
                    unfocusedIndicatorColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    unfocusedLabelColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                    unfocusedPlaceholderColor = GovUkTheme.colourScheme.textAndIcons.secondary,
//                        unfocusedPrefixColor = Color.Gray,
//                        unfocusedSuffixColor = Color.Gray,
//                        unfocusedSupportingTextColor = Color.Gray,
                    unfocusedTextColor = GovUkTheme.colourScheme.textAndIcons.secondary,
                )
            )
            LargeVerticalSpacer()
            BodyBoldLabel(stringResource(R.string.local_postcode_use_title))
            LargeVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.local_postcode_use_description_1))
            LargeVerticalSpacer()
            BodyRegularLabel(stringResource(R.string.local_postcode_use_description_2))

            ExtraLargeVerticalSpacer()

            BodyRegularLabel(
                text = uiState.toString(),
                modifier = modifier
            )
        }
    }
}

@Composable
private fun BottomNavBar(
    postcode: String,
    onPostcodeLookup: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
        HorizontalDivider(
            thickness = 1.dp,
            color = GovUkTheme.colourScheme.strokes.fixedContainer
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 64.dp)
                .padding(
                    start = GovUkTheme.spacing.medium,
                    end = GovUkTheme.spacing.medium,
                    bottom = GovUkTheme.spacing.large
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrimaryButton(
                text = stringResource(R.string.local_confirm_button),
                onClick = { onPostcodeLookup(postcode) },
            )
        }
    }
}
