import cn.enaium.jimmer.gradle.extension.InputDtoModifier

plugins {
    kotlin("jvm") version "1.9.23"
    id("com.google.devtools.ksp") version "1.9.23-1.0.20"
    id("cn.enaium.jimmer.gradle") version System.getProperty("jimmer-gradle")
}

group = "cn.enaium"
version = "0.0.1"

%{content}