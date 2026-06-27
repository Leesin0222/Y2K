pluginManagement {
    includeBuild("build-logic")
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

rootProject.name = "Y2K"
include(":app")
include(":core:designsystem")
include(":core:navigation")
include(":core:player:api")
include(":core:player:impl")
include(":core:ai:api")
include(":core:ai:impl")
include(":core:database")
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:search:api")
include(":feature:search:impl")
include(":feature:library:api")
include(":feature:library:impl")
include(":feature:equalizer:api")
include(":feature:equalizer:impl")
include(":feature:profile:api")
include(":feature:profile:impl")
include(":feature:nowplaying:api")
include(":feature:nowplaying:impl")
include(":feature:tracklist:api")
include(":feature:tracklist:impl")
include(":feature:artistdetail:api")
include(":feature:artistdetail:impl")
include(":feature:aidj:api")
include(":feature:aidj:impl")
