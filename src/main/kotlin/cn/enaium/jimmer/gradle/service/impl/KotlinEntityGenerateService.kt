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

import cn.enaium.jimmer.gradle.extension.Association
import cn.enaium.jimmer.gradle.extension.Generator
import cn.enaium.jimmer.gradle.model.Column
import cn.enaium.jimmer.gradle.model.ForeignKey
import cn.enaium.jimmer.gradle.service.BASE_ENTITY
import cn.enaium.jimmer.gradle.service.EntityGenerateService
import cn.enaium.jimmer.gradle.utility.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.babyfish.jimmer.sql.*
import org.gradle.api.Project
import java.util.*

/**
 * @author Enaium
 */
class KotlinEntityGenerateService : EntityGenerateService {
    /**
     * Generate entity
     * @param generator Generator
     * @return relative path and content
     */
    override fun generate(project: Project, generator: Generator) {
        val idSuffix = "_${generator.table.primaryKey.get()}"

        val metaData = getConnection(generator).metaData

        val tables = metaData.getTables()

        val commonColumns = getCommonColumns(tables)

        val type2Builder = mutableMapOf<String, TypeSpec.Builder>()

        val packageName = generator.target.packageName.get()

        // Generate base entity
        if (commonColumns.isNotEmpty()) {
            TypeSpec.interfaceBuilder(ClassName(packageName, BASE_ENTITY)).let {
                it.addProperties(commonColumns.map { column ->
                    val propertyBuilder = PropertySpec.builder(
                        column.name.snakeToCamelCase(firstCharUppercase = false),
                        getTypeName(kotlinTypeMappings, column)
                    )
                    if (column.name == generator.table.primaryKey.get()) {
                        propertyBuilder.addAnnotation(Id::class)
                    }
                    propertyBuilder.build()
                })
                it.addAnnotation(MappedSuperclass::class)
            }.let {
                type2Builder[BASE_ENTITY] = it
            }
        }

        // Add fake association
        if (generator.table.association.get() == Association.FAKE) {
            tables.forEach { table ->
                table.columns.filter { commonColumns.contains(it).not() }.forEach { column ->
                    if (column.name.endsWith(idSuffix)) {
                        val foreignKey = ForeignKey(
                            "${table.name}_${column.name}_id_fkey",
                            table.name,
                            column,
                            tables.first {
                                it.name == column.name.substring(
                                    0,
                                    column.name.length - idSuffix.length
                                )
                            }.columns.first { it.name == generator.table.primaryKey.get() },
                            real = false
                        )

                        if (table.foreignKeys.contains(foreignKey).not()) {
                            table.foreignKeys.add(foreignKey)
                        }
                    }
                }
            }
        }

        // Generate entity
        tables.forEach { table ->
            // Skip table without primary key
            if (table.primaryKeys.isEmpty()) {
                return@forEach
            }

            val typeName = table.name.snakeToCamelCase()
            TypeSpec.interfaceBuilder(ClassName(packageName, typeName)).let { type ->
                if (commonColumns.isNotEmpty()) type.addSuperinterface(ClassName(packageName, BASE_ENTITY))
                // Add table columns
                type.addProperties(table.columns
                    .filter {
                        // Exclude common columns
                        commonColumns.contains(it).not()
                    }
                    .filter {
                        // Exclude id column
                        if (generator.table.idView.get().not()) it.name.endsWith(
                            idSuffix,
                            true
                        ).not() else true
                    }
                    .map { column ->
                        val propertyBuilder = PropertySpec.builder(
                            column.name.snakeToCamelCase(firstCharUppercase = false),
                            getTypeName(kotlinTypeMappings, column).copy(nullable = column.nullable)
                        )

                        if (generator.table.comment.get()) {
                            column.remark?.let {
                                propertyBuilder.addKdoc(it)
                            }
                        }

                        if (column.name.endsWith(idSuffix, true)) {
                            propertyBuilder.addAnnotation(IdView::class)
                        }
                        propertyBuilder.build()
                    }
                )

                // Add table associations
                if (generator.table.association.get() != Association.NO) {
                    type.addProperties(table.foreignKeys.map { foreignKey ->
                        val referenceTypeName = foreignKey.reference.tableName.snakeToCamelCase()

                        val unique = table.uniqueKeys.filter { it.columns.size == 1 }.map { it.columns.first() }
                            .contains(foreignKey.column)

                        // owning side
                        val own = PropertySpec.builder(
                            referenceTypeName.firstCharLowercase(),
                            ClassName(packageName, referenceTypeName).copy(nullable = foreignKey.column.nullable)
                        ).addAnnotation(
                            AnnotationSpec.builder(
                                if (unique) OneToOne::class else ManyToOne::class
                            ).build()
                        )

                        // inverse side
                        type2Builder[referenceTypeName]?.addProperty(
                            PropertySpec.builder(
                                typeName.firstCharLowercase().let {
                                    if (unique) {
                                        it
                                    } else {
                                        it.toPlural()
                                    }
                                },
                                if (unique) {
                                    ClassName(packageName, typeName).copy(nullable = true)
                                } else {
                                    List::class.asTypeName().parameterizedBy(ClassName(packageName, typeName))
                                }
                            ).addAnnotation(
                                AnnotationSpec.builder(
                                    if (unique) OneToOne::class else OneToMany::class
                                ).addMember("mappedBy = %S", referenceTypeName.firstCharLowercase()).build()
                            ).build()
                        )

                        own.build()
                    })
                }

                type.addAnnotation(Entity::class)
                type.addAnnotation(
                    AnnotationSpec.builder(Table::class)
                        .addMember("name = %S", table.name)
                        .build()
                )
            }.let {
                type2Builder[typeName] = it
            }
        }

        // Generate many-to-many association table
        if (generator.table.association.get() != Association.NO) {
            tables.forEach { table ->
                // If the table has two columns and two foreign keys, it is a many-to-many association table
                if (table.columns.size != 2 || table.foreignKeys.size != 2) return@forEach


                val owningColumn = table.foreignKeys.first().column
                val inverseColumn = table.foreignKeys.last().column

                val owningTypeName = table.foreignKeys.first().reference.tableName.snakeToCamelCase()
                val inverseTypeName = table.foreignKeys.last().reference.tableName.snakeToCamelCase()

                type2Builder[owningTypeName]?.addProperty(
                    PropertySpec.builder(
                        inverseTypeName.firstCharLowercase().toPlural(),
                        List::class.asTypeName().parameterizedBy(ClassName(packageName, inverseTypeName))
                    )
                        .addAnnotation(ManyToMany::class)
                        .addAnnotation(
                            AnnotationSpec.builder(JoinTable::class)
                                .addMember("name = %S", table.name)
                                .addMember("joinColumns = [%T(name = %S)]", JoinColumn::class, owningColumn.name)
                                .addMember(
                                    "inverseJoinColumns = [%T(name = %S)]",
                                    JoinColumn::class,
                                    inverseColumn.name
                                )
                                .build()
                        ).build()
                )

                type2Builder[inverseTypeName]?.addProperty(
                    PropertySpec.builder(
                        owningTypeName.firstCharLowercase().toPlural(),
                        List::class.asTypeName().parameterizedBy(ClassName(packageName, owningTypeName))
                    ).addAnnotation(
                        AnnotationSpec.builder(ManyToMany::class)
                            .addMember("mappedBy = %S", inverseTypeName.firstCharLowercase().toPlural())
                            .build()
                    ).build()
                )
            }
        }

        // Write to file
        for ((tableName, typeBuilder) in type2Builder) {
            FileSpec.builder(packageName, tableName)
                .indent(generator.poet.indent.get())
                .addType(typeBuilder.build()).build()
                .writeTo(project.projectDir.resolve(generator.target.srcDir.get()))
        }
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
}