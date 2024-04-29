import java.util.Locale

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    jacoco
    id("org.sonarqube") version "4.4.1.3373"
}

android {
    namespace = "uk.govuk.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "uk.govuk.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            // enableAndroidTestCoverage = true
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
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // testOptions {
    //     managedDevices {
    //         localDevices {
    //             create("pixel3api33") {
    //                 device = "Pixel 3"
    //                 apiLevel = 33
    //                 systemImageSource = "aosp"
    //             }
    //             create("pixelCapi29") {
    //                 device = "Pixel C"
    //                 apiLevel = 29
    //                 systemImageSource = "aosp"
    //             }
    //         }
    //         groups {
    //             create("phoneAndTablet") {
    //                 targetDevices.add(devices["pixel3api33"])
    //                 targetDevices.add(devices["pixelCapi29"])
    //             }
    //         }
    //     }
    // }
}

sonar {
    properties {
        property("sonar.projectName", "govuk-mobile-android-app")
        property("sonar.projectKey", "alphagov_govuk-mobile-android-app")
        property("sonar.organization", "alphagov")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.androidLint.reportPaths", layout.buildDirectory.dir("reports/lint-results-debug.xml"))
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

jacoco {
    toolVersion = "0.8.11"
}

val exclusions =
    listOf(
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*"
    )

tasks.withType(Test::class) {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

android {
    applicationVariants.all(
        closureOf<com.android.build.gradle.internal.api.BaseVariantImpl> {
            val variant =
                this@closureOf.name.replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(
                            Locale.getDefault()
                        )
                    } else {
                        it.toString()
                    }
                }

            val unitTests = "test${variant}UnitTest"
            // val androidTests = "connected${variant}AndroidTest"

            tasks.register<JacocoReport>("Jacoco${variant}CodeCoverage") {
                // dependsOn(listOf(unitTests, androidTests))
                dependsOn(listOf(unitTests))
                group = "Reporting"
                description = "Jacoco coverage report"
                reports {
                    xml.required.set(true)
                    html.required.set(true)
                }
                sourceDirectories.setFrom(layout.projectDirectory.dir("src/main"))
                classDirectories.setFrom(
                    files(
                        fileTree(layout.buildDirectory.dir("intermediates/javac/")) {
                            exclude(exclusions)
                        },
                        fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/")) {
                            exclude(exclusions)
                        }
                    )
                )
                executionData.setFrom(
                    files(
                        fileTree(layout.buildDirectory) { include(listOf("**/*.exec", "**/*.ec")) }
                    )
                )
            }
        }
    )
}
