plugins {
    kotlin("jvm") version "2.4.10"
    application
}

group = "dev.kocabey"
version = "0.2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("dev.kocabey.achievements.MainKt")
    applicationName = "gh-achievements"
}

tasks.test {
    useJUnitPlatform()
}
