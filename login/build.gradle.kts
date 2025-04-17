plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

android {
    namespace = "uk.gov.govuk.login"
    compileSdk = Version.COMPILE_SDK

    defaultConfig {
        minSdk = Version.MIN_SDK

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        manifestPlaceholders["appAuthRedirectScheme"] = "govuk"

        buildConfigField("String", "LOGIN_SERVICE_URL", "\"https://aulmirij8h.execute-api.eu-west-2.amazonaws.com/\"")
        buildConfigField("String", "AUTHORIZE_URL", "\"https://eu-west-2fij6f25zh.auth.eu-west-2.amazoncognito.com/oauth2/authorize\"")
        buildConfigField("String", "TOKEN_URL", "\"https://eu-west-2fij6f25zh.auth.eu-west-2.amazoncognito.com/oauth2/token\"")
        buildConfigField("String", "CLIENT_ID", "\"121f51j1s4kmk9i98um0b5mphh\"")
        buildConfigField("String", "REDIRECT_URI", "\"govuk://govuk/login-auth-callback\"")
        buildConfigField("String", "SCOPE", "\"openid\"")
    }

    buildTypes {
        release {
            buildConfigField("String", "LOGIN_SERVICE_URL", "\"https://aulmirij8h.execute-api.eu-west-2.amazonaws.com/\"")
            buildConfigField("String", "AUTHORIZE_URL", "\"https://eu-west-2fij6f25zh.auth.eu-west-2.amazoncognito.com/oauth2/authorize\"")
            buildConfigField("String", "TOKEN_URL", "\"https://eu-west-2fij6f25zh.auth.eu-west-2.amazoncognito.com/oauth2/token\"")
            buildConfigField("String", "CLIENT_ID", "\"121f51j1s4kmk9i98um0b5mphh\"")
            buildConfigField("String", "REDIRECT_URI", "\"govuk://govuk/login-auth-callback\"")
            buildConfigField("String", "SCOPE", "\"openid\"")
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
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
}

dependencies {
    implementation(projects.design)
    implementation(projects.analytics)
    implementation(projects.data)

    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.scalars)
    implementation(libs.openid)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    debugImplementation(libs.androidx.ui.tooling)
    testImplementation(kotlin("test"))
}
