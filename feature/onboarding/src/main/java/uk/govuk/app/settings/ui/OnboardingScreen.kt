package uk.govuk.app.settings.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun OnboardingRoute() {
    // Collect UI state from view model here and pass to screen (if necessary)
    OnboardingScreen()
}

@Composable
private fun OnboardingScreen() {
    Text("Onboarding Screen")
}