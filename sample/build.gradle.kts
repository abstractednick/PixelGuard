plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.abstractednick.pixelguard.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.abstractednick.pixelguard.sample"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }

    lint {
        abortOnError = true
        warningsAsErrors = false
        disable += setOf("MissingApplicationIcon", "GradleDependency")
    }
}

dependencies {
    implementation(project(":pixelguard-core"))
    implementation(project(":pixelguard-diagnostics"))
    implementation(project(":pixelguard-coil"))
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
}
