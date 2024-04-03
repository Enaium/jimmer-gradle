# jimmer-gradle

![GitHub top language](https://img.shields.io/github/languages/top/enaium/jimmer-gradle?style=flat-square&logo=kotlin)
![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/cn.enaium.jimmer.gradle?style=flat-square&logo=gradle)

Feature:

- Generate code for database table, column and association.
- Incremental compile for dto language (apt/ksp).
- implementation (spring-boot-start/sql/sql-kotlin) for dependencies.
- annotationProcessor/ksp for dependencies.

## version

```kotlin
jimmer {
    version.set("0.8.107")//default latest
}
```

## generate entity

![Static Badge](https://img.shields.io/badge/-Kotlin-gray?style=flat-square&logo=kotlin&logoColor=white)
![Static Badge](https://img.shields.io/badge/-Java-gray?style=flat-square&logo=openjdk&logoColor=white)

![Static Badge](https://img.shields.io/badge/-PostgreSQL-gray?style=flat-square&logo=postgresql&logoColor=white)
![Static Badge](https://img.shields.io/badge/-MariaDB-gray?style=flat-square&logo=mariadb&logoColor=white)
![Static Badge](https://img.shields.io/badge/-MySQL-gray?style=flat-square&logo=mysql&logoColor=white)

### full config

```kotlin

import cn.enaium.jimmer.gradle.extension.Association
import cn.enaium.jimmer.gradle.extension.Driver
import cn.enaium.jimmer.gradle.extension.Language

plugins {
    //...
    id("cn.enaium.jimmer.gradle") version "<version>"
}

dependencies {
    //...
    implementation("org.postgresql:postgresql:42.6.0")//require jdbc driver
}

jimmer {
    language.set(Language.KOTLIN)//Language.KOTLIN,Language.JAVA, no default, so you must set it
    generator {
        target {
            srcDir.set("src/main/kotlin")
            packageName.set("cn.enaium")
        }
        jdbc {
            driver.set(Driver.POSTGRESQL)//Driver.POSTGRESQL,Driver.MARIADB,Driver.MYSQL, no default
            url.set("jdbc:postgresql://localhost:5432/postgres")
            username.set("postgres")
            password.set("postgres")
        }
        table {
            idView.set(true)
            comment.set(true)
            primaryKey.set("id")//default id
            association.set(Association.REAL)//Association.REAL,Association.FAKE,Association.NO, default Association.REAL
            typeMappings.set(
                mapOf(
                    "float8" to "kotlin.Float",
                )
            )
        }
    }
}
```

### association

You must add the suffix '_id'(`primaryKey.set("id")`) to the column name if the column is a fake foreign key, otherwise
the column cannot be recognized as a foreign key.

If you want to use the fake foreign key, you need to set the `association.set(Association.FAKE)`, otherwise the default
is `association.set(Association.REAL)`, of course you can also set `association.set(Association.NO)` to disable the
association.

**Warning: You can't use the fake association if you included the real association in the database.**

```kotlin
jimmer {
    generator {
        table {
            primaryKey.set("id")
            association.set(Association.REAL)
        }
    }
}
```

### typeMappings

You can customize the type mapping, the default is as follows:

```kotlin
jimmer {
    generator {
        table {
            typeMappings.set(
                mapOf(
                    "float8" to "kotlin.Float",//Java: "java.lang.Float"
                )
            )
        }
    }
}
```

## dto incremental compile

```kotlin
import cn.enaium.jimmer.gradle.extension.Language

jimmer {
    language.set(Language.KOTLIN)//Language.JAVA
}
```

## implementation for dependencies

### spring-boot-start

```kotlin
import cn.enaium.jimmer.gradle.extension.Language

plugins {
    id("org.springframework.boot")//require
}

jimmer {
    language = Language.KOTLIN//Language.JAVA
}
```

### sql-kotlin

```kotlin
import cn.enaium.jimmer.gradle.extension.Language

jimmer {
    language = Language.KOTLIN
}
```

### sql

```kotlin
import cn.enaium.jimmer.gradle.extension.Language

jimmer {
    language = Language.JAVA
}
```

## annotationProcessor/ksp for dependencies

## ksp

```kotlin
import cn.enaium.jimmer.gradle.extension.Language

plugins {
    id("com.google.devtools.ksp")//require
}

jimmer {
    language = Language.KOTLIN
}
```

## annotationProcessor

```kotlin
import cn.enaium.jimmer.gradle.extension.Language

jimmer {
    language = Language.JAVA
}
```