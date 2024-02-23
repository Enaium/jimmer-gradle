import cn.enaium.jimmer.gradle.extension.Driver

plugins {
    java
    id("cn.enaium.jimmer.gradle")
}

group = "cn.enaium"
version = "0.0.1"

jimmer {
    generator {
        environment {
            srcDir.set("src/main/kotlin")
            packageName.set("cn.enaium")
        }
        jdbc {
            driver.set(Driver.${driver})
            url.set("${url}")
            username.set("${username}")
            password.set("${password}")
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    runtimeOnly("${driverDependency}")
}