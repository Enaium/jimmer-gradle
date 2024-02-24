# jimmer-gradle

## generate entity

Current only support PostgreSQL and generate kotlin entity.

```kotlin
import cn.enaium.jimmer.gradle.extension.Driver

plugins {
    //...
    id("cn.enaium.jimmer-gradle") version "0.0.1"
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