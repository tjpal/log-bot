/*
    Copyright (c) 2023 Thomas P.
    Use of this source code is governed by the MIT license that can be found in the project root directory.
*/
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.10"

    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:31.1-jre")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("loganalyzerbot.AppKt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}