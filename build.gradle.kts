// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.play.services) apply false
    alias(libs.plugins.crashlytics) apply false
    id("org.sonarqube") version "5.0.0.4638"
}

subprojects {
    sonar {
        properties {
            property("sonar.projectName", "govuk-mobile-android-app")
            property("sonar.projectKey", "alphagov_govuk-mobile-android-app")
            property("sonar.organization", "alphagov")
            property("sonar.host.url", "https://sonarcloud.io")
            property("sonar.sourceEncoding", "UTF-8")
            property("sonar.sources", "src/main")
            property("sonar.qualitygate.wait", "true")
            property("sonar.androidLint.reportPaths", "${projectDir}/build/reports/lint-results-debug.xml")
//        property("sonar.coverage.jacoco.xmlReportPaths", "${projectDir}/build/test-results/testDebugUnitTest/TEST-*.xml")
        }
    }
}
