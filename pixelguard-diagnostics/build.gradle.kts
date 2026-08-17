plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "io.github.abstractednick.pixelguard.diagnostics"
    compileSdk = 35
    defaultConfig { minSdk = 21 }
    buildFeatures { buildConfig = false }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
    publishing { singleVariant("release") { withSourcesJar() } }
}

dependencies {
    api(project(":pixelguard-core"))
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = property("GROUP").toString()
                artifactId = "pixelguard-diagnostics"
                version = property("VERSION_NAME").toString()
            }
        }
    }
}
