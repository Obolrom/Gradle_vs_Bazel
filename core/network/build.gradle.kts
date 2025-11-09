plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    // alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.romix.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
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
    api(project(":core:model"))

    implementation(libs.androidx.core.ktx)

//    implementation(libs.retrofit)
//    implementation(libs.okhttp)
    // implementation(libs.retrofit.moshi)
    // kapt(libs.moshi.codegen)
}
