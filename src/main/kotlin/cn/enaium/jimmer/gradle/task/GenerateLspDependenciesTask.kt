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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import kotlin.io.path.*

/**
 * @author Enaium
 */
open class GenerateLspDependenciesTask : DefaultTask() {

    private val configurations = project.configurations
    private val projectDir = project.projectDir

    init {
        group = "jimmer"
        description = "Generate dependencies for LSP"
    }

    @TaskAction
    fun generateLspDependency() {
        val lspHome = Path(System.getProperty("user.home")).resolve("jimmer-dto-lsp")

        if (lspHome.resolve("server.jar").exists().not()) {
            return
        }

        val dependenciesFile = lspHome.resolve("dependencies.json")

        if (dependenciesFile.exists().not()) {
            dependenciesFile.createParentDirectories()
            dependenciesFile.createFile()
            dependenciesFile.writeText("{}")
        }

        val dependenciesJson = ObjectMapper().readTree(dependenciesFile.readText())
        val dependencies = configurations.named("runtimeClasspath").get()

        (dependenciesJson as ObjectNode).set<ArrayNode>(
            projectDir.absolutePath,
            JsonNodeFactory.instance.arrayNode().apply {
                dependencies.forEach {
                    add(it.absolutePath)
                }
            })

        dependenciesFile.writeText(ObjectMapper().writeValueAsString(dependenciesJson))
    }
}