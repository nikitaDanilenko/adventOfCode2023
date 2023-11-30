plugins {
    kotlin("jvm") version "1.9.21"
    application
}

group = "io.danilenko"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-netty:2.3.6")
    implementation("ch.qos.logback:logback-classic:1.4.7")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}