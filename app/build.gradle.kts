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

    implementation("org.jetbrains.kotlin:kotlin-scripting-common:1.8.20-RC")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:1.8.20-RC")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies:1.8.20-RC")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven:1.8.20-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    implementation("org.jetbrains.kotlin:kotlin-scripting-common")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host")

    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.5")

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