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

import cn.enaium.jimmer.gradle.extension.Driver
import cn.enaium.jimmer.gradle.extension.Language
import org.gradle.testkit.runner.BuildResult
import org.junit.jupiter.api.Test

/**
 * @author Enaium
 */
class H2GeneratorTest : AbstractGeneratorEntityTest() {

    private val driverDependency = "com.h2database:h2:2.4.240"

    @Test
    fun generateEntity() {
        val kotlin = create(language = Language.KOTLIN)
        assertGenerateTask(kotlin, Language.KOTLIN)

        val java = create(language = Language.JAVA)
        assertGenerateTask(java, Language.JAVA)
    }

    private fun create(language: Language): BuildResult {
        return create(
            "jdbc:h2:mem:test;DATABASE_TO_LOWER=true;INIT=RUNSCRIPT FROM 'src/test/resources/mariadb.sql'",
            "",
            "",
            Driver.H2,
            language,
            driverDependency
        )
    }
}