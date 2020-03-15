plugins {
    kotlin("jvm") version "1.3.70"
    id("application")
    id("com.github.ben-manes.versions") version "0.28.0"
}

group = "org.jraf"
version = "2.0.0"

repositories {
    mavenLocal()
    mavenCentral()
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xinline-classes")
        }
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    wrapper {
        distributionType = Wrapper.DistributionType.ALL
        gradleVersion = "6.2.2"
    }
}

application {
    mainClassName = "org.jraf.workinghour.main.MainKt"
}

val versionsCoroutine = "1.3.4"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$versionsCoroutine")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$versionsCoroutine")
    implementation("org.xerial:sqlite-jdbc:3.30.1")
    implementation("com.googlecode.log4jdbc:log4jdbc:1.2")
}
