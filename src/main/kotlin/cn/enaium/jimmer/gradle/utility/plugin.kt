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

package cn.enaium.jimmer.gradle.utility

import org.gradle.api.plugins.PluginContainer

/**
 * @author Enaium
 */
internal val PluginContainer.hasJava: Boolean
    get() = hasPlugin("java")

internal val PluginContainer.hasKotlin: Boolean
    get() = hasPlugin("org.jetbrains.kotlin.jvm") || hasPlugin("org.jetbrains.kotlin.multiplatform") || hasPlugin("org.jetbrains.kotlin.android")

internal val PluginContainer.hasKsp: Boolean
    get() = hasPlugin(KSP_PLUGIN_ID)

internal val PluginContainer.hasSpringBoot: Boolean
    get() = hasPlugin(SPRINGBOOT_PLUGIN_ID)