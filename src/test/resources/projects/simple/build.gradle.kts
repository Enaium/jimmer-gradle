import cn.enaium.jimmer.gradle.extension.Driver
import cn.enaium.jimmer.gradle.extension.Language

plugins {
    java
    id("cn.enaium.jimmer.gradle")
}

group = "cn.enaium"
version = "0.0.1"

jimmer {
    generator {
        environment {
            srcDir.set("src/main/${language}".lowercase())
            packageName.set("cn.enaium")
            language.set(Language.${language})
        }
        jdbc {
            driver.set(Driver.${driver})
            url.set("${url}")
            username.set("${username}")
            password.set("${password}")
        }
        optional {
            idView.set(true)
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    runtimeOnly("${driverDependency}")
}