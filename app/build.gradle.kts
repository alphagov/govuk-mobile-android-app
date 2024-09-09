plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.play.services)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.firebaseAppDistribution)
    alias(libs.plugins.kover)
}

android {
    namespace = "uk.govuk.app"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        applicationId = "uk.govuk.app"
        minSdk = Version.MIN_SDK
        targetSdk = Version.TARGET_SDK
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        create("alpha") {
            applicationIdSuffix = ".dev"
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            matchingFallbacks += listOf("debug")
            signingConfig = signingConfigs.getByName("debug")

            firebaseAppDistribution {
                artifactType = "APK"
                groups = "android-alpha-testers"
            }
        }

        debug {
            applicationIdSuffix = ".dev"
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.analytics)
    implementation(projects.config)
    implementation(projects.design)
    implementation(projects.feature.home)
    implementation(projects.feature.settings)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.search)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.lottie.compose)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.hilt.navigation.compose)
    androidTestImplementation(libs.hilt.android)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
