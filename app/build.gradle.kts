plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.romix.build.gradle_vs_bazel"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.romix.build.gradle_vs_bazel"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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

    implementation(project(":feature:feat1"))
    implementation(project(":feature:feat2"))
    implementation(project(":feature:feat3"))
    implementation(project(":feature:feat4"))
    implementation(project(":feature:feat5"))
    implementation(project(":feature:feat6"))
    implementation(project(":feature:feat7"))
    implementation(project(":feature:feat8"))
    implementation(project(":feature:feat9"))
    implementation(project(":feature:feat10"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}