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

        buildConfigField("String", "AUTH_BASE_URL", "\"https://govukapp-staging.auth.eu-west-2.amazoncognito.com/oauth2/\"")
        buildConfigField("String", "TOKEN_BASE_URL", "\"https://m0q9zbtrs2.execute-api.eu-west-2.amazonaws.com/staging/oauth2/\"")
        buildConfigField("String", "AUTH_CLIENT_ID", "\"7qal023jms3dumkqd6173etleh\"")
        buildConfigField("String", "AUTHORIZE_ENDPOINT", "\"authorize\"")
        buildConfigField("String", "TOKEN_ENDPOINT", "\"token\"")
        buildConfigField("String", "AUTH_REDIRECT", "\"govuk://govuk/login-auth-callback\"")
    }

    buildTypes {
        release {
            buildConfigField("String", "AUTH_BASE_URL", "\"https://govukapp-integration.auth.eu-west-2.amazoncognito.com/oauth2/\"")
            buildConfigField("String", "TOKEN_BASE_URL", "\"https://g0s70cfqng.execute-api.eu-west-2.amazonaws.com/integration/oauth2/\"")
            buildConfigField("String", "AUTH_CLIENT_ID", "\"35vb4qv5kigloongjg4snabius\"")
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

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.appcheck.play)

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