import cn.enaium.jimmer.gradle.extension.InputDtoModifier

plugins {
    java
    id("cn.enaium.jimmer.gradle") version System.getProperty("jimmer-gradle")
}

group = "cn.enaium"
version = "0.0.1"

%{content}