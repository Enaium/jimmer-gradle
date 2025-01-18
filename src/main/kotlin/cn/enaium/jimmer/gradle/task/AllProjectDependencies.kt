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

package cn.enaium.jimmer.gradle.task

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Enaium
 */
open class AllProjectDependencies : DefaultTask() {

    private val rootProject = project.rootProject

    init {
        group = "jimmer"
        description = "Get all project dependencies"
    }

    @TaskAction
    fun allProjectDependencies() {
        val rootProject = rootProject
        val allProjects = rootProject.allprojects
        val map = mutableMapOf<String, List<String>>()
        try {
            allProjects.forEach { p ->
                p.configurations.named { it == "runtimeClasspath" }.takeIf { it.isNotEmpty() }
                    ?.named("runtimeClasspath")
                    ?.also {
                        map[p.name] = it.get().map { it.absolutePath }
                    }
                p.configurations.named { it == "debugRuntimeClasspath" }.takeIf { it.isNotEmpty() }
                    ?.named("debugRuntimeClasspath")?.also {
                        map[p.name] = it.get().map { it.absolutePath }
                    }
            }
        } catch (_: Throwable) {

        }
        rootProject.logger.lifecycle(jacksonObjectMapper().writeValueAsString(map))
    }
}