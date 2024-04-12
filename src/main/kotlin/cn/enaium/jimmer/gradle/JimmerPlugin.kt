/*
 * Copyright 2024 Enaium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.enaium.jimmer.gradle

import cn.enaium.jimmer.gradle.extension.JimmerExtension
import cn.enaium.jimmer.gradle.extension.Language
import cn.enaium.jimmer.gradle.task.GenerateEntityTask
import cn.enaium.jimmer.gradle.utility.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

/**
 * @author Enaium
 */
class JimmerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("jimmer", JimmerExtension::class.java)

        project.afterEvaluate { afterProject ->
            if (!extension.language.isPresent) {
                if (afterProject.plugins.hasJava) {
                    extension.language.set(Language.JAVA)
                }

                if (afterProject.plugins.hasKotlin) {
                    extension.language.set(Language.KOTLIN)
                }
            }
        }

        project.afterEvaluate { afterProject ->
            afterProject.tasks.create("generateEntity", GenerateEntityTask::class.java) {
                it.group = "jimmer"
            }

            if (extension.language.get() == Language.JAVA) {
                afterProject.tasks.withType(JavaCompile::class.java) { compile ->

                    fun add(key: String, value: String) {
                        compile.options.compilerArgs.add("-A$key=$value")
                    }

                    extension.keepIsPrefix.takeIf { it.isPresent }?.let {
                        add(KEEP_IS_PREFIX, it.get().toString())
                    }

                    extension.source.includes.takeIf { it.isPresent && it.get().isNotEmpty() }?.let {
                        add(SOURCE_INCLUDES, it.get().joinToString(","))
                    }

                    extension.source.excludes.takeIf { it.isPresent && it.get().isNotEmpty() }?.let {
                        add(SOURCE_EXCLUDES, it.get().joinToString(","))
                    }

                    extension.dto.dirs.takeIf { it.isPresent && it.get().isNotEmpty() }?.let {
                        add(DTO_DIRS, it.get().joinToString(","))
                    }

                    extension.dto.testDirs.takeIf { it.isPresent && it.get().isNotEmpty() }?.let {
                        add(DTO_TEST_DIRS, it.get().joinToString(","))
                    }

                    extension.client.checkedException.takeIf { it.isPresent }?.let {
                        add(CLIENT_CHECKED_EXCEPTION, it.get().toString())
                    }

                    extension.client.ignoreJdkWarning.takeIf { it.isPresent }?.let {
                        add(CLIENT_IGNORE_JDK_WARNING, it.get().toString())
                    }

                    extension.entry.objects.takeIf { it.isPresent }?.let {
                        add(ENTRY_OBJECTS, it.get())
                    }

                    extension.entry.tables.takeIf { it.isPresent }?.let {
                        add(ENTRY_TABLES, it.get())
                    }

                    extension.entry.tableExes.takeIf { it.isPresent }?.let {
                        add(ENTRY_TABLE_EXES, it.get())
                    }

                    extension.entry.fetchers.takeIf { it.isPresent }?.let {
                        add(ENTRY_FETCHERS, it.get())
                    }
                }
            } else if (afterProject.plugins.hasKsp && extension.language.get() == Language.KOTLIN) {
                fun add(key: String, value: String) {
                    afterProject.extensions.ksp.arg(key, value)
                }

                extension.dto.dirs.takeIf { it.isPresent && it.get().isNotEmpty() }?.let {
                    add(DTO_DIRS, it.get().toString())
                }

                extension.dto.testDirs.takeIf { it.isPresent && it.get().isNotEmpty() }?.let {
                    add(DTO_TEST_DIRS, it.get().toString())
                }

                extension.dto.mutable.takeIf { it.isPresent }?.let {
                    add(DTO_MUTABLE, it.get().toString())
                }

                extension.client.checkedException.takeIf { it.isPresent }?.let {
                    add(CLIENT_CHECKED_EXCEPTION, it.get().toString())
                }
            }

            if (afterProject.tasks.hasCompileJava && extension.language.get() == Language.JAVA) {
                afterProject.tasks.compileJava { compile ->
                    compile.options.compilerArgs.firstOrNull {
                        it.startsWith("-A$DTO_DIRS=")
                    }?.let {
                        val dirs = it.split("=")[1]
                        for (path in dirs.trim().split("\\*[,:;]\\s*".toRegex())) {
                            var p = path
                            if (p.isEmpty() || p == "/") {
                                continue
                            }
                            if (p.startsWith("/")) {
                                p = p.substring(1)
                            }
                            if (path.endsWith("/")) {
                                p = p.substring(0, path.length - 1)
                            }
                            if (p.isNotEmpty()) {
                                compile.inputs.dir(p)
                            }
                        }
                    } ?: let {
                        val dir = afterProject.layout.projectDirectory.dir("src").file("main/dto/")
                        if (dir.asFile.exists()) {
                            compile.inputs.dir(dir)
                        }
                    }
                }
            }

            if (afterProject.tasks.hasKsp && extension.language.get() == Language.KOTLIN) {
                afterProject.tasks.kspKotlin { kspKotlin ->
                    afterProject.extensions.ksp.arguments[DTO_DIRS]?.let { dirs ->
                        dirs.split("\\s*[,:;]\\s*".toRegex()).mapNotNull {
                            when {
                                it == "" || it == "/" -> null
                                it.startsWith("/") -> it.substring(1)
                                it.endsWith("/") -> it.substring(0, it.length - 1)
                                else -> it.takeIf { it.isNotEmpty() }
                            }
                        }.toSet().forEach {
                            kspKotlin.inputs.dir(it)
                        }
                    } ?: let {
                        val dir = afterProject.layout.projectDirectory.dir("src").file("main/dto/")
                        if (dir.asFile.exists()) {
                            kspKotlin.inputs.dir(dir)
                        }
                    }
                }
            }

            // Add spring-boot-starter,sql,sql-kotlin
            if (afterProject.plugins.hasSpringBoot) {
                afterProject.dependencies.implementation(
                    "org.babyfish.jimmer:jimmer-spring-boot-starter:${extension.version.get()}"
                )
            } else if (extension.language.get() == Language.JAVA) {
                afterProject.dependencies.implementation(
                    "org.babyfish.jimmer:jimmer-sql:${extension.version.get()}"
                )
            } else if (extension.language.get() == Language.KOTLIN) {
                afterProject.dependencies.implementation(
                    "org.babyfish.jimmer:jimmer-sql-kotlin:${extension.version.get()}"
                )
            }

            // Add apt
            if (project.plugins.hasJava) {
                project.dependencies.annotationProcessor(
                    "org.babyfish.jimmer:jimmer-apt:${extension.version.get()}"
                )
            }
        }

        // Add ksp
        if (project.plugins.hasKsp) {
            project.dependencies.ksp(
                "org.babyfish.jimmer:jimmer-ksp:${extension.version.get()}"
            )
        }
    }
}