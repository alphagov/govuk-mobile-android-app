plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.android.gms.oss-licenses-plugin")
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
    namespace = "uk.gov.govuk"
    compileSdk = Version.COMPILE_SDK

    // Todo - replace with Google Play auto increment mechanism for play store builds
    val buildNumber = System.getenv("GITHUB_RUN_NUMBER")?.toInt() ?: 1

    defaultConfig {
        applicationId = "uk.gov.govuk"
        minSdk = Version.MIN_SDK
        targetSdk = Version.TARGET_SDK
        versionCode = buildNumber
        versionName = "0.0.$buildNumber"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "PLAY_STORE_URL", "\"https://play.google.com/store/apps/details?id=$applicationId\"")
        buildConfigField("String", "ONE_SIGNAL_APP_ID", "\"4c235189-5c5f-4a71-8385-2549fc36419f\"")
    }

    signingConfigs {
        create("alpha") {
            storeFile = file("${project.rootDir}/alpha.jks")
            storePassword = System.getenv("ALPHA_KEY_PASSWORD")
            keyAlias = System.getenv("ALPHA_KEY_ALIAS")
            keyPassword = System.getenv("ALPHA_KEY_PASSWORD")
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

            signingConfig = if (System.getenv("ALPHA_KEYSTORE") != null) {
                println("Signing with Alpha Keystore!!!")
                signingConfigs.getByName("alpha")
            } else {
                signingConfigs.getByName("debug")
            }

            firebaseAppDistribution {
                artifactType = "APK"
                releaseNotesFile = "${project.rootDir}/releasenotes.txt"
                groups = "android-alpha-testers"
            }
        }

        debug {
            applicationIdSuffix = ".dev"
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }

        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = if (System.getenv("RELEASE_KEYSTORE") != null) {
                println("Signing with Release Keystore!!!")
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }

            buildConfigField("String", "ONE_SIGNAL_APP_ID", "\"bbea84fc-28cc-4712-a6c5-88f5d08b0d0d\"")
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
    implementation(projects.biometrics)
    implementation(projects.config)
    implementation(projects.design)
    implementation(projects.data)
    implementation(projects.feature.home)
    implementation(projects.feature.onboarding)
    implementation(projects.feature.settings)
    implementation(projects.feature.search)
    implementation(projects.feature.topics)
    implementation(projects.feature.visited)
    implementation(projects.notifications)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.hilt.android)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.lottie.compose)
    implementation(libs.play.services.oss.licenses)

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

    debugImplementation(libs.androidx.ui.test.manifest)
}
