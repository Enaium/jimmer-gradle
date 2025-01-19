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

package cn.enaium.jimmer.gradle.service

import cn.enaium.jimmer.gradle.extension.Generator
import cn.enaium.jimmer.gradle.model.Column
import cn.enaium.jimmer.gradle.model.Table
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

/**
 * @author Enaium
 */
interface EntityGenerateService {

    fun generate(projectDir: File, generator: Generator)

    fun getConnection(generator: Generator): Connection {
        return generator.jdbc.ddl.orNull?.let { ddl ->
            DriverManager.getConnection(
                "jdbc:h2:mem:test;DATABASE_TO_LOWER=true;INIT=RUNSCRIPT FROM '${
                    ddl.absolutePath.replace(
                        "\\",
                        "/"
                    )
                }'"
            )
        } ?: let {
            DriverManager.getConnection(
                generator.jdbc.url.get(),
                generator.jdbc.username.get(),
                generator.jdbc.password.get()
            )
        }
    }

    fun getCommonColumns(tables: Set<Table>): Set<Column> {
        return tables.asSequence().flatMap { it.columns }.groupBy { it.name }
            .filter { it -> it.value.size == tables.count { it.primaryKeys.isNotEmpty() } }.map { it.value.first() }
            .toSet()
    }
}

internal const val BASE_ENTITY = "BaseEntity"