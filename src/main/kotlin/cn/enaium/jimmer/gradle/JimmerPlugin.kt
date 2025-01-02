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
import cn.enaium.jimmer.gradle.task.AllProjectDependencies
import cn.enaium.jimmer.gradle.task.GenerateEntityTask
import cn.enaium.jimmer.gradle.task.GenerateLspDependenciesTask
import cn.enaium.jimmer.gradle.utility.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.tasks.JvmConstants

/**
 * @author Enaium
 */
class JimmerPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("jimmer", JimmerExtension::class.java)

        if (!extension.language.isPresent) {
            if (project.plugins.hasJava) {
                extension.language.set(Language.JAVA)
            }

            if (project.plugins.hasKotlin) {
                extension.language.set(Language.KOTLIN)
            }
        }

        project.afterEvaluate { afterProject ->
            val generateEntity = afterProject.tasks.register("generateEntity", GenerateEntityTask::class.java)
            val generateLspDependencies =
                afterProject.tasks.register("generateLspDependencies", GenerateLspDependenciesTask::class.java)
            val allProjectDependencies =
                afterProject.tasks.register("allProjectDependencies", AllProjectDependencies::class.java)

            if (extension.language.get() == Language.JAVA) {
                afterProject.tasks.compileJava { compile ->

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

                    extension.dto.defaultNullableInputModifier.takeIf { it.isPresent }?.let {
                        add(DTO_DEFAULT_NULLABLE_INPUT_MODIFIER, it.get().name.lowercase())
                    }

                    extension.dto.hibernateValidatorEnhancement.takeIf { it.isPresent }?.let {
                        add(DTO_HIBERNATE_VALIDATOR_ENHANCEMENT, it.get().toString())
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
                    kspArg(afterProject.extensions.getByName("ksp"), key, value)
                }

                extension.dto.dirs.takeIf { it.isPresent && it.get().isNotEmpty() }?.let {
                    add(DTO_DIRS, it.get().joinToString(","))
                }

                extension.dto.testDirs.takeIf { it.isPresent && it.get().isNotEmpty() }?.let {
                    add(DTO_TEST_DIRS, it.get().joinToString(","))
                }

                extension.dto.mutable.takeIf { it.isPresent }?.let {
                    add(DTO_MUTABLE, it.get().toString())
                }

                extension.dto.defaultNullableInputModifier.takeIf { it.isPresent }?.let {
                    add(DTO_DEFAULT_NULLABLE_INPUT_MODIFIER, it.get().name.lowercase())
                }

                extension.client.checkedException.takeIf { it.isPresent }?.let {
                    add(CLIENT_CHECKED_EXCEPTION, it.get().toString())
                }

                extension.immutable.isModuleRequired.takeIf { it.isPresent }?.let {
                    add(IMMUTABLE_IS_MODULE_REQUIRED, it.get().toString())
                }
            }

            if (afterProject.tasks.hasCompileJava && extension.language.get() == Language.JAVA) {
                afterProject.rootProject.tasks.also {
                    if (it.hasPre) {
                        it.getByName(PRE_TASK_NAME).dependsOn(afterProject.tasks.getByName("compileJava"))
                    }
                }
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

            if (afterProject.tasks.hasCompileKotlin && afterProject.tasks.hasKsp && extension.language.get() == Language.KOTLIN) {
                afterProject.rootProject.tasks.also {
                    if (it.hasPre) {
                        it.getByName(PRE_TASK_NAME).dependsOn(afterProject.tasks.getByName(KSP_TASK_NAME))
                    }
                }
            }

            fun addInputs(task: Task) {
                if (!afterProject.plugins.hasKsp) {
                    return
                }
                kspArguments(afterProject.extensions.getByName("ksp"))[DTO_DIRS]?.let { dirs ->
                    dirs.split("\\s*[,:;]\\s*".toRegex()).mapNotNull {
                        when {
                            it == "" || it == "/" -> null
                            it.startsWith("/") -> it.substring(1)
                            it.endsWith("/") -> it.substring(0, it.length - 1)
                            else -> it.takeIf { it.isNotEmpty() }
                        }
                    }.toSet().forEach {
                        task.inputs.dir(it)
                    }
                } ?: let {
                    val dir = afterProject.layout.projectDirectory.dir("src").file("main/dto/")
                    if (dir.asFile.exists()) {
                        task.inputs.dir(dir)
                    }
                }
            }

            if (afterProject.tasks.hasCompileKotlin) {
                afterProject.tasks.compileKotlin { compileKotlin ->
                    addInputs(compileKotlin)
                }
            }

            if (afterProject.tasks.hasCompileDebugKotlin) {
                afterProject.tasks.compileDebugKotlin { compileDebugKotlin ->
                    addInputs(compileDebugKotlin)
                }
            }

            if (afterProject.tasks.hasKsp) {
                afterProject.tasks.kspKotlin { kspKotlin ->
                    addInputs(kspKotlin)
                }
            }

            if (afterProject.tasks.hasDebugKsp) {
                afterProject.tasks.kspDebugKotlin { kspDebugKotlin ->
                    addInputs(kspDebugKotlin)
                }
            }

            // Add spring-boot-starter,sql,sql-kotlin
            if (afterProject.plugins.hasSpringBoot && !afterProject.hasDependency(JIMMER_SPRINGBOOT_NAME)) {
                afterProject.dependencies.implementation(
                    "$JIMMER_GROUP:$JIMMER_SPRINGBOOT_NAME:${extension.version.get()}"
                )
            } else if (extension.language.get() == Language.JAVA && !afterProject.hasDependency(JIMMER_SQL_NAME)) {
                afterProject.dependencies.implementation(
                    "$JIMMER_GROUP:$JIMMER_SQL_NAME:${extension.version.get()}"
                )
            } else if (extension.language.get() == Language.KOTLIN
                && !afterProject.hasDependency(JIMMER_SQL_KOTLIN_NAME)
            ) {
                afterProject.dependencies.implementation(
                    "$JIMMER_GROUP:$JIMMER_SQL_KOTLIN_NAME:${extension.version.get()}"
                )
            }

            if (extension.language.get() == Language.JAVA
                && !afterProject.hasDependency(JIMMER_APT_NAME, JvmConstants.ANNOTATION_PROCESSOR_CONFIGURATION_NAME)
            ) {
                afterProject.dependencies.annotationProcessor(
                    "$JIMMER_GROUP:$JIMMER_APT_NAME:${extension.version.get()}"
                )
            }
        }

        if (project.plugins.hasKsp && extension.language.get() == Language.KOTLIN
            && hasClass("com.google.devtools.ksp.gradle.KspExtension")
        ) {
            project.dependencies.ksp(
                "$JIMMER_GROUP:$JIMMER_KSP_NAME:${extension.version.get()}"
            )
        }
    }

    private fun kspArg(any: Any, key: String, value: String) {
        any.javaClass.methods.firstOrNull {
            it.name == "arg" && it.parameterTypes.size == 2
        }?.invoke(any, key, value)
    }

    private fun kspArguments(any: Any): MutableMap<String, String> {
        @Suppress("UNCHECKED_CAST")
        return any.javaClass.methods.firstOrNull {
            it.name == "getArguments"
        }?.invoke(any) as MutableMap<String, String>
    }
}