package uk.gov.govuk.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes

internal data class OnboardingPage(
    val screenName: String,
    @StringRes val title: Int,
    @StringRes val body: Int,
    @DrawableRes val image: Int? = null,
    @RawRes val animation: Int? = null
)
