/*
☆
☆ Author: ☆ MelodyHSong ☆
☆ Language: Kotlin/Gradle
☆ Student ID: 843-12-0525
☆ File Name: build.gradle.kts (Project Level)
☆ Date: October 5, 2025
☆ Description: Defines the plugins for the entire project hierarchy using standard IDs.
☆
*/

// NOTE: This uses standard ID syntax to resolve the Unresolved reference errors. I HATE THOSE ERRORS!!!

plugins {
    // Android application plugin definition
    id("com.android.application") version "8.12.3" apply false

    // Kotlin plugins definitions
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}

// NOTE: All dependencies are configured at the module level (:app)
// This file can safely be empty aside from the plugins block, as recommended by Android Studio.


//End of File