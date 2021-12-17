plugins {
    kotlin("jvm") version "1.6.0"
    java
}

group = "dev.acraig"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}