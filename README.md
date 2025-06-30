# jimmer-gradle

![GitHub top language](https://img.shields.io/github/languages/top/enaium/jimmer-gradle?style=flat-square&logo=kotlin)
![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/cn.enaium.jimmer.gradle?style=flat-square&logo=gradle)

## Feature

- Generate code for table, column and association from database or ddl.
- Incremental compile for dto language (apt/ksp).
- Add implementation (spring-boot-start/sql/sql-kotlin) for dependencies.
- Add annotationProcessor/ksp for dependencies.
- Easy to add arguments for annotationProcessor/ksp.
- Can use jimmer's catalog in the project.
- Let jimmer generate code when opening the project for the first time.

## Usage(Recommended Method 2)

Warning: You cannot use the method 1 and method 2 at the same time.

### Gradle Project Plugin(Method 1)

In the `build.gradle.kts` file, you can use the following code to apply the plugin.

```kotlin
plugins {
    id("cn.enaium.jimmer.gradle") version "latest.release"
}
```

If you also used kotlin, you need to declare the ksp plugin before the jimmer plugin.

```kotlin
plugins {
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21+"
    id("cn.enaium.jimmer.gradle") version "latest.release"
}
```

### Gradle Settings Plugin(Method 2)

In the `settings.gradle.kts` file, you can use the following code to apply the plugin.

```kotlin
plugins {
    id("cn.enaium.jimmer.gradle.setting") version "latest.release"
}
```

If you want to modify the extension of the gradle project plugin, then you can use the gradle project plugin.

```kotlin
plugins {
    alias(jimmers.plugins.jimmer)
}
```

If you also used kotlin, you need to declare the ksp plugin before the jimmer plugin.

```kotlin
plugins {
    kotlin("jvm") version "2.0.21"
    alias(jimmers.plugins.ksp) version "2.0.21+"
    alias(jimmers.plugins.jimmer)
}
```

then you need to add the ksp dependency in the `build.gradle.kts` file.

```kotlin
dependencies {
    ksp(jimmers.ksp)
}
```

## Jimmer's Version(Both Gradle Project Plugin and Gradle Settings Plugin)

Warning: The version of the extension is not supported for `ksp` when
issue [#1789](https://github.com/google/ksp/issues/1789) isn't fixed, but you can use the latest version or use the
gradle setting
plugin.

In the `build.gradle.kts` file if you use the gradle project plugin, or in the `settings.gradle.kts` file if you use the
gradle settings plugin, you can use the following code to set the version of jimmer.

```kotlin
jimmer {
    version.set("latest.release")//default latest
}
```

## Generate Entity(Only Gradle Project Plugin)

![Static Badge](https://img.shields.io/badge/-Kotlin-gray?style=flat-square&logo=kotlin&logoColor=white)
![Static Badge](https://img.shields.io/badge/-Java-gray?style=flat-square&logo=openjdk&logoColor=white)

![Static Badge](https://img.shields.io/badge/-PostgreSQL-gray?style=flat-square&logo=postgresql&logoColor=white)
![Static Badge](https://img.shields.io/badge/-MariaDB-gray?style=flat-square&logo=mariadb&logoColor=white)
![Static Badge](https://img.shields.io/badge/-MySQL-gray?style=flat-square&logo=mysql&logoColor=white)

### From Database

```kotlin
import cn.enaium.jimmer.gradle.extension.Association
import cn.enaium.jimmer.gradle.extension.Driver

plugins {
    //...
    id("cn.enaium.jimmer.gradle") version "<version>"
}

dependencies {
    //...
    runtimeOnly("org.postgresql:postgresql:42.6.0")//require jdbc driver
}

jimmer {
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
//            catalog.set("postgres")
//            schemaPattern.set("public")
//            tableNamePattern.set("t_%")
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

### From DDL

```kotlin
jimmer {
    generator {
        target {
            srcDir.set("src/main/kotlin")
            packageName.set("cn.enaium")
        }
        jdbc {
            ddl.set(file("src/main/resources/schema.sql"))
        }
    }
}
```

### Association(Only Gradle Project Plugin)

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

### typeMappings(Only Gradle Project Plugin)

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

## Dto Incremental Compile

Open the dto file and press `Ctrl + F9` to compile the dto file.

## implementation for dependencies

### spring-boot-start

```kotlin
plugins {
    id("org.springframework.boot")//require
}
```

It will automatically add or use the catalog of jimmer if you use the gradle settings plugin.

```kotlin
dependencies {
    implementation(jimmers.springBootStart)
}
```

### sql-kotlin

It will automatically add or use the catalog of jimmer if you use the gradle settings plugin.

```kotlin
dependencies {
    implementation(jimmers.sqlKotlin)
}
```

### sql

It will automatically add or use the catalog of jimmer if you use the gradle settings plugin.

```kotlin
dependencies {
    implementation(jimmers.sql)
}
```

## annotationProcessor/ksp for dependencies

### ksp

It will automatically add or use the catalog of jimmer if you use the gradle settings plugin.

```kotlin
plugins {
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21+" //require and must declare before jimmer gradle plugin
}
```

```kotlin
plugins {
    kotlin("jvm") version "2.0.21"
    alias(jimmers.plugins.ksp) version "2.0.21+"
}
```

```kotlin
dependencies {
    ksp(jimmers.ksp)
}
```

### annotationProcessor

It will automatically add or use the catalog of jimmer if you use the gradle settings plugin.

```kotlin
dependencies {
    annotationProcessor(jimmers.apt)
}
```

## annotationProcessor/ksp arguments(Only Gradle Project Plugin)

```kotlin
jimmer {
    entry {
        objects.set("Drafts")//equal to -Ajimmer.entry.objects=Drafts
    }
}
```

## Patch

```kotlin
jimmer {
    patch {
        enable = true
    }
}

dependencies {
    patch(jimmers.sqlKotlin) {
        exclude(module = "kotlin-stdlib")
        exclude(module = "annotations")
        exclude(module = "validation-api")
    }
    patchKsp(jimmers.ksp)
    ksp(files(configurations.patchKsp.get()))
}
```

## Gradle Project Plugin Extension

| extension                           | type                                                 | default          | description                                                                        |
|-------------------------------------|------------------------------------------------------|------------------|------------------------------------------------------------------------------------|
| `version`                           | `String`                                             | `latest.release` | Jimmer version.                                                                    |
| `keepIsPrefix`                      | `Boolean`                                            | `false`          | Keep 'is' prefix in getter method.                                                 |
| `autoImplDepend`                    | `Boolean`                                            | `false`          | Add jimmer-sql-kotlin or jimmer-sql dependencies automatically.                    |
| `generator`                         | `cn.enaium.jimmer.gradle.extension.Generator`        |                  | Entity generator.                                                                  |
| `generator.target`                  | `cn.enaium.jimmer.gradle.extension.Target`           |                  | Entity generator target.                                                           |
| `generator.target.srcDir`           | `String`                                             |                  | Entity generator target src dir.                                                   |
| `generator.target.packageName`      | `String`                                             |                  | Entity generator target package.                                                   |
| `generator.jdbc`                    | `cn.enaium.jimmer.gradle.extension.Jdbc`             |                  | For database connection.                                                           |
| `generator.jdbc.driver`             | `cn.enaium.jimmer.gradle.extension.Driver`           |                  | Database driver.                                                                   |
| `generator.jdbc.url`                | `String`                                             |                  | Database url.                                                                      |
| `generator.jdbc.username`           | `String`                                             |                  | Database username.                                                                 |
| `generator.jdbc.password`           | `String`                                             |                  | Database password.                                                                 |
| `generator.jdbc.ddl`                | `File`                                               |                  | DDL sql file.                                                                      |
| `generator.jdbc.catalog`            | `String`                                             |                  | Database catalog.                                                                  |
| `generator.jdbc.schemaPattern`      | `String`                                             |                  | Database schema pattern.                                                           |
| `generator.jdbc.tableNamePattern`   | `String`                                             |                  | Database table name pattern.                                                       |
| `generator.table`                   | `cn.enaium.jimmer.gradle.extension.Table`            |                  | Table rule.                                                                        |
| `generator.table.name`              | `Boolean`                                            | `false`          | Add table annotation.                                                              |
| `generator.table.column`            | `Boolean`                                            | `false`          | Add column annotation.                                                             |
| `generator.table.primaryKey`        | `String`                                             | `id`             | Table primary key name.                                                            |
| `generator.table.asociation`        | `cn.enaium.jimmer.gradle.extension.Association`      | `REAL`           | Table association rule.                                                            |
| `generator.table.typeMappings`      | `Map<String, String>`                                |                  | Column type mapping.                                                               |
| `generator.table.comment`           | `Boolean`                                            | `false`          | Generate table comment.                                                            |
| `generator.table.idView`            | `Boolean`                                            | `false`          | Generate id view annotation.                                                       |
| `generator.table.joinTable`         | `Boolean`                                            | `false`          | Generate join table annotation.                                                    |
| `generator.table.idGeneratorType`   | `String`                                             |                  | Generate generated value annotation that has generator type.                       |
| `generator.poet`                    | `cn.enaium.jimmer.gradle.extension.Poet`             |                  | Poet rule.                                                                         |
| `generator.poet.indent`             | `String`                                             | Four spaces      | Poet indent.                                                                       |
| `client.checkedExceptions`          | `Boolean`                                            |                  |                                                                                    |
| `client.ignoreJdkWarning`           | `Boolean`                                            |                  | Java only.                                                                         |
| `dto.dirs`                          | `List<String>`                                       |                  |                                                                                    |
| `dto.testDirs`                      | `List<String>`                                       |                  |                                                                                    |
| `dto.mutable`                       | `Boolean`                                            |                  | Kotlin only.                                                                       |
| `dto.defaultNullableInputModifier`  | `cn.enaium.jimmer.gradle.extension.InputDtoModifier` |                  |                                                                                    |
| `dto.hibernateValidatorEnhancement` | `Boolean`                                            |                  | Java only.                                                                         |
| `entry`                             | `cn.enaium.jimmer.gradle.extension.Entry`            |                  | Java only.                                                                         |
| `entry.objects`                     | `String`                                             |                  | Generate `objects` class name, java only.                                          |
| `entry.tables`                      | `String`                                             |                  | Generate `tables` class name, java only.                                           |
| `entry.tableExes`                   | `String`                                             |                  | Generate `tableExes` class name, java only.                                        |
| `entry.fetchers`                    | `String`                                             |                  | Generate `fetchers` class name, java only.                                         |
| `immutable.isModuleRequired`        | `Boolean`                                            |                  | Kotlin only.                                                                       |
| `source.includes`                   | `List<String>`                                       |                  | Java only.                                                                         |
| `source.excludes`                   | `List<String>`                                       |                  | Java only.                                                                         |
| `patch.enable`                      | `Boolean`                                            | `false`          | Provide a patch that makes some dependencies of the Jimmer compatible with Android |

## Gradle Settings Plugin Extension

| extension | type     | default          | description     |
|-----------|----------|------------------|-----------------|
| `version` | `String` | `latest.release` | Jimmer version. |