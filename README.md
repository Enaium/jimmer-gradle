# jimmer-gradle

![GitHub top language](https://img.shields.io/github/languages/top/enaium/jimmer-gradle?style=flat-square&logo=kotlin)
![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/cn.enaium.jimmer.gradle?style=flat-square&logo=gradle)

Feature:

- Generate code for database table, column and association.
- Incremental compile for dto language (apt/ksp).
- implementation (spring-boot-start/sql/sql-kotlin) for dependencies.
- annotationProcessor/ksp for dependencies.
- Easy to add arguments for annotationProcessor/ksp.

## version

```kotlin
jimmer {
    version.set("0.8.122")//default latest
}
```

## generate entity

![Static Badge](https://img.shields.io/badge/-Kotlin-gray?style=flat-square&logo=kotlin&logoColor=white)
![Static Badge](https://img.shields.io/badge/-Java-gray?style=flat-square&logo=openjdk&logoColor=white)

![Static Badge](https://img.shields.io/badge/-PostgreSQL-gray?style=flat-square&logo=postgresql&logoColor=white)
![Static Badge](https://img.shields.io/badge/-MariaDB-gray?style=flat-square&logo=mariadb&logoColor=white)
![Static Badge](https://img.shields.io/badge/-MySQL-gray?style=flat-square&logo=mysql&logoColor=white)

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
    language.set(Language.KOTLIN)//Language.KOTLIN,Language.JAVA, auto detect if you don't set
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

[default](src/main/kotlin/cn/enaium/jimmer/gradle/utility/mapping.kt)

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

### ksp

```kotlin
import cn.enaium.jimmer.gradle.extension.Language

plugins {
    id("com.google.devtools.ksp")//require
}

jimmer {
    language = Language.KOTLIN
}
```

### annotationProcessor

```kotlin
import cn.enaium.jimmer.gradle.extension.Language

jimmer {
    language = Language.JAVA
}
```

## annotationProcessor/ksp arguments

```kotlin
jimmer {
    entry {
        objects.set("Drafts")//equal to -Ajimmer.entry.objects=Drafts
    }
}
```

## extension

| extension                                 | type                                                 | default                  | description                                 |
|-------------------------------------------|------------------------------------------------------|--------------------------|---------------------------------------------|
| `language`                                | `cn.enaium.jimmer.gradle.extension.Language`         | Auto detect              | Your project language.                      |
| `version`                                 | `String`                                             | `+`                      | Jimmer version.                             |
| `keepIsPrefix`                            | `Boolean`                                            | `false`                  | Keep 'is' prefix in getter method.          |
| `generator`                               | `cn.enaium.jimmer.gradle.extension.Generator`        |                          | Entity generator.                           |
| `generator.target`                        | `cn.enaium.jimmer.gradle.extension.Target`           |                          | Entity generator target.                    |
| `generator.target.srcDir`                 | `String`                                             |                          | Entity generator target src dir.            |
| `generator.target.packageName`            | `String`                                             |                          | Entity generator target package.            |
| `generator.jdbc`                          | `cn.enaium.jimmer.gradle.extension.Jdbc`             |                          | For database connection.                    |
| `generator.jdbc.driver`                   | `cn.enaium.jimmer.gradle.extension.Driver`           |                          | Database driver.                            |
| `generator.jdbc.url`                      | `String`                                             |                          | Database url.                               |
| `generator.jdbc.username`                 | `String`                                             |                          | Database username.                          |
| `generator.jdbc.password`                 | `String`                                             |                          | Database password.                          |
| `generator.table`                         | `cn.enaium.jimmer.gradle.extension.Table`            |                          | Table rule.                                 |
| `generator.table.primaryKey`              | `String`                                             | `id`                     | Table primary key name.                     |
| `generator.table.asociation`              | `cn.enaium.jimmer.gradle.extension.Association`      | `REAL`                   | Table association rule.                     |
| `generator.table.typeMappings`            | `Map<String, String>`                                | [default](#typemappings) | Column type mapping.                        |
| `generator.table.comment`                 | `Boolean`                                            | `false`                  | Generate table comment.                     |
| `generator.table.idView`                  | `Boolean`                                            | `false`                  | Generate id view.                           |
| `generator.poet`                          | `cn.enaium.jimmer.gradle.extension.Poet`             |                          | Poet rule.                                  |
| `generator.poet.indent`                   | `String`                                             | Four spaces              | Poet indent.                                |
| `client.checkedExceptions`                | `Boolean`                                            |                          |                                             |
| `client.ignoreJdkWarning`                 | `Boolean`                                            |                          | Java only.                                  |
| `dto.dirs`                                | `List<String>`                                       |                          |                                             |
| `dto.testDirs`                            | `List<String>`                                       |                          |                                             |
| `dto.mutable`                             | `Boolean`                                            |                          | Kotlin only.                                |
| `jimmer.dto.defaultNullableInputModifier` | `cn.enaium.jimmer.gradle.extension.InputDtoModifier` |                          |                                             |
| `entry`                                   | `cn.enaium.jimmer.gradle.extension.Entry`            |                          | Java only.                                  |
| `entry.objects`                           | `String`                                             |                          | Generate `objects` class name, java only.   |
| `entry.tables`                            | `String`                                             |                          | Generate `tables` class name, java only.    |
| `entry.tableExes`                         | `String`                                             |                          | Generate `tableExes` class name, java only. |
| `entry.fetchers`                          | `String`                                             |                          | Generate `fetchers` class name, java only.  |
| `source.includes`                         | `List<String>`                                       |                          | Java only.                                  |
| `source.excludes`                         | `List<String>`                                       |                          | Java only.                                  |