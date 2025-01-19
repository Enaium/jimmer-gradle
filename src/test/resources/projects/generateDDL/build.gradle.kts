import cn.enaium.jimmer.gradle.extension.Driver
import cn.enaium.jimmer.gradle.extension.Language

plugins {
    java
    id("cn.enaium.jimmer.gradle") version System.getProperty("jimmer-gradle")
}

group = "cn.enaium"
version = "0.0.1"

jimmer {
    language.set(Language.%{language})
    generator {
        target {
            srcDir.set("src/main/%{language}".lowercase())
            packageName.set("cn.enaium")
        }
        jdbc {
            driver.set(Driver.%{driver})
            ddl.set(file("src/main/resources/schema.sql"))
        }
        table {
            idView.set(true)
        }
    }
}

repositories {
    mavenCentral()
}