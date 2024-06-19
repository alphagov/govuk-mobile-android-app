package uk.govuk.app.onboarding.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import uk.govuk.app.onboarding.R
import uk.govuk.app.onboarding.ui.theme.Black30


@Composable
fun OnboardingRoute() {
    // Collect UI state from view model here and pass to screen (if necessary)
    OnboardingScreen()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingScreen() {
    val pages = listOf(
        OnboardingPage(
            title = "Get things done on the go",
            body = "Access government services and information on your phone using the GOV.UK app",
            image = R.drawable.image_1
        ),
        OnboardingPage(
            title = "Quickly get back to previous pages",
            body = "Pages you’ve visited are saved so you can easily return to them",
            image = R.drawable.image_2
        ),
        OnboardingPage(
            title = "Tailored to you",
            body = "Choose topics that are relevant to you so you can find what you need faster",
            image = R.drawable.image_3
        ),
    )

    val pagerState = rememberPagerState(pageCount = {
        pages.count()
    })

    HorizontalPager(
        state = pagerState,
        verticalAlignment = Alignment.Top
    ) { pageIndex ->
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
                    .padding(start = 32.dp, top = 32.dp, end = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = pages[pageIndex].image),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = pages[pageIndex].title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = pages[pageIndex].body,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Divider(
                thickness = 1.dp,
                color = Black30
            )

            Column(
                modifier = Modifier
                    .padding(start = 32.dp, top = 16.dp, end = 32.dp, bottom = 32.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Continue")
                }

                TextButton(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Skip",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private data class OnboardingPage(
    val title: String,
    val body: String,
    @DrawableRes val image: Int
)
