plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.realm)
    alias(libs.plugins.kover)
}

android {
    namespace = "uk.gov.govuk.data"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "AUTH_BASE_URL", "\"https://eu-west-2fij6f25zh.auth.eu-west-2.amazoncognito.com/oauth2/\"")
        buildConfigField("String", "AUTHORIZE_ENDPOINT", "\"authorize\"")
        buildConfigField("String", "TOKEN_ENDPOINT", "\"token\"")
        buildConfigField("String", "AUTH_CLIENT_ID", "\"121f51j1s4kmk9i98um0b5mphh\"")
        buildConfigField("String", "AUTH_REDIRECT", "\"govuk://govuk/login-auth-callback\"")
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

sonar {
    properties {
        property(
            "sonar.coverage.exclusions",
            properties["sonar.coverage.exclusions"].toString() + ",**/RealmEncryptionHelper.*,**/RealmProvider.*,**/RealmDataStore.*"
        )
        property(
            "sonar.cpd.exclusions",
            properties["sonar.cpd.exclusions"].toString() + ",**/RealmEncryptionHelper.*,**/RealmProvider.*,**/RealmDataStore.*"
        )
    }
}

dependencies {

    implementation(libs.hilt.android)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.security.crypto)

    ksp(libs.hilt.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.realm.base)
    implementation(libs.openid)

    implementation(libs.gov.securestore) {
        artifact {
            classifier = "release"
            type = "aar"
        }
    }

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}