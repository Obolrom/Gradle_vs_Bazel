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
include(":feature:feat11")
include(":feature:feat12")
include(":feature:feat13")
include(":feature:feat14")
include(":feature:feat15")
include(":feature:feat16")
include(":feature:feat17")
include(":feature:feat18")
include(":feature:feat19")
include(":feature:feat20")
include(":feature:feat21")
include(":feature:feat22")
include(":feature:feat23")
include(":feature:feat24")
include(":feature:feat25")
include(":feature:feat26")
include(":feature:feat27")
include(":feature:feat28")
include(":feature:feat29")
include(":feature:feat30")
include(":feature:feat31")
include(":feature:feat32")
include(":feature:feat33")
include(":feature:feat34")
include(":feature:feat35")
include(":feature:feat36")
include(":feature:feat37")
include(":feature:feat38")
include(":feature:feat39")
include(":feature:feat40")
include(":feature:feat41")
include(":feature:feat42")
include(":feature:feat43")
include(":feature:feat44")
include(":feature:feat45")
include(":feature:feat46")
include(":feature:feat47")
include(":feature:feat48")
include(":feature:feat49")
include(":feature:feat50")

