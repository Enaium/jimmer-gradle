# jimmer-gradle

![GitHub top language](https://img.shields.io/github/languages/top/enaium/jimmer-gradle?style=flat-square&logo=kotlin)
![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/cn.enaium.jimmer.gradle?style=flat-square&logo=gradle)

A Gradle plugin for [Jimmer](https://github.com/babyfish-ct/jimmer) that generates code from your database or DDL,
supports incremental compilation, and simplifies dependency management for both Java and Kotlin projects.

---

## Table of Contents

- [Features](#features)
- [Quick Start](#quick-start)
    - [Method 1: Gradle Project Plugin](#method-1-gradle-project-plugin)
    - [Method 2: Gradle Settings Plugin (Recommended)](#method-2-gradle-settings-plugin-recommended)
- [Jimmer Version Configuration](#jimmer-version-configuration)
- [Entity Generation](#entity-generation)
    - [From Database](#from-database)
    - [From DDL](#from-ddl)
    - [Association Handling](#association-handling)
    - [Type Mappings](#type-mappings)
- [Incremental DTO Compilation](#incremental-dto-compilation)
- [Dependency Management](#dependency-management)
- [Annotation Processor / KSP Arguments](#annotation-processor--ksp-arguments)
- [Patch Support](#patch-support)
- [Extension Reference](#extension-reference)

---

## Features

- Generate code for tables, columns, and associations from a database or DDL.
- Incremental compilation for DTOs (APT/KSP).
- Automatic dependency management (Spring Boot, SQL, SQL-Kotlin).
- Easy configuration for annotationProcessor/KSP arguments.
- Use Jimmer's catalog in your project.
- Auto-generate code on first project open.

---

## Quick Start

> **Note:** Do not use Method 1 and Method 2 at the same time.

### Method 1: Gradle Project Plugin

Add to your `build.gradle.kts`:

```kotlin
plugins {
    id("cn.enaium.jimmer.gradle") version "latest.release"
}
```

If using Kotlin, declare the KSP plugin before the Jimmer plugin:

```kotlin
plugins {
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21+"
    id("cn.enaium.jimmer.gradle") version "latest.release"// Must declare after the ksp plugin
}
```

Enable auto dependency implementation:

```kotlin
jimmer {
    autoImplDepend = true
}

dependencies {
    ksp("org.babyfish.jimmer:jimmer-ksp:x.x.x")// Not auto
}
```

---

### Method 2: Gradle Settings Plugin (Recommended)

Add to your `settings.gradle.kts`:

```kotlin
plugins {
    id("cn.enaium.jimmer.gradle.setting") version "latest.release"
}
```

To modify the extension, use the project plugin as well:

```kotlin
plugins {
    alias(jimmers.plugins.jimmer)
}
```

If using Kotlin, declare the KSP plugin before the Jimmer plugin:

```kotlin
plugins {
    kotlin("jvm") version "2.0.21"
    alias(jimmers.plugins.ksp) version "2.0.21+"
    alias(jimmers.plugins.jimmer)// Must declare after the ksp plugin
}
```

Add the KSP dependency in `build.gradle.kts`:

```kotlin
dependencies {
    ksp(jimmers.ksp)
}
```

---

## Jimmer Version Configuration

Set the Jimmer version in your `build.gradle.kts` (for project plugin) or `settings.gradle.kts` (for settings plugin):

```kotlin
jimmer {
    version.set("latest.release") // default is latest
}
```

---

## Entity Generation

### From Database

```kotlin
import cn.enaium.jimmer.gradle.extension.Association
import cn.enaium.jimmer.gradle.extension.Driver

plugins {
    // ...
    id("cn.enaium.jimmer.gradle") version "<version>"
}

dependencies {
    // ...
    runtimeOnly("org.postgresql:postgresql:42.6.0") // JDBC driver required
}

jimmer {
    generator {
        target {
            srcDir.set("src/main/kotlin")
            packageName.set("cn.enaium")
        }
        jdbc {
            driver.set(Driver.POSTGRESQL) // POSTGRESQL, MARIADB, MYSQL
            url.set("jdbc:postgresql://localhost:5432/postgres")
            username.set("postgres")
            password.set("postgres")
            // Optional: catalog, schemaPattern, tableNamePattern
        }
        table {
            idView.set(true)
            comment.set(true)
            primaryKey.set("id") // default is "id"
            association.set(Association.REAL) // REAL, FAKE, NO
            typeMappings.set(mapOf("float8" to "kotlin.Float"))
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

### Association Handling

- Use the `_id` suffix for fake foreign keys (e.g., `user_id`).
- Set `association.set(Association.FAKE)` for fake associations, `Association.REAL` for real, or `Association.NO` to
  disable.
- **Warning:** Do not mix fake and real associations in the same database.

### Type Mappings

Default mappings are in [`mapping.kt`](src/main/kotlin/cn/enaium/jimmer/gradle/utility/mapping.kt). You can override
them:

```kotlin
jimmer {
    generator {
        table {
            typeMappings.set(mapOf("float8" to "kotlin.Float")) // Java: "java.lang.Float"
        }
    }
}
```

---

## Incremental DTO Compilation

Open a DTO file and press `Ctrl + F9` to compile it incrementally.

---

## Dependency Management

### Spring Boot Starter

```kotlin
plugins {
    id("org.springframework.boot") // required
}

dependencies {
    implementation(jimmers.springBootStart)
}
```

### SQL-Kotlin

```kotlin
dependencies {
    implementation(jimmers.sqlKotlin)
}
```

### SQL

```kotlin
dependencies {
    implementation(jimmers.sql)
}
```

---

## Annotation Processor / KSP Arguments

### KSP

```kotlin
plugins {
    kotlin("jvm") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21+" // must be before jimmer plugin
}

dependencies {
    ksp(jimmers.ksp)
}
```

### Annotation Processor

```kotlin
dependencies {
    annotationProcessor(jimmers.apt)
}
```

### Arguments (Project Plugin Only)

```kotlin
jimmer {
    entry {
        objects.set("Drafts") // equals -Ajimmer.entry.objects=Drafts
    }
}
```

---

## Patch Support

```kotlin
jimmer {
    patch {
        enable = true
    }
}

dependencies {
    patch(jimmers.sqlKotlin)
    patchKsp(jimmers.ksp)
}
```

---

## Extension Reference

### Gradle Project Plugin Extension

| Extension                           | Type                  | Default          | Description                     |
|-------------------------------------|-----------------------|------------------|---------------------------------|
| `version`                           | `String`              | `latest.release` | Jimmer version                  |
| `keepIsPrefix`                      | `Boolean`             | `false`          | Keep 'is' prefix in getter      |
| `autoImplDepend`                    | `Boolean`             | `false`          | Auto-add jimmer-sql-kotlin/sql  |
| `generator`                         | `Generator`           |                  | Entity generator                |
| `generator.target`                  | `Target`              |                  | Generation target               |
| `generator.target.srcDir`           | `String`              |                  | Target src dir                  |
| `generator.target.packageName`      | `String`              |                  | Target package                  |
| `generator.jdbc`                    | `Jdbc`                |                  | DB connection                   |
| `generator.jdbc.driver`             | `Driver`              |                  | DB driver                       |
| `generator.jdbc.url`                | `String`              |                  | DB URL                          |
| `generator.jdbc.username`           | `String`              |                  | DB username                     |
| `generator.jdbc.password`           | `String`              |                  | DB password                     |
| `generator.jdbc.ddl`                | `File`                |                  | DDL file                        |
| `generator.jdbc.catalog`            | `String`              |                  | DB catalog                      |
| `generator.jdbc.schemaPattern`      | `String`              |                  | DB schema pattern               |
| `generator.jdbc.tableNamePattern`   | `String`              |                  | DB table name pattern           |
| `generator.table`                   | `Table`               |                  | Table rule                      |
| `generator.table.name`              | `Boolean`             | `false`          | Add table annotation            |
| `generator.table.column`            | `Boolean`             | `false`          | Add column annotation           |
| `generator.table.primaryKey`        | `String`              | `id`             | Table PK name                   |
| `generator.table.association`       | `Association`         | `REAL`           | Association rule                |
| `generator.table.typeMappings`      | `Map<String, String>` |                  | Column type mapping             |
| `generator.table.comment`           | `Boolean`             | `false`          | Generate table comment          |
| `generator.table.idView`            | `Boolean`             | `false`          | Generate id view annotation     |
| `generator.table.joinTable`         | `Boolean`             | `false`          | Generate join table annotation  |
| `generator.table.idGeneratorType`   | `String`              |                  | Generator type annotation       |
| `generator.poet`                    | `Poet`                |                  | Poet rule                       |
| `generator.poet.indent`             | `String`              | Four spaces      | Poet indent                     |
| `client.checkedExceptions`          | `Boolean`             |                  |                                 |
| `client.ignoreJdkWarning`           | `Boolean`             |                  | Java only                       |
| `dto.dirs`                          | `List<String>`        |                  |                                 |
| `dto.testDirs`                      | `List<String>`        |                  |                                 |
| `dto.mutable`                       | `Boolean`             |                  | Kotlin only                     |
| `dto.defaultNullableInputModifier`  | `InputDtoModifier`    |                  |                                 |
| `dto.hibernateValidatorEnhancement` | `Boolean`             |                  | Java only                       |
| `entry`                             | `Entry`               |                  | Java only                       |
| `entry.objects`                     | `String`              |                  | `objects` class name            |
| `entry.tables`                      | `String`              |                  | `tables` class name             |
| `entry.tableExes`                   | `String`              |                  | `tableExes` class name          |
| `entry.fetchers`                    | `String`              |                  | `fetchers` class name           |
| `immutable.isModuleRequired`        | `Boolean`             |                  | Kotlin only                     |
| `source.includes`                   | `List<String>`        |                  | Java only                       |
| `source.excludes`                   | `List<String>`        |                  | Java only                       |
| `patch.enable`                      | `Boolean`             | `false`          | Patch for Android compatibility |

### Gradle Settings Plugin Extension

| Extension | Type     | Default          | Description    |
|-----------|----------|------------------|----------------|
| `version` | `String` | `latest.release` | Jimmer version |