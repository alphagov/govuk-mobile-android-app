import org.gradle.api.internal.provider.MissingValueException

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
        maven("https://maven.pkg.github.com/govuk-one-login/mobile-android-logging") {
            val ghUser = providers.gradleProperty("github.username")
            val ghToken = providers.gradleProperty("github.token")

            try {
                credentials {
                    username = ghUser.get()
                    password = ghToken.get()
                }
            } catch (exception: MissingValueException) {
                credentials {
                    username = System.getenv("CI_ACCESS_USERNAME")
                    password = System.getenv("CI_ACCESS_TOKEN")
                }
            }
        }
    }
}

rootProject.name = "govuk-mobile-android-app"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":analytics")
include(":app")
include(":config")
include(":design")
include(":feature:home")
include(":feature:onboarding")
include(":feature:search")
include(":feature:settings")
include(":feature:topics")
include(":feature:visited")
