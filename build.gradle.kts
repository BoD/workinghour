plugins {
    kotlin("multiplatform") version "1.3.72"
    id("com.github.ben-manes.versions") version "0.28.0"
    id("com.squareup.sqldelight")
}

group = "org.jraf"
version = "2.1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "6.5.1"
    }
}

val versionsCoroutine = "1.3.7"

kotlin {
    jvm()
    macosX64 {
        binaries {
            executable {}
        }
    }

    sourceSets {
        all {
            languageSettings.apply {
                enableLanguageFeature("InlineClasses")
                useExperimentalAnnotation("kotlin.time.ExperimentalTime")
                useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
                progressiveMode = true
            }
        }

        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$versionsCoroutine")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$versionsCoroutine")
//                implementation("org.xerial:sqlite-jdbc:3.32.3")
//                implementation("com.googlecode.log4jdbc:log4jdbc:1.2")
                implementation("com.squareup.sqldelight:sqlite-driver:1.4.0")
            }
        }
        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }

        macosX64().compilations["main"].defaultSourceSet {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$versionsCoroutine")
                implementation("com.squareup.sqldelight:native-driver:1.4.0")
            }
        }
//        macosX64().compilations["test"].defaultSourceSet {
//            dependencies {
//                implementation(kotlin("test"))
//            }
//        }
    }
}

sqldelight {
    database("SqlDelightDatabase") {
        packageName = "org.jraf.workinghour"
    }
}
