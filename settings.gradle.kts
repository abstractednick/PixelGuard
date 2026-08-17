pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PixelGuard"
include(
    ":pixelguard-core",
    ":pixelguard-diagnostics",
    ":pixelguard-coil",
    ":pixelguard-glide",
    ":sample",
)
