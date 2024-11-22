package uk.govuk.app.networking.ui.component

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.networking.R
import uk.govuk.app.networking.domain.Constants.GOV_UK_URL

@Composable
fun ProblemMessage(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    buttonTitle: String? = null,
    onButtonClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Message(
        title = title ?: stringResource(R.string.problem_title),
        description = description ?: stringResource(R.string.problem_description),
        buttonTitle = buttonTitle ?: stringResource(R.string.go_to_the_gov_uk_website),
        modifier = modifier,
        externalLink = true,
        onButtonClick = onButtonClick ?: {
            Intent(Intent.ACTION_VIEW).let { intent ->
                intent.data = Uri.parse(GOV_UK_URL)
                context.startActivity(intent)
            }
        }
    )
}

@Preview
@Composable
private fun ProblemMessagePreview() {
    GovUkTheme {
        ProblemMessage()
    }
}
