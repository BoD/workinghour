plugins {
    kotlin("multiplatform") version "1.3.72"
    id("com.github.ben-manes.versions") version "0.28.0"
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

    sourceSets {
        all {
            languageSettings.enableLanguageFeature("InlineClasses")
            languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
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

        sourceSets["jvmMain"].dependencies {
            implementation(kotlin("stdlib-jdk8"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$versionsCoroutine")
            implementation("org.xerial:sqlite-jdbc:3.32.3")
            implementation("com.googlecode.log4jdbc:log4jdbc:1.2")
        }
        sourceSets["jvmTest"].dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-junit"))
        }
    }
}
