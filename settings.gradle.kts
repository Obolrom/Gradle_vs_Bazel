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

rootProject.name = "Gradle_vs_Bazel"
include(":app")

include(":core:model")
include(":core:ui")
include(":core:network")

include(":feature:feat1")
include(":feature:feat2")
include(":feature:feat3")
include(":feature:feat4")
include(":feature:feat5")
include(":feature:feat6")
include(":feature:feat7")
include(":feature:feat8")
include(":feature:feat9")
include(":feature:feat10")
