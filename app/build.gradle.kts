plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.apnea"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.apnea"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // JEDYNA biblioteka TensorFlow Lite
    implementation("org.tensorflow:tensorflow-lite:2.16.1")
}