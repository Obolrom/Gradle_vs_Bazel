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
    implementation(project(":feature:feat11"))
    implementation(project(":feature:feat12"))
    implementation(project(":feature:feat13"))
    implementation(project(":feature:feat14"))
    implementation(project(":feature:feat15"))
    implementation(project(":feature:feat16"))
    implementation(project(":feature:feat17"))
    implementation(project(":feature:feat18"))
    implementation(project(":feature:feat19"))
    implementation(project(":feature:feat20"))
    implementation(project(":feature:feat21"))
    implementation(project(":feature:feat22"))
    implementation(project(":feature:feat23"))
    implementation(project(":feature:feat24"))
    implementation(project(":feature:feat25"))
    implementation(project(":feature:feat26"))
    implementation(project(":feature:feat27"))
    implementation(project(":feature:feat28"))
    implementation(project(":feature:feat29"))
    implementation(project(":feature:feat30"))
    implementation(project(":feature:feat31"))
    implementation(project(":feature:feat32"))
    implementation(project(":feature:feat33"))
    implementation(project(":feature:feat34"))
    implementation(project(":feature:feat35"))
    implementation(project(":feature:feat36"))
    implementation(project(":feature:feat37"))
    implementation(project(":feature:feat38"))
    implementation(project(":feature:feat39"))
    implementation(project(":feature:feat40"))
    implementation(project(":feature:feat41"))
    implementation(project(":feature:feat42"))
    implementation(project(":feature:feat43"))
    implementation(project(":feature:feat44"))
    implementation(project(":feature:feat45"))
    implementation(project(":feature:feat46"))
    implementation(project(":feature:feat47"))
    implementation(project(":feature:feat48"))
    implementation(project(":feature:feat49"))
    implementation(project(":feature:feat50"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}