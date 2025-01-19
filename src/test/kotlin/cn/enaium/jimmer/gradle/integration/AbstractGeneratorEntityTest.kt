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

package cn.enaium.jimmer.gradle.integration

import cn.enaium.jimmer.gradle.extension.Language
import cn.enaium.jimmer.gradle.util.ProjectTest
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import kotlin.test.assertEquals

/**
 * @author Enaium
 */
abstract class AbstractGeneratorEntityTest {

    private val projectTest = ProjectTest("generateEntity")

    private val entities = listOf("Answer", "BaseEntity", "Comment", "People", "Post", "Profile", "Question", "Topic")

    fun assertGenerateTask(buildResult: BuildResult, language: Language) {
        assertEquals(buildResult.task(":generateEntity")?.outcome, TaskOutcome.SUCCESS)
        val lang = if (language == Language.KOTLIN) "kotlin" else "java"
        val extension = if (language == Language.KOTLIN) "kt" else "java"
        val packagePath = projectTest.testProjectDir.resolve("src")
            .resolve("main")
            .resolve(lang)
            .resolve("cn")
            .resolve("enaium")

        entities.forEach {
            val fileName = "$it.$extension"
            val entity = packagePath.resolve(fileName)
            assertEquals(entity.exists(), true)
            assertEquals(
                entity.readText(),
                object {}.javaClass.getResource("/generated/entity/$lang/$fileName")!!.readText()
            )
        }
    }

    fun create(
        url: String,
        username: String,
        password: String,
        driver: String,
        language: String,
        driverDependency: String
    ): BuildResult {
        return projectTest.create(
            "generateEntity", mapOf(
                "url" to url,
                "username" to username,
                "password" to password,
                "driver" to driver,
                "language" to language,
                "driverDependency" to driverDependency
            )
        )
    }
}