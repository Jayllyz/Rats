plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    kotlin("plugin.serialization") version "2.1.10"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
}

secrets {
    propertiesFileName = "secrets.properties"
    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"
    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*") // Ignore all keys matching the regexp "sdk.*"
}

android {
    namespace = "com.rats"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.rats"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

ktlint {
    android = true
    ignoreFailures = false
}

// Task to run both lint and ktlintFormat
tasks.register("formatAndLint") {
    dependsOn("ktlintFormat", "lint")
    group = "formatting"
    description = "Run both ktlintFormat and lint checks"
}

// Task to run ktlintFormat
tasks.register("format") {
    dependsOn("ktlintFormat")
    group = "formatting"
    description = "Run ktlint formatter"
}

// Task to run lint
tasks.register("lintCheck") {
    dependsOn("lint")
    group = "verification"
    description = "Run Android lint"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.play.services.maps)
    implementation(libs.places)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
