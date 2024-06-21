package uk.govuk.app.onboarding.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import kotlinx.coroutines.launch
import uk.govuk.app.onboarding.R
import uk.govuk.app.onboarding.ui.theme.LightGrey


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
            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 32.dp, top = 32.dp, end = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT) {
                    Image(
                        painter = painterResource(id = pages[pageIndex].image),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }

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
                color = MaterialTheme.colorScheme.outline,
            )

            Column(
                modifier = Modifier
                    .padding(start = 32.dp, top = 16.dp, end = 32.dp, bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val coroutineScope = rememberCoroutineScope()
                val onClick: () -> Unit = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pageIndex + 1)
                    }
                }

                if (pageIndex < pagerState.pageCount - 1) {
                    if (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
                        MediumButtonGroup(onClick)
                    } else {
                        CompactButtonGroup(onClick)
                    }
                } else {
                    PrimaryButton(
                        text = "Done",
                        onClick = { },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                PagerIndicator(pagerState.pageCount, pageIndex)
            }
        }
    }
}

@Composable
private fun CompactButtonGroup(onClick: () -> Unit) {
    PrimaryButton(
        text = "Continue",
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    )
    SecondaryButton(text = "Skip")
}

@Composable
private fun MediumButtonGroup(onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        PrimaryButton(
            text = "Continue",
            onClick = onClick,
            modifier = Modifier.weight(0.5f)
        )
        SecondaryButton(text = "Skip", modifier = Modifier.weight(0.5f))
    }
}

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(text = text)
    }
}

@Composable
private fun SecondaryButton(text: String, modifier: Modifier = Modifier) {
    TextButton(
        onClick = { },
        modifier = modifier
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0 until pageCount) {
            if (i == currentPage) {
                FilledCircle(Modifier.padding(horizontal = 8.dp))
            } else {
                OutlinedCircle(Modifier.padding(horizontal = 8.dp))
            }
        }
    }
}

@Composable
private fun OutlinedCircle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(CircleShape)
            .border(2.dp, LightGrey, CircleShape)
    )
}

@Composable
private fun FilledCircle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
    )
}

private data class OnboardingPage(
    val title: String,
    val body: String,
    @DrawableRes val image: Int
)
