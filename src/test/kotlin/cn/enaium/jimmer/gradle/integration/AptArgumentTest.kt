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

import cn.enaium.jimmer.gradle.extension.InputDtoModifier
import cn.enaium.jimmer.gradle.util.ProjectTest
import cn.enaium.jimmer.gradle.utility.*
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * @author Enaium
 */
class AptArgumentTest {
    @Test
    fun test() {
        val argument = mutableMapOf<String, Any>()
        var content = ""
        argument[KEEP_IS_PREFIX] = true
        argument[SOURCE_EXCLUDES] = listOf("src/main/java")
        argument[SOURCE_EXCLUDES] = listOf("src/test/java")
        argument[DTO_DIRS] = listOf("src/main/java")
        argument[DTO_TEST_DIRS] = listOf("src/test/java")
        argument[DTO_DEFAULT_NULLABLE_INPUT_MODIFIER] = InputDtoModifier.FIXED
        argument[DTO_HIBERNATE_VALIDATOR_ENHANCEMENT] = true
        argument[CLIENT_CHECKED_EXCEPTION] = true
        argument[CLIENT_IGNORE_JDK_WARNING] = true
        argument[ENTRY_OBJECTS] = "Drafts"
        argument[ENTRY_TABLES] = "Tables"
        argument[ENTRY_TABLE_EXES] = "TableExes"
        argument[ENTRY_FETCHERS] = "Fetchers"

        for ((key, value) in argument) {
            when (value) {
                is List<*> -> {
                    content += """$key = listOf("${value[0].toString()}")"""
                    content += "\n"
                }

                is InputDtoModifier -> {
                    content += """$key = InputDtoModifier.${value.name}"""
                    content += "\n"
                }

                is String -> {
                    content += """$key = "$value""""
                    content += "\n"
                }

                else -> {
                    content += """$key = $value"""
                    content += "\n"
                }
            }
        }

        val taskName = "aptArgument"

        content += """tasks.create("$taskName") {"""
        content += "\n"
        content += """doLast {"""
        content += "\n"
        for ((k, v) in argument) {
            val value = when (v) {
                is List<*> -> v[0].toString()
                is InputDtoModifier -> v.name.lowercase()
                else -> v.toString()
            }
            content += """if (tasks.compileJava.get().options.compilerArgs.indexOf("-A$k=$value") == -1) {"""
            content += "\n"
            content += """error("$k")"""
            content += "\n"
            content += "}"
            content += "\n"
        }
        content += "\n"
        content += "}"
        content += "\n"
        content += "}"

        content += """"""

        val projectTest = ProjectTest("aptArgument")
        val task = projectTest.create(taskName, mapOf("content" to content)).task(":$taskName")
        assertEquals(task?.outcome, TaskOutcome.SUCCESS)
        projectTest.clear()
    }
}