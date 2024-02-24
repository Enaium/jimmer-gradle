# jimmer-gradle

![GitHub top language](https://img.shields.io/github/languages/top/enaium/jimmer-gradle?style=flat-square&logo=kotlin)
![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/cn.enaium.jimmer.gradle?style=flat-square&logo=gradle)

## generate entity

![Static Badge](https://img.shields.io/badge/-PostgreSQL-gray?style=flat-square&logo=postgresql&logoColor=white)
![Static Badge](https://img.shields.io/badge/-MariaDB-gray?style=flat-square&logo=mariadb&logoColor=white)
![Static Badge](https://img.shields.io/badge/-MySQL-gray?style=flat-square&logo=mysql&logoColor=white)


Current only support generate kotlin entity.

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