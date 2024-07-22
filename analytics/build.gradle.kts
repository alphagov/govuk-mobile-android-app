plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)

}

android {
    namespace = "uk.govuk.app.analytics"
    compileSdk = 34

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.hilt.android)

    implementation(libs.gov.logging.api) {
        artifact {
            classifier = "release"
            type = "aar"
        }
    }
    implementation(libs.gov.logging.impl) {
        artifact {
            classifier = "release"
            type = "aar"
        }
    }
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)

    ksp(libs.hilt.compiler)
}