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

package cn.enaium.jimmer.gradle.task

import cn.enaium.jimmer.gradle.extension.JimmerExtension
import cn.enaium.jimmer.gradle.extension.Language
import cn.enaium.jimmer.gradle.service.impl.JavaEntityGenerateService
import cn.enaium.jimmer.gradle.service.impl.KotlinEntityGenerateService
import cn.enaium.jimmer.gradle.utility.javaTypeMappings
import cn.enaium.jimmer.gradle.utility.kotlinTypeMappings
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.net.URLClassLoader
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.sql.DriverPropertyInfo
import java.util.*
import java.util.logging.Logger

/**
 * @author Enaium
 */
open class GenerateEntityTask : DefaultTask() {

    private val extension = project.extensions.getByType(JimmerExtension::class.java)
    private val configurations = project.configurations
    private val projectDir = project.projectDir

    init {
        group = "jimmer"
        description = "Generate entity"
    }

    @TaskAction
    fun generateEntity() {
        val generator = extension.generator

        if (extension.language.get() == Language.KOTLIN) {
            kotlinTypeMappings.putAll(generator.table.typeMappings.get())
        } else if (extension.language.get() == Language.JAVA) {
            javaTypeMappings.putAll(generator.table.typeMappings.get())
        }

        val classloader = configurations.named("runtimeClasspath").get()
            .find { file -> file.name.startsWith(generator.jdbc.driver.get().module) }
            ?.let {
                URLClassLoader(arrayOf(it.toURI().toURL()), this.javaClass.classLoader)
            }
            ?: throw RuntimeException("Failed to find driver module")
        DriverManager.registerDriver(
            DiverWrapper(
                Class.forName(generator.jdbc.driver.get().className, true, classloader).getConstructor()
                    .newInstance() as Driver
            )
        )

        if (extension.language.get() == Language.KOTLIN) {
            KotlinEntityGenerateService().generate(projectDir, generator)
        } else if (extension.language.get() == Language.JAVA) {
            JavaEntityGenerateService().generate(projectDir, generator)
        } else {
            throw RuntimeException("Unknown language ${extension.language}")
        }
    }
}

private class DiverWrapper(private val driver: Driver) : Driver {
    override fun connect(url: String, info: Properties): Connection {
        return driver.connect(url, info)
    }

    override fun acceptsURL(url: String): Boolean {
        return driver.acceptsURL(url)
    }

    override fun getPropertyInfo(url: String, info: Properties): Array<DriverPropertyInfo> {
        return driver.getPropertyInfo(url, info)
    }

    override fun getMajorVersion(): Int {
        return driver.majorVersion
    }

    override fun getMinorVersion(): Int {
        return driver.minorVersion
    }

    override fun jdbcCompliant(): Boolean {
        return driver.jdbcCompliant()
    }

    override fun getParentLogger(): Logger {
        return driver.parentLogger
    }
}