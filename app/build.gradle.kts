plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.eventreminder" // Changed
    compileSdk = 36 // Changed

    defaultConfig {
        applicationId = "com.example.eventreminder" // Changed
        minSdk = 24 // Changed
        targetSdk = 36 // Changed
        versionCode = 1 // Changed
        versionName = "1.0" // Changed

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // Changed
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Changed to 'isMinifyEnabled' for Kotlin
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro") // Changed
        }
        debug {
            isDebuggable = true
            // Add logging
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8 // Changed
        targetCompatibility = JavaVersion.VERSION_1_8 // Changed
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true // Changed
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity) // You have activity-ktx in your gradle file, but activity in your toml file. I've used the one in the toml file.
    // implementation("androidx.recyclerview:recyclerview:1.3.2") // This one isn't in your toml file yet, you could add it!

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
