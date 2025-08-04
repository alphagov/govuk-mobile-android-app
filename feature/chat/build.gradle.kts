import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.realm)
    alias(libs.plugins.kover)
}

android {
    namespace = "uk.gov.govuk.chat"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "CHAT_BASE_URL", "\"https://chat.integration.publishing.service.gov.uk/api/v0/\"")
        buildConfigField("String", "ABOUT_APP_URL", "\"https://www.gov.uk\"")

        if (file("${rootProject.projectDir.path}/github.properties").exists()) {
            val propsFile = File("${rootProject.projectDir.path}/github.properties")
            val props = Properties().also { it.load(FileInputStream(propsFile)) }
            val chatToken = props["chatToken"] as String?
            buildConfigField("String", "CHAT_TOKEN", "\"$chatToken\"")
        } else {
            buildConfigField("String", "CHAT_TOKEN", "\"${System.getenv("CHAT_TOKEN")}\"")
        }
    }

    buildTypes {
        release {
            buildConfigField("String", "CHAT_BASE_URL", "\"https://chat.integration.publishing.service.gov.uk/api/v0/\"")
            buildConfigField("String", "CHAT_TOKEN", "\"\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.analytics)
    implementation(projects.design)
    implementation(projects.data)
    implementation(projects.config)

    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.adaptive.android)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.hilt.android)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.realm.base)

    implementation("com.github.jeziellago:compose-markdown:0.5.7")

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(kotlin("test"))
}
