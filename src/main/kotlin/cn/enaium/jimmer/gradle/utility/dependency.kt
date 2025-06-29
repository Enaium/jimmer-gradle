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

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.tasks.JvmConstants

/**
 * @author Enaium
 */
internal fun DependencyHandler.implementation(dependency: Any) {
    add(JvmConstants.IMPLEMENTATION_CONFIGURATION_NAME, dependency)
}

internal fun DependencyHandler.annotationProcessor(dependency: Any) {
    add(JvmConstants.ANNOTATION_PROCESSOR_CONFIGURATION_NAME, dependency)
}

internal fun DependencyHandler.ksp(dependency: String) {
    add("ksp", dependency)
}

internal fun Project.hasDependency(
    artifact: String,
    name: String = JvmConstants.IMPLEMENTATION_CONFIGURATION_NAME
): Boolean {
    var has = false
    configurations.getByName(name).dependencies.all {
        if (it.name == artifact) {
            has = true
            return@all
        }
    }
    return has
}