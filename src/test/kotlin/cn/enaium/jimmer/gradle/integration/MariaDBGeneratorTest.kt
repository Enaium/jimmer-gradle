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
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.MariaDBContainer
import java.sql.DriverManager

/**
 * @author Enaium
 */
class MariaDBGeneratorTest : AbstractGeneratorEntityTest() {
    private val driverDependency = "org.mariadb.jdbc:mariadb-java-client:3.3.3"

    @Test
    fun generateEntity() {
        val kotlin = create(language = Language.KOTLIN)
        assertGenerateTask(kotlin, Language.KOTLIN)

        val java = create(language = Language.JAVA)
        assertGenerateTask(java, Language.JAVA)
    }

    private fun create(language: Language): BuildResult {
        return create(
            mariadb.jdbcUrl,
            mariadb.username,
            mariadb.password,
            Driver.MARIADB,
            language,
            driverDependency
        )
    }

    companion object {
        private val mariadb: MariaDBContainer<*> = MariaDBContainer("mariadb:latest")


        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            mariadb.start()

            Class.forName(mariadb.driverClassName)
            val createStatement = DriverManager.getConnection(
                "${mariadb.jdbcUrl}?allowMultiQueries=true",
                mariadb.username,
                mariadb.password
            ).createStatement()

            createStatement.execute(object {}::class.java.getResource("/mariadb.sql")!!.readText())
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            mariadb.stop()
        }
    }
}