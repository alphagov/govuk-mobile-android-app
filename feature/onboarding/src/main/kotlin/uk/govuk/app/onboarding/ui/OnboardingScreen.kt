package uk.govuk.app.onboarding.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowHeightSizeClass
import kotlinx.coroutines.launch
import uk.govuk.app.design.ui.components.PrimaryButton
import uk.govuk.app.design.ui.components.SecondaryButton
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.onboarding.OnboardingPage
import uk.govuk.app.onboarding.OnboardingViewModel
import uk.govuk.app.onboarding.R
import uk.govuk.app.onboarding.ui.theme.LightGrey


@Composable
internal fun OnboardingRoute(
    onboardingCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: OnboardingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    OnboardingScreen(
        uiState.pages,
        onPageView = { pageIndex -> viewModel.onPageView(pageIndex) },
        onContinue = { pageIndex, cta ->
            viewModel.onContinue(pageIndex, cta)
        },
        onSkip = { pageIndex, cta ->
            viewModel.onSkip(pageIndex, cta)
            onboardingCompleted()
        },
        onDone = { pageIndex, cta ->
            viewModel.onDone(pageIndex, cta)
            onboardingCompleted()
        },
        onPagerIndicator = { pageIndex ->
            viewModel.onPagerIndicator(pageIndex)
        },
        modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingScreen(
    pages: List<OnboardingPage>,
    onPageView: (Int) -> Unit,
    onContinue: (Int, String) -> Unit,
    onSkip: (Int, String) -> Unit,
    onDone: (Int, String) -> Unit,
    onPagerIndicator: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = {
        pages.count()
    })

    LaunchedEffect(pagerState.currentPage) {
        onPageView(pagerState.currentPage)
    }

    Column(modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.Top
        ) { pageIndex ->
            Page(pages[pageIndex])
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = GovUkTheme.colourScheme.strokes.listDivider,
        )

        val coroutineScope = rememberCoroutineScope()
        val changePage: (Int) -> Unit = { pageIndex ->
            coroutineScope.launch {
                pagerState.animateScrollToPage(
                    pageIndex,
                    animationSpec = tween(500)
                )
            }
        }

        val currentPage = pagerState.currentPage
        Footer(
            currentPageIndex = currentPage,
            pageCount = pagerState.pageCount,
            onContinue = { cta ->
                onContinue(pagerState.currentPage, cta)
                changePage(pagerState.currentPage + 1)
            },
            onDone = { cta ->
                onDone(currentPage, cta)
            },
            onSkip = { cta ->
                onSkip(currentPage, cta)
            },
            onPagerIndicator = { index ->
                onPagerIndicator(currentPage)
                changePage(index)
            }
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
            .padding(top = GovUkTheme.spacing.extraLarge)
            .padding(horizontal = GovUkTheme.spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT) {
            Image(
                painter = painterResource(id = page.image),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(GovUkTheme.spacing.extraLarge))
        }

        Text(
            text = stringResource(page.title),
            modifier = Modifier.focusable(),
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            style = GovUkTheme.typography.titleLargeBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(GovUkTheme.spacing.medium))

        Text(
            text = stringResource(page.body),
            modifier = Modifier.focusable(),
            color = GovUkTheme.colourScheme.textAndIcons.primary,
            style = GovUkTheme.typography.bodyRegular,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Footer(
    currentPageIndex: Int,
    pageCount: Int,
    onContinue: (String) -> Unit,
    onDone: (String) -> Unit,
    onSkip: (String) -> Unit,
    onPagerIndicator: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(top = GovUkTheme.spacing.medium, bottom = GovUkTheme.spacing.small)
            .padding(horizontal = GovUkTheme.spacing.extraLarge),
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
            DoneButton(
                onClick = { cta -> onDone(cta) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(GovUkTheme.spacing.small))
        PagerIndicator(pageCount, currentPageIndex, onPagerIndicator)
    }
}

@Composable
private fun VerticalButtonGroup(
    onContinue: (String) -> Unit,
    onSkip: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        ContinueButton(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        )
        SkipButton(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun HorizontalButtonGroup(
    onContinue: (String) -> Unit,
    onSkip: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ContinueButton(
            onClick = onContinue,
            modifier = Modifier.weight(0.5f)
        )
        SkipButton(
            onClick = onSkip,
            modifier = Modifier.weight(0.5f)
        )
    }
}

@Composable
private fun ContinueButton(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cta = stringResource(id = R.string.continueButton)
    PrimaryButton(
        text = cta,
        onClick = { onClick(cta) },
        modifier = modifier
    )
}

@Composable
private fun SkipButton(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cta = stringResource(id = R.string.skipButton)
    SecondaryButton(
        text = cta,
        onClick = { onClick(cta) },
        modifier = modifier
    )
}

@Composable
private fun DoneButton(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cta = stringResource(id = R.string.doneButton)
    PrimaryButton(
        text = cta,
        onClick = { onClick(cta) },
        modifier = modifier
    )
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0 until pageCount) {
            val description = stringResource(id = R.string.pageIndicatorContentDescription, i + 1, pageCount)

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onClick(i) }
                    .semantics {
                        contentDescription = description
                    },
                contentAlignment = Alignment.Center
            ) {
                if (i == currentPage) {
                    FilledCircle()
                } else {
                    OutlinedCircle()
                }
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
            .background(GovUkTheme.colourScheme.surfaces.primary)
    )
}
