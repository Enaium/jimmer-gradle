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
import com.google.devtools.ksp.gradle.KspExtension
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
            afterProject.tasks.create("generateEntity", GenerateEntityTask::class.java) {
                it.group = "jimmer"
            }

            if (afterProject.tasks.names.contains("compileJava") && extension.language.get() == Language.JAVA) {
                afterProject.tasks.withType(JavaCompile::class.java) { compile ->
                    compile.options.compilerArgs.firstOrNull {
                        it.startsWith("-Ajimmer.dto.dirs=")
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

            if (afterProject.tasks.names.contains("kspKotlin") && extension.language.get() == Language.KOTLIN) {
                afterProject.tasks.getByName("kspKotlin") { kspKotlin ->
                    afterProject.extensions.getByType(KspExtension::class.java).arguments["jimmer.dto.dirs"]?.let { dirs ->
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
            if (afterProject.plugins.hasPlugin("org.springframework.boot")) {
                afterProject.dependencies.add(
                    "implementation",
                    "org.babyfish.jimmer:jimmer-spring-boot-starter:${extension.version.get()}"
                )
            } else if (extension.language.get() == Language.JAVA) {
                afterProject.dependencies.add(
                    "implementation",
                    "org.babyfish.jimmer:jimmer-sql:${extension.version.get()}"
                )
            } else if (extension.language.get() == Language.KOTLIN) {
                afterProject.dependencies.add(
                    "implementation",
                    "org.babyfish.jimmer:jimmer-sql-kotlin:${extension.version.get()}"
                )
            }

            // Add apt
            if (project.plugins.hasPlugin("java")) {
                project.dependencies.add(
                    "annotationProcessor",
                    "org.babyfish.jimmer:jimmer-apt:${extension.version.get()}"
                )
            }
        }

        // Add ksp
        if (project.plugins.hasPlugin("com.google.devtools.ksp")) {
            project.dependencies.add(
                "ksp",
                "org.babyfish.jimmer:jimmer-ksp:${extension.version.get()}"
            )
        }
    }
}