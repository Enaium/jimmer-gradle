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

package cn.enaium.jimmer.gradle

import cn.enaium.jimmer.gradle.extension.SettingExtension
import cn.enaium.jimmer.gradle.utility.lib
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

/**
 * @author Enaium
 */
class SettingPlugin : Plugin<Settings> {
    override fun apply(target: Settings) {
        val extension = target.extensions.create("jimmer", SettingExtension::class.java)

        target.dependencyResolutionManagement.versionCatalogs.apply {
            register("jimmer_catalog") {
                it.plugin("jimmer", "cn.enaium.jimmer.gradle").version {
                    // Nothing
                }
                it.plugin("ksp", "com.google.devtools.ksp").version {
                    // Nothing
                }
                it.lib("apt", "apt").version(extension.version.get())
                it.lib("bom", "bom").version(extension.version.get())
                it.lib("client", "client").version(extension.version.get())
                it.lib("coreKotlin", "core-kotlin").version(extension.version.get())
                it.lib("core", "core").version(extension.version.get())
                it.lib("dtoCompiler", "dto-compiler").version(extension.version.get())
                it.lib("ksp", "ksp").version(extension.version.get())
                it.lib("mapstructApt", "mapstruct-apt").version(extension.version.get())
                it.lib("springBootStarter", "spring-boot-starter").version(extension.version.get())
                it.lib("sqlKotlin", "sql-kotlin").version(extension.version.get())
                it.lib("sql", "sql").version(extension.version.get())
            }
        }
    }
}