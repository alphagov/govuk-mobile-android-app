package uk.govuk.app.onboarding.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextButton
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
import uk.govuk.app.design.ui.component.BodyRegularLabel
import uk.govuk.app.design.ui.component.ExtraLargeVerticalSpacer
import uk.govuk.app.design.ui.component.HorizontalButtonGroup
import uk.govuk.app.design.ui.component.LargeTitleBoldLabel
import uk.govuk.app.design.ui.component.ListDivider
import uk.govuk.app.design.ui.component.MediumVerticalSpacer
import uk.govuk.app.design.ui.component.PrimaryButton
import uk.govuk.app.design.ui.component.SmallVerticalSpacer
import uk.govuk.app.design.ui.component.VerticalButtonGroup
import uk.govuk.app.design.ui.theme.GovUkTheme
import uk.govuk.app.onboarding.OnboardingPage
import uk.govuk.app.onboarding.OnboardingViewModel
import uk.govuk.app.onboarding.R


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
        onContinue = { text ->
            viewModel.onButtonClick(text)
        },
        onSkip = { text ->
            viewModel.onButtonClick(text)
            onboardingCompleted()
        },
        onDone = { text ->
            viewModel.onButtonClick(text)
            onboardingCompleted()
        },
        onPagerIndicator = {
            viewModel.onPagerIndicatorClick()
        },
        modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingScreen(
    pages: List<OnboardingPage>,
    onPageView: (Int) -> Unit,
    onContinue: (String) -> Unit,
    onSkip: (String) -> Unit,
    onDone: (String) -> Unit,
    onPagerIndicator: () -> Unit,
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

        ListDivider()

        val coroutineScope = rememberCoroutineScope()
        val changePage: (Int) -> Unit = { pageIndex ->
            coroutineScope.launch {
                pagerState.scrollToPage(
                    pageIndex
                )
            }
        }

        val currentPage = pagerState.currentPage
        Footer(
            currentPageIndex = currentPage,
            pageCount = pagerState.pageCount,
            onContinue = { text ->
                onContinue(text)
                changePage(pagerState.currentPage + 1)
            },
            onDone = { text ->
                onDone(text)
            },
            onSkip = { text ->
                onSkip(text)
            },
            onPagerIndicator = { index ->
                onPagerIndicator()
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
            .padding(top = GovUkTheme.spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (windowSizeClass.windowHeightSizeClass != WindowHeightSizeClass.COMPACT) {
            Image(
                painter = painterResource(id = page.image),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = GovUkTheme.spacing.extraLarge)
            )

            ExtraLargeVerticalSpacer()
        }

        LargeTitleBoldLabel(
            stringResource(page.title),
            modifier = Modifier
                .focusable()
                .padding(horizontal = GovUkTheme.spacing.extraLarge),
            textAlign = TextAlign.Center
        )
        MediumVerticalSpacer()
        BodyRegularLabel(
            stringResource(page.body),
            modifier = Modifier
                .focusable()
                .padding(horizontal = GovUkTheme.spacing.extraLarge),
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
            .padding(horizontal = GovUkTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

        val continueButtonText = stringResource(R.string.continueButton)
        val skipButtonText = stringResource(R.string.skipButton)

        if (currentPageIndex < pageCount - 1) {
            if (windowSizeClass.windowHeightSizeClass == WindowHeightSizeClass.COMPACT) {
                HorizontalButtonGroup(
                    primaryText = continueButtonText,
                    onPrimary = { onContinue(continueButtonText) },
                    secondaryText = skipButtonText,
                    onSecondary = { onSkip(skipButtonText) }
                )
            } else {
                VerticalButtonGroup(
                    primaryText = continueButtonText,
                    onPrimary = { onContinue(continueButtonText) },
                    secondaryText = skipButtonText,
                    onSecondary = { onSkip(skipButtonText) }
                )
            }
        } else {
            DoneButton(
                onClick = { text -> onDone(text) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        SmallVerticalSpacer()
        PagerIndicator(pageCount, currentPageIndex, onPagerIndicator)
    }
}

@Composable
private fun DoneButton(
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val text = stringResource(id = R.string.doneButton)
    PrimaryButton(
        text = text,
        onClick = { onClick(text) },
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

            TextButton(
                onClick = { onClick(i) },
                modifier = Modifier.semantics {
                    contentDescription = description
                }
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
            .border(2.dp, GovUkTheme.colourScheme.strokes.pageControlsInactive, CircleShape)
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
