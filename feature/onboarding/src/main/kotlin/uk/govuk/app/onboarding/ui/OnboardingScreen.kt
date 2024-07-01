package uk.govuk.app.onboarding.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
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
internal fun OnboardingRoute(
    onboardingCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Todo - should probably move this to view model
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

    OnboardingScreen(
        pages,
        onDone = onboardingCompleted,
        onSkip = onboardingCompleted,
        modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingScreen(
    pages: List<OnboardingPage>,
    onDone: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = {
        pages.count()
    })

    Column(
        modifier = modifier
            .fillMaxWidth()
            .safeDrawingPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.Top
        ) { pageIndex ->
            Page(pages[pageIndex])
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline,
        )

        val coroutineScope = rememberCoroutineScope()
        val onContinue: () -> Unit = {
            coroutineScope.launch {
                pagerState.animateScrollToPage(
                    pagerState.currentPage + 1,
                    animationSpec = tween(500)
                )
            }
        }

        Footer(
            currentPageIndex = pagerState.currentPage,
            pageCount = pagerState.pageCount,
            onContinue = onContinue,
            onDone = onDone,
            onSkip = onSkip
        )
    }
}

@Composable
private fun Page(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(start = 32.dp, top = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT) {
            Image(
                painter = painterResource(id = page.image),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        Text(
            text = page.title,
            modifier = Modifier.focusable(),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.body,
            modifier = Modifier.focusable(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Footer(
    currentPageIndex: Int,
    pageCount: Int,
    onContinue: () -> Unit,
    onDone: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(start = 32.dp, top = 16.dp, end = 32.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

        if (currentPageIndex < pageCount - 1) {
            if (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
                HorizontalButtonGroup(
                    onContinue = onContinue,
                    onSkip = onSkip
                )
            } else {
                VerticalButtonGroup(
                    onContinue = onContinue,
                    onSkip = onSkip
                )
            }
        } else {
            PrimaryButton(
                text = stringResource(id = R.string.doneButton),
                onClick = onDone,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        PagerIndicator(pageCount, currentPageIndex)
    }
}

@Composable
private fun VerticalButtonGroup(
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        PrimaryButton(
            text = stringResource(id = R.string.continueButton),
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        )
        SecondaryButton(
            text = stringResource(id = R.string.skipButton),
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun HorizontalButtonGroup(
    onContinue: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        PrimaryButton(
            text = stringResource(id = R.string.continueButton),
            onClick = onContinue,
            modifier = Modifier.weight(0.5f)
        )
        SecondaryButton(
            text = stringResource(id = R.string.skipButton),
            onClick = onSkip,
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
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
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
