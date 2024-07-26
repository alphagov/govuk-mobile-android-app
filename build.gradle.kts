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
    if (!projectDir.endsWith("feature")) {
        apply(plugin = "org.sonarqube")
        sonar {
            properties {
                property("sonar.sources", "src/main")
                property("sonar.tests", "src/test,src/androidTest")
                property(
                    "sonar.coverage.jacoco.xmlReportPaths",
                    "${projectDir}/build/reports/kover/reportDebug.xml"
                )
                property(
                    "sonar.coverage.exclusions",
                    "**/di/**/*.*,**/ui/**/*.*, **/navigation/**/*.*,**/*Activity.*,**/*Application.*"
                )
                property(
                    "sonar.androidLint.reportPaths",
                    "${projectDir}/build/reports/lint-results-debug.xml"
                )
            }
        }
    }
}

apply(from = "${project.rootDir}/sonar.gradle")
