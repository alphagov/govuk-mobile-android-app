pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "govuk-mobile-android-app"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":analytics")
include(":app")
include(":config")
include(":design")
include(":feature:home")
include(":feature:local")
include(":feature:onboarding")
include(":feature:search")
include(":feature:settings")
include(":feature:topics")
include(":feature:visited")
include(":notifications")
include(":data")
