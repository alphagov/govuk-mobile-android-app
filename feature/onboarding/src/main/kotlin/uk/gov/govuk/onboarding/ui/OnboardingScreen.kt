package uk.gov.govuk.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.gov.govuk.design.ui.component.DoubleButtonGroup
import uk.gov.govuk.design.ui.component.ExtraLargeVerticalSpacer
import uk.gov.govuk.design.ui.component.FixedContainerDivider
import uk.gov.govuk.design.ui.component.MediumVerticalSpacer
import uk.gov.govuk.design.ui.component.OnboardingSlide
import uk.gov.govuk.design.ui.component.PrimaryButton
import uk.gov.govuk.design.ui.theme.GovUkTheme
import uk.gov.govuk.onboarding.OnboardingPage
import uk.gov.govuk.onboarding.OnboardingViewModel
import uk.gov.govuk.onboarding.R

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
        pages.size
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
            Page(
                page = pages[pageIndex],
                isCurrentPage = pagerState.currentPage == pageIndex
            )
        }

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
            pageCount = pages.size,
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
    isCurrentPage: Boolean,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    OnboardingSlide(
        title = page.title,
        body = page.body,
        modifier = modifier,
        image = page.image,
        animation = page.animation,
        focusRequester = focusRequester
    )

    LaunchedEffect(page.title) {
        if (isCurrentPage) {
            delay(200)
            focusRequester.requestFocus()
        }
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
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FixedContainerDivider()

        MediumVerticalSpacer()

        PagerIndicator(pageCount, currentPageIndex, onPagerIndicator)

        MediumVerticalSpacer()

        val continueButtonText = stringResource(R.string.continueButton)
        val skipButtonText = stringResource(R.string.skipButton)

        if (currentPageIndex < pageCount - 1) {
            DoubleButtonGroup(
                primaryText = continueButtonText,
                onPrimary = { onContinue(continueButtonText) },
                secondaryText = skipButtonText,
                onSecondary = { onSkip(skipButtonText) }
            )
        } else {
            DoneButton(
                onClick = { text -> onDone(text) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = GovUkTheme.spacing.medium)
            )
        }

        ExtraLargeVerticalSpacer()
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
            .background(GovUkTheme.colourScheme.surfaces.switchOn)
    )
}