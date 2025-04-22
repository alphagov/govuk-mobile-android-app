package uk.govuk.app.local.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.govuk.design.ui.component.BodyBoldLabel
import uk.gov.govuk.design.ui.component.BodyRegularLabel
import uk.gov.govuk.design.ui.component.FixedPrimaryButton
import uk.gov.govuk.design.ui.component.FullScreenHeader
import uk.gov.govuk.design.ui.component.LargeVerticalSpacer
import uk.gov.govuk.design.ui.component.SmallVerticalSpacer
import uk.gov.govuk.design.ui.component.Title1BoldLabel
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.govuk.app.local.LocalUiState
import uk.govuk.app.local.LocalViewModel
import uk.govuk.app.local.R

@Composable
internal fun LocalEntryRoute(
    onBack: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: LocalViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LocalEntryScreen(
        uiState = uiState,
        onBack = onBack,
        onCancel = onCancel,
        onPageView = { viewModel.onLookupPageView() },
        onPostcodeLookup = { buttonText, postcode ->
            viewModel.onSearchPostcode(
                buttonText = buttonText,
                postcode = postcode
            )
        },
        modifier = modifier
    )
}

@Composable
private fun LocalEntryScreen(
    uiState: LocalUiState,
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onPageView: () -> Unit,
    onPostcodeLookup: (String, String) -> Unit,
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
            FullScreenHeader(
                modifier = Modifier
                    .semantics {
                        isTraversalGroup = true
                        traversalIndex = -1f
                    },
                onBack = { onBack() },
                actionText = "Cancel",
                onAction = onCancel
            )
        },
        bottomBar = {
            BottomNavBar(
                postcode = postcode,
                onPostcodeLookup = onPostcodeLookup,
                modifier = Modifier
                    .semantics {
                        isTraversalGroup = true
                        traversalIndex = 1f
                    }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = GovUkTheme.spacing.medium)
                .fillMaxWidth()
        ) {
            SmallVerticalSpacer()
            Title1BoldLabel(
                text = stringResource(R.string.local_whats_your_postcode)
            )
            SmallVerticalSpacer()
            BodyRegularLabel(
                text = stringResource(R.string.local_postcode_example)
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
                        handleColor = GovUkTheme.colourScheme.surfaces.textFieldHighlighted,
                        backgroundColor = GovUkTheme.colourScheme.surfaces.textFieldHighlighted
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
        }
    }

    LaunchedEffect(uiState) {
        // Todo - navigate back to home screen when a local authority is returned, logic will be
        //  updated in future tickets!
        if (uiState.localAuthority != null) {
            onCancel()
        }
    }
}

@Composable
private fun BottomNavBar(
    postcode: String,
    onPostcodeLookup: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(GovUkTheme.colourScheme.surfaces.background)) {
        val buttonText = stringResource(R.string.local_confirm_button)
        FixedPrimaryButton(
            text = buttonText,
            onClick = { onPostcodeLookup(buttonText, postcode) }
        )
    }
}
