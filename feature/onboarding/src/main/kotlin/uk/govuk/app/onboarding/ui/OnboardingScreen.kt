package uk.govuk.app.onboarding.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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

            Row() {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f)
                        .padding(start = 11.dp, top = 8.dp, end = 11.dp, bottom = 8.dp)
                ) {
                    Text(text = "Continue")
                }
            }

            TextButton(
                onClick = { },
                modifier = Modifier.padding(start = 11.dp, top = 16.dp, end = 11.dp, bottom = 32.dp)
            ) {
                Text("Skip")
            }
        }
    }
}

private data class OnboardingPage(
    val title: String,
    val body: String,
    @DrawableRes val image: Int
)
