// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    kotlin("jvm") version "2.1.10"
}

buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }
}