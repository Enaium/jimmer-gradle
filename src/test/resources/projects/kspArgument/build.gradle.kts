import cn.enaium.jimmer.gradle.extension.InputDtoModifier
import org.gradle.api.JavaVersion

plugins {
    kotlin("jvm") version "2.0.21"
    alias(jimmers.plugins.ksp) version "2.0.21+"
    alias(jimmers.plugins.jimmer)
}

group = "cn.enaium"
version = "0.0.1"

%{content}