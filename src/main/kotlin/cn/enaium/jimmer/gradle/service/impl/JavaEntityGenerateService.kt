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
import com.squareup.javapoet.*
import org.babyfish.jimmer.sql.*
import org.gradle.api.Project
import org.jetbrains.annotations.Nullable
import java.util.*
import javax.lang.model.element.Modifier

/**
 * @author Enaium
 */
class JavaEntityGenerateService : EntityGenerateService {
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
            TypeSpec.interfaceBuilder(ClassName.get(packageName, BASE_ENTITY)).let {
                it.addModifiers(Modifier.PUBLIC)
                it.addMethods(commonColumns.map { column ->
                    val returns = MethodSpec.methodBuilder(column.name.snakeToCamelCase(firstCharUppercase = false))
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(getTypeName(javaTypeMappings, column))
                    if (column.name == generator.table.primaryKey.get()) {
                        returns.addAnnotation(Id::class.java)
                    }
                    returns.build()
                })
                it.addAnnotation(MappedSuperclass::class.java)
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
            TypeSpec.interfaceBuilder(ClassName.get(packageName, typeName)).let { type ->
                if (commonColumns.isNotEmpty()) type.addSuperinterface(ClassName.get(packageName, BASE_ENTITY))
                // Add table columns
                type.addMethods(table.columns
                    .filter {
                        // Exclude common columns
                        commonColumns.contains(it).not()
                    }.filter {
                        // Exclude id column
                        if (generator.table.idView.get().not()) it.name.endsWith(
                            idSuffix,
                            true
                        ).not() else true
                    }
                    .map { column ->
                        val methodBuilder =
                            MethodSpec.methodBuilder(column.name.snakeToCamelCase(firstCharUppercase = false))
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)

                        methodBuilder.returns(getTypeName(javaTypeMappings, column))

                        if (generator.table.comment.get()) {
                            column.remark?.let {
                                methodBuilder.addJavadoc(it)
                            }
                        }

                        if (column.name.endsWith(idSuffix, true)) {
                            methodBuilder.addAnnotation(IdView::class.java)
                        }

                        if (column.nullable) {
                            methodBuilder.addAnnotation(Nullable::class.java)
                        }

                        methodBuilder.build()
                    }
                )

                // Add table associations
                if (generator.table.association.get() != Association.NO) {
                    type.addMethods(table.foreignKeys.map { foreignKey ->
                        val referenceTypeName = foreignKey.reference.tableName.snakeToCamelCase()

                        val unique = table.uniqueKeys.filter { it.columns.size == 1 }.map { it.columns.first() }
                            .contains(foreignKey.column)

                        // owning side
                        val own = MethodSpec.methodBuilder(referenceTypeName.firstCharLowercase())
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                            .returns(ClassName.get(packageName, referenceTypeName))
                            .addAnnotation(
                                AnnotationSpec.builder(
                                    if (unique) OneToOne::class.java else ManyToOne::class.java
                                ).build(),
                            )

                        if (foreignKey.column.nullable) {
                            own.addAnnotation(Nullable::class.java)
                        }

                        // inverse side
                        type2Builder[referenceTypeName]?.addMethod(
                            MethodSpec.methodBuilder(
                                typeName.firstCharLowercase().let {
                                    if (unique) {
                                        it
                                    } else {
                                        it.toPlural()
                                    }
                                }
                            ).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(
                                    if (unique) {
                                        ClassName.get(packageName, typeName)
                                    } else {
                                        ParameterizedTypeName.get(
                                            ClassName.get("java.util", "List"),
                                            ClassName.get(packageName, typeName)
                                        )
                                    }
                                ).addAnnotation(
                                    AnnotationSpec.builder(
                                        if (unique) OneToOne::class.java else OneToMany::class.java
                                    ).addMember("mappedBy", "\$S", referenceTypeName.firstCharLowercase())
                                        .build()
                                ).let {
                                    if (unique) it.addAnnotation(Nullable::class.java) else it
                                }.build()
                        )

                        own.build()
                    })
                }
                type.addAnnotation(Entity::class.java)
                type.addModifiers(Modifier.PUBLIC)
                type.addAnnotation(
                    AnnotationSpec.builder(Table::class.java)
                        .addMember("name", "\$S", table.name)
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

                type2Builder[owningTypeName]?.addMethod(
                    MethodSpec.methodBuilder(inverseTypeName.firstCharLowercase().toPlural())
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(
                            ParameterizedTypeName.get(
                                ClassName.get("java.util", "List"),
                                ClassName.get(packageName, inverseTypeName)
                            )
                        )
                        .addAnnotation(ManyToMany::class.java)
                        .addAnnotation(
                            AnnotationSpec.builder(JoinTable::class.java)
                                .addMember("name", "\$S", table.name)
                                .addMember(
                                    "joinColumns", "\$L", AnnotationSpec.builder(JoinColumn::class.java)
                                        .addMember("name", "\$S", owningColumn.name)
                                        .build()
                                )
                                .addMember(
                                    "inverseJoinColumns", "\$L", AnnotationSpec.builder(JoinColumn::class.java)
                                        .addMember("name", "\$S", inverseColumn.name)
                                        .build()
                                ).build()
                        ).build()
                )

                type2Builder[inverseTypeName]?.addMethod(
                    MethodSpec.methodBuilder(owningTypeName.firstCharLowercase().toPlural())
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(
                            ParameterizedTypeName.get(
                                ClassName.get("java.util", "List"),
                                ClassName.get(packageName, owningTypeName)
                            )
                        ).addAnnotation(
                            AnnotationSpec.builder(ManyToMany::class.java)
                                .addMember("mappedBy", "\$S", inverseTypeName.firstCharLowercase().toPlural())
                                .build()
                        ).build()
                )
            }
        }

        // Write to file
        type2Builder.forEach { (_, type) ->
            JavaFile.builder(packageName, type.build())
                .indent(generator.poet.indent.get())
                .build()
                .writeTo(project.file(project.projectDir.resolve(generator.target.srcDir.get())))
        }
    }

    private fun getTypeName(typeMappings: Map<String, String>, column: Column): TypeName {
        return typeMappings[column.type.lowercase(Locale.ROOT)]
            ?.let {
                ClassName.get(
                    it.substring(0, it.lastIndexOf(".")),
                    it.substring(it.lastIndexOf(".") + 1)
                )
            }?.let {
                if (!column.nullable && it.isBoxedPrimitive) {
                    it.unbox()
                } else {
                    it
                }
            }
            ?: ClassName.get(java.lang.String::class.java)
    }
}