plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gradle.publish)
    `java-gradle-plugin`
    `maven-publish`
}

group = "cn.enaium"
version = "${properties["version"]}"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(libs.kotlinpoet)
    implementation(libs.javapoet)
    implementation(libs.jspecify)
    implementation(libs.jimmer)
    implementation(libs.jimmer.ksp)
    implementation(libs.jimmer.dto.compiler)
    implementation(libs.jackson)
    implementation(libs.h2)

    implementation(libs.asm)
    implementation(libs.asm.tree)

    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.mariadb)
    testImplementation(libs.testcontainers.mysql)
    testImplementation(libs.postgresql)
    testImplementation(libs.mariadb)
    testImplementation(libs.mysql)
    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-D${rootProject.name}=${project.version}")
    dependsOn(tasks.publishToMavenLocal)
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
        create("setting") {
            id = "cn.enaium.jimmer.gradle.setting"
            implementationClass = "cn.enaium.jimmer.gradle.SettingPlugin"
            displayName = "setting"
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


