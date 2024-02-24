# jimmer-gradle

![GitHub top language](https://img.shields.io/github/languages/top/enaium/jimmer-gradle?style=flat-square)
![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/cn.enaium.jimmer.gradle?style=flat-square)


## generate entity

Current only support PostgreSQL and generate kotlin entity.

```kotlin
import cn.enaium.jimmer.gradle.extension.Driver

plugins {
    //...
    id("cn.enaium.jimmer.gradle") version "<version>"
}

group = "cn.enaium"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    //...
    implementation("org.postgresql:postgresql:42.6.0")
}

jimmer {
    generator {
        environment {
            srcDir.set("src/main/kotlin")
            packageName.set("cn.enaium")
        }
        jdbc {
            driver.set(Driver.POSTGRESQL)
            url.set("jdbc:postgresql://localhost:5432/postgres")
            username.set("postgres")
            password.set("postgres")
        }
        optional {
            idView.set(true)
            comment.set(true)
        }
    }
}
```