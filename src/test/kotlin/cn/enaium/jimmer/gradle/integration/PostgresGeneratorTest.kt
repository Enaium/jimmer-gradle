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

import cn.enaium.jimmer.gradle.util.ProjectTest
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager
import kotlin.test.assertEquals

/**
 * @author Enaium
 */
class PostgresGeneratorTest : ProjectTest() {
    override fun name(): String {
        return "kotlinGenerator"
    }

    @Test
    fun generateEntity() {
        val create = create(
            "generateEntity",
            mapOf(
                "url" to postgres.jdbcUrl,
                "username" to postgres.username,
                "password" to postgres.password,
                "driver" to "POSTGRESQL",
                "driverDependency" to "org.postgresql:postgresql:42.6.0"
            )
        )
        assertEquals(create.task(":generateEntity")?.outcome, TaskOutcome.SUCCESS)
    }

    companion object {
        private val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:latest")


        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            postgres.start()

            Class.forName(postgres.driverClassName)
            DriverManager.getConnection(
                postgres.jdbcUrl,
                postgres.username,
                postgres.password
            ).createStatement().execute(object {}::class.java.getResource("/postgres.sql")!!.readText())
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            postgres.stop()
        }
    }
}