# jimmer-gradle

![GitHub top language](https://img.shields.io/github/languages/top/enaium/jimmer-gradle?style=flat-square&logo=kotlin)
![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/cn.enaium.jimmer.gradle?style=flat-square&logo=gradle)

Feature:

- Generate code for database table and column.
- Incremental compile for dto language (apt/ksp)

## generate entity

![Static Badge](https://img.shields.io/badge/-Kotlin-gray?style=flat-square&logo=kotlin&logoColor=white)
![Static Badge](https://img.shields.io/badge/-Java-gray?style=flat-square&logo=openjdk&logoColor=white)

![Static Badge](https://img.shields.io/badge/-PostgreSQL-gray?style=flat-square&logo=postgresql&logoColor=white)
![Static Badge](https://img.shields.io/badge/-MariaDB-gray?style=flat-square&logo=mariadb&logoColor=white)
![Static Badge](https://img.shields.io/badge/-MySQL-gray?style=flat-square&logo=mysql&logoColor=white)

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
    implementation("org.postgresql:postgresql:42.6.0")//require jdbc driver
}

jimmer {
    generator {
        environment {
            language.set(Language.KOTLIN)//Language.JAVA
            srcDir.set("src/main/kotlin")
            packageName.set("cn.enaium")
        }
        jdbc {
            driver.set(Driver.POSTGRESQL)//Driver.MARIADB,Driver.MYSQL
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

## dto incremental compile

```kotlin
jimmer {
    generator {
        environment {
            language.set(Language.KOTLIN)//Language.JAVA
        }
    }
}
```

