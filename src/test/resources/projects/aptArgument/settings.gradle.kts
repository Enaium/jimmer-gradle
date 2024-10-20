pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("cn.enaium.jimmer.gradle.setting") version System.getProperty("jimmer-gradle")
}