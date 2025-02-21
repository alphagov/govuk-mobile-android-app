plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

android {
    namespace = "uk.govuk.app.settings"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "ACCESSIBILITY_STATEMENT_EVENT", "\"AccessibilityStatement\"")
        buildConfigField("String", "ACCESSIBILITY_STATEMENT_URL", "\"https://www.gov.uk/government/publications/accessibility-statement-for-the-govuk-app\"")

        buildConfigField("String", "HELP_AND_FEEDBACK_EVENT", "\"HelpAndFeedback\"")
        buildConfigField("String", "HELP_AND_FEEDBACK_URL", "\"https://www.gov.uk/contact/govuk-app\"")

        buildConfigField("String", "OPEN_SOURCE_LICENCE_EVENT", "\"OpenSourceLicenses\"")

        buildConfigField("String", "NOTIFICATIONS_PERMISSION_EVENT", "\"NotificationsPermission\"")

        buildConfigField("String", "PRIVACY_POLICY_EVENT", "\"PrivacyPolicy\"")
        buildConfigField("String", "PRIVACY_POLICY_URL", "\"https://www.gov.uk/government/publications/govuk-app-privacy-notice-how-we-use-your-data\"")

        buildConfigField("String", "TERMS_AND_CONDITIONS_EVENT", "\"TermsAndConditions\"")
        buildConfigField("String", "TERMS_AND_CONDITIONS_URL", "\"http://www.gov.uk/government/publications/govuk-app-terms-and-conditions\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.analytics)
    implementation(projects.design)
    implementation(projects.config)
    implementation(projects.notifications)

    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.play.services.oss.licenses)
    implementation(libs.play.services.measurement.api)
    implementation(libs.androidx.ui.tooling)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
