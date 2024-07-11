package uk.govuk.app.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingPage(
    val analyticsAlias: String,
    @StringRes val title: Int,
    @StringRes val body: Int,
    @DrawableRes val image: Int
)
