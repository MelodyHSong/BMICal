/*
☆
☆ Author: ☆ MelodyHSong ☆
☆ Language: Kotlin/Gradle
☆ Student ID: 843-12-0525
☆ File Name: settings.gradle.kts
☆ Date: October 5, 2025
☆ Description: Defines the repository locations for all dependencies and configures the project structure.
☆
*/

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// ☆ CRUCIAL FIX: Add dependency repositories to resolve 'Cannot resolve external dependency' errors ☆
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BMICal"
include(":app")
