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
import androidx.compose.foundation.layout.safeDrawingPadding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import kotlinx.coroutines.launch
import uk.govuk.app.onboarding.R
import uk.govuk.app.onboarding.ui.theme.LightGrey

private data class OnboardingPage(
    val title: String,
    val body: String,
    @DrawableRes val image: Int
)

@Composable
fun OnboardingRoute(modifier: Modifier = Modifier) {
    // Collect UI state from view model here and pass to screen (if necessary)
    val pages = listOf(
        OnboardingPage(
            title = stringResource(id = R.string.getThingsDoneScreenTitle),
            body = stringResource(id = R.string.getThingsDoneScreenBody),
            image = R.drawable.image_1
        ),
        OnboardingPage(
            title = stringResource(id = R.string.backToPreviousScreenTitle),
            body = stringResource(id = R.string.backToPreviousScreenBody),
            image = R.drawable.image_2
        ),
        OnboardingPage(
            title = stringResource(id = R.string.tailoredToYouScreenTitle),
            body = stringResource(id = R.string.tailoredToYouScreenBody),
            image = R.drawable.image_3
        ),
    )

    OnboardingScreen(pages, modifier)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingScreen(
    pages: List<OnboardingPage>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = {
        pages.count()
    })

    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) { pageIndex ->
        Column(modifier = Modifier
            .fillMaxWidth()
            .safeDrawingPadding()
        ) {
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
                        HorizontalButtonGroup(onClick)
                    } else {
                        VerticalButtonGroup(onClick)
                    }
                } else {
                    PrimaryButton(
                        text = stringResource(id = R.string.doneButton),
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
private fun VerticalButtonGroup(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        PrimaryButton(
            text = stringResource(id = R.string.continueButton),
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        )
        SecondaryButton(
            text = stringResource(id = R.string.skipButton),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun HorizontalButtonGroup(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        PrimaryButton(
            text = stringResource(id = R.string.continueButton),
            onClick = onClick,
            modifier = Modifier.weight(0.5f)
        )
        SecondaryButton(
            text = stringResource(id = R.string.skipButton),
            modifier = Modifier.weight(0.5f)
        )
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(text = text)
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    modifier: Modifier = Modifier
) {
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
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
