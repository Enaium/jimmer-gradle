plugins {
    kotlin("jvm") version "1.9.23"
    id("com.gradle.plugin-publish") version "1.2.1"
    `java-gradle-plugin`
    `maven-publish`
}

group = "cn.enaium"
version = "0.0.9"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.9.23-1.0.20")

    implementation("com.squareup:kotlinpoet:${property("kotlinpoet")}")
    implementation("com.squareup:javapoet:${property("javapoet")}")
    implementation("org.jetbrains:annotations:${property("jetbrainsAnnotations")}")
    implementation("org.babyfish.jimmer:jimmer-core:${property("jimmer")}")
    testImplementation("org.testcontainers:postgresql:${property("testcontainers")}")
    testImplementation("org.testcontainers:mariadb:${property("testcontainers")}")
    testImplementation("org.testcontainers:mysql:${property("testcontainers")}")
    testImplementation("org.postgresql:postgresql:${property("postgresql")}")
    testImplementation("org.mariadb.jdbc:mariadb-java-client:${property("mariadb")}")
    testImplementation("com.mysql:mysql-connector-j:${property("mysql")}")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvmToolchain(8)
}

gradlePlugin {
    website.set("https://github.com/Enaium/jimmer-gradle/")
    vcsUrl.set("https://github.com/Enaium/jimmer-gradle/")
    plugins {
        create("jimmer") {
            id = "cn.enaium.jimmer.gradle"
            implementationClass = "cn.enaium.jimmer.gradle.JimmerPlugin"
            displayName = "jimmer-gradle"
            description = "A gradle plugin for jimmer"
            tags.set(listOf("orm", "jimmer", "generator"))
        }
    }
}



publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
        }
    }
}


