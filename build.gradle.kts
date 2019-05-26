import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.31"
    id("kotlinx-serialization") version "1.3.31"
    `maven-publish`
}

group = "com.rnett"
version = "1.0-SNAPSHOT"

repositories {
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
    mavenCentral()
    jcenter()
    maven("https://kotlin.bintray.com/kotlinx")
}
val ktor_version = "1.2.0"

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-servlet:$ktor_version")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    api("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

val sourcesJar = tasks.create<Jar>("sourcesJar") {
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

publishing {
    publications{
        create("default", MavenPublication::class) {
            from(components["java"])
            group = "com.rnett.kframe"
            artifactId = "kframe"
            version = "1.0.0"

            artifact(sourcesJar)
        }
    }
}