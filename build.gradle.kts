buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("org.jetbrains.kotlin.android.extensions") version "1.9.23" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("androidx.room") version "2.6.1" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false
}
