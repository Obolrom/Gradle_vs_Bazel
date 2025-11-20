plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.romix.feature.feat251"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
    }

    buildFeatures {
        viewBinding = true
        // compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":core:network"))

    implementation(libs.androidx.core.ktx)

}

