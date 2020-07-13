pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
        google()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.squareup.sqldelight") {
                useModule("com.squareup.sqldelight:gradle-plugin:1.4.0")
            }
        }
    }
}

rootProject.name = "workinghour"

enableFeaturePreview("GRADLE_METADATA")
