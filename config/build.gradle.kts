plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

android {
    namespace = "uk.govuk.config"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "CONFIG_BASE_URL", "\"https://app.integration.publishing.service.gov.uk/config/\"")
        buildConfigField("String", "CONFIG_PUBLIC_KEY", "\"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEI9ifhn/iLdu3PwCKMhzqICSNUTivwF78Z9ybmhyIDF1Nvv+BavPyvz1XICfgEQ8g6IvHapaALXHcTszv5tFFfg==\"")
    }

    buildTypes {
        release {
            buildConfigField("String", "CONFIG_BASE_URL", "\"https://app.publishing.service.gov.uk/config/\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.hilt.android)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.scalars)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
