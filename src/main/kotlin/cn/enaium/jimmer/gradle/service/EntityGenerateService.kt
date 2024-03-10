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
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager

/**
 * @author Enaium
 */
interface EntityGenerateService {
    fun generate(generator: Generator): Map<Path, String>

    fun getConnection(generator: Generator): Connection {
        return DriverManager.getConnection(
            generator.jdbc.url.get(),
            generator.jdbc.username.get(),
            generator.jdbc.password.get()
        )
    }

    fun getCommonColumns(tables: Set<Table>): Set<Column> {
        return tables.flatMap { it.columns }.groupBy { it.name }
            .filter { it -> it.value.size == tables.count { it.primaryKeys.isNotEmpty() } }.map { it.value.first() }
            .toSet()
    }
}