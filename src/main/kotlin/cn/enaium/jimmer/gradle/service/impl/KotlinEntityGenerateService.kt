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

package cn.enaium.jimmer.gradle.service.impl

import cn.enaium.jimmer.gradle.extension.Generator
import cn.enaium.jimmer.gradle.model.Column
import cn.enaium.jimmer.gradle.model.Table
import cn.enaium.jimmer.gradle.service.EntityGenerateService
import cn.enaium.jimmer.gradle.utility.getTables
import cn.enaium.jimmer.gradle.utility.snakeToCamelCase
import cn.enaium.jimmer.gradle.utility.toPlural
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.babyfish.jimmer.sql.*
import java.nio.file.Path
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.io.path.Path

/**
 * @author Enaium
 */
class KotlinEntityGenerateService : EntityGenerateService {
    /**
     * Generate entity
     * @param generator Generator
     * @return relative path and content
     */
    override fun generate(generator: Generator): Map<Path, String> {
        val metaData = getConnection(generator).metaData

        val tables = metaData.getTables()

        val commonColumns = getCommonColumns(tables)

        val type2properties: MutableMap<Type, CopyOnWriteArrayList<Property>> =
            mutableMapOf()

        // Initialize
        tables.forEach Table@{ table ->
            // Skip table if it has no primary key
            if (table.primaryKeys.isEmpty()) {
                return@Table
            }

            val tName = table.name.snakeToCamelCase()
            val tableInterface = TypeSpec.interfaceBuilder(tName)
            val fs = CopyOnWriteArrayList<Property>()
            table.columns.forEach Column@{ column ->
                // Skip column if it is common column
                if (commonColumns.map { it.name }.contains(column.name)) {
                    return@Column
                }
                val pName = column.name.snakeToCamelCase(false)
                val property = PropertySpec.builder(
                    pName,
                    getTypeName(generator.typeMappings.get(), column).copy(nullable = column.nullable)
                )
                fs.add(Property(pName, column, property))
            }
            type2properties[Type(tName, table, tableInterface)] = fs
        }

        // Add `OneToOne` and `OneToMany` properties
        tables.forEach Table@{ table ->
            // Skip table if it has no primary key
            if (table.primaryKeys.isEmpty()) {
                return@Table
            }
            table.foreignKeys.forEach ForeignKey@{ foreignKey ->

                // Skip column if it is common column
                if (commonColumns.map { it.name }.contains(foreignKey.column.name)) {
                    return@ForeignKey
                }

                val unique = table.uniqueKeys.filter { it.columns.size == 1 }.map { it.columns.first().name }
                    .contains(foreignKey.column.name)
                val nullable = table.columns.first { it.name == foreignKey.column.name }.nullable

                val tName = foreignKey.reference.tableName.snakeToCamelCase()
                val pName = foreignKey.column.name.snakeToCamelCase(false).let {
                    if (it.endsWith("Id")) it.substring(0, it.length - 2) else it
                }

                val property = if (unique) {
                    val property =
                        PropertySpec.builder(pName, ClassName(generator.environment.packageName.get(), tName))
                    property.addAnnotation(OneToOne::class)

                    type2properties.forEach { (type, properties) ->
                        if (type.table?.name == foreignKey.reference.tableName) {
                            val snakeToCamelCase = table.name.snakeToCamelCase(false)
                            properties.add(
                                Property(
                                    snakeToCamelCase,
                                    null,
                                    PropertySpec.builder(
                                        snakeToCamelCase,
                                        ClassName(
                                            generator.environment.packageName.get(),
                                            table.name.snakeToCamelCase()
                                        ).copy(nullable = true)
                                    ).addAnnotation(
                                        AnnotationSpec.builder(OneToOne::class)
                                            .addMember("mappedBy = %S", pName)
                                            .build()
                                    )
                                )
                            )
                        }
                    }
                    property
                } else {
                    val property =
                        PropertySpec.builder(
                            pName,
                            ClassName(generator.environment.packageName.get(), tName).copy(nullable = nullable)
                        )
                    property.addAnnotation(ManyToOne::class)

                    type2properties.forEach { (type, properties) ->
                        if (type.table?.name == foreignKey.reference.tableName) {
                            val snakeToCamelCase = table.name.snakeToCamelCase(false).toPlural()
                            properties.add(
                                Property(
                                    snakeToCamelCase,
                                    null,
                                    PropertySpec.builder(
                                        snakeToCamelCase,
                                        List::class.asClassName()
                                            .parameterizedBy(
                                                ClassName(
                                                    generator.environment.packageName.get(),
                                                    table.name.snakeToCamelCase()
                                                )
                                            )
                                    ).addAnnotation(
                                        AnnotationSpec.builder(OneToMany::class)
                                            .addMember("mappedBy = %S", pName)
                                            .build()
                                    )
                                )
                            )
                        }
                    }
                    property
                }

                // Add property to relevant table
                type2properties.forEach { (type, properties) ->
                    if (type.table?.name == table.name) {
                        properties.add(Property(pName, null, property))
                    }
                }
            }
        }

        // Add `ManyToMany` and `JoinTable` properties
        tables.filter { it.columns.size == 2 && it.foreignKeys.size == 2 }.forEach Table@{ table ->
            val foreignKeys = table.foreignKeys.toList()
            val f1 = foreignKeys[0]
            val f2 = foreignKeys[1]

            val t1 = f1.reference.tableName
            val t2 = f2.reference.tableName

            val p1 = f1.column.name.snakeToCamelCase(false).let {
                if (it.endsWith("Id")) it.substring(0, it.length - 2) else it
            }.toPlural()
            val p2 = f2.column.name.snakeToCamelCase(false).let {
                if (it.endsWith("Id")) it.substring(0, it.length - 2) else it
            }.toPlural()


            val property1 = PropertySpec.builder(
                p1, List::class.asClassName().parameterizedBy(
                    ClassName(generator.environment.packageName.get(), t1.snakeToCamelCase())
                )
            )
                .addAnnotations(
                    listOf(
                        AnnotationSpec.builder(ManyToMany::class).build(),
                        AnnotationSpec.builder(JoinTable::class)
                            .addMember("name = %S", table.name)
                            .addMember("joinColumnName = %S", f1.column.name)
                            .addMember("inverseJoinColumnName = %S", f2.column.name)
                            .build()
                    )
                )


            val property2 = PropertySpec.builder(
                p2, List::class.asClassName().parameterizedBy(
                    ClassName(generator.environment.packageName.get(), t2.snakeToCamelCase())
                )
            )
                .addAnnotation(
                    AnnotationSpec.builder(ManyToMany::class)
                        .addMember("mappedBy = %S", p1)
                        .build()
                )


            type2properties.forEach { (type, properties) ->
                if (type.table?.name == t1) {
                    properties.add(Property(p1, null, property2))
                }
                if (type.table?.name == t2) {
                    properties.add(Property(p2, null, property1))
                }
            }
        }

        if (commonColumns.isNotEmpty()) {
            // Add BaseEntity
            val baseEntity = Type(
                "BaseEntity", null,
                TypeSpec.interfaceBuilder("BaseEntity").addAnnotation(MappedSuperclass::class)
            )

            val baseEntityProperties = CopyOnWriteArrayList<Property>()
            commonColumns.forEach { column ->
                val pName = column.name.snakeToCamelCase(false)
                val property = PropertySpec.builder(pName, getTypeName(generator.typeMappings.get(), column))

                // Add column comment if comment is enabled
                if (generator.optional.comment.get()) {
                    column.remark?.let {
                        property.addKdoc(it)
                    }
                }

                baseEntityProperties.add(Property(pName, column, property))
            }
            type2properties[baseEntity] = baseEntityProperties
        }

        // Add or remove some properties for entity, such as `BaseEntity` superinterface, `commonColumns`, `idView`, `comment` etc.
        type2properties.forEach type@{ (type, properties) ->
            type.table ?: return@type

            type.builder.addAnnotation(Entity::class)
            type.builder.addAnnotation(
                AnnotationSpec.builder(
                    org.babyfish.jimmer.sql.Table::class
                ).addMember("name = %S", type.table.name).build()
            )

            // Add BaseEntity superinterface
            if (commonColumns.isNotEmpty()) {
                type.builder.addSuperinterface(ClassName(generator.environment.packageName.get(), "BaseEntity"))
            }

            properties.forEach property@{ property ->
                property.column ?: return@property
                // Remove relevant columns if idView is disabled
                val rk = type.table.foreignKeys.map { it.column.name }.contains(property.column.name)
                if (generator.optional.idView.get().not() && rk) {
                    properties.remove(property)
                }

                // Add idView annotation if idView is enabled
                if (generator.optional.idView.get() && rk) {
                    property.builder.addAnnotation(IdView::class)
                }

                // Add column comment if comment is enabled
                if (generator.optional.comment.get()) {
                    property.column.remark?.let {
                        property.builder.addKdoc(it)
                    }
                }
            }
        }

        return type2properties.map {
            val file = FileSpec.builder(generator.environment.packageName.get(), it.key.name)
                .indent(generator.poet.indent.get())
            it.value.forEach { p ->
                it.key.builder.addProperty(p.builder.build())
            }
            file.addType(it.key.builder.build())
            Path(
                generator.environment.srcDir.get(),
                generator.environment.packageName.get().replace(".", "/"),
                "${it.key.name}.kt"
            ) to file.build().toString()
        }.toMap()
    }

    private fun getTypeName(typeMappings: Map<String, String>, column: Column): TypeName {
        return typeMappings[column.type.lowercase(Locale.ROOT)]
            ?.let {
                ClassName(
                    it.substring(0, it.lastIndexOf(".")),
                    it.substring(it.lastIndexOf(".") + 1)
                )
            }
            ?: String::class.asTypeName().copy(nullable = column.nullable)
    }

    class Type(val name: String, val table: Table?, val builder: TypeSpec.Builder)
    class Property(val name: String, val column: Column?, val builder: PropertySpec.Builder)
}