/*
☆
☆ Author: ☆ MelodyHSong ☆
☆ Language: Kotlin/Gradle
☆ Student ID: 843-12-0525
☆ File Name: build.gradle.kts (Module: app)
☆ Date: October 6, 2025
☆ Description: Configuration for the BMI Calculator module.
☆
*/

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    // Application Info (2 ptos.)
    namespace = "com.alexis_benejan.bmical"
    compileSdk = 36 // Updated to 36 for modern dependencies

    defaultConfig {
        applicationId = "com.alexis_benejan.bmical"
        minSdk = 24
        targetSdk = 36
        versionCode = 2 // Incrementing version code
        versionName = "1.1.0" // ☆ NEW VERSION: 1.1.0 Release ☆

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
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

    // ☆ VIEW BINDING ENABLED (3 ptos.) ☆
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core AndroidX and Kotlin
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)

    // Material Design (10 ptos.)
    implementation("com.google.android.material:material:1.12.0")

    // Lifecycle (required for Activity extensions)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Lottie Animation Library for the background animation (REQUIRED)
    implementation("com.airbnb.android:lottie:6.4.1")

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
} // End of dependencies