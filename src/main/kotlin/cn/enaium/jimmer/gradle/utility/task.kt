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

import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.compile.JavaCompile

/**
 * @author Enaium
 */
internal val TaskContainer.hasKsp: Boolean
    get() = findByName(KSP_TASK_NAME) != null

internal val TaskContainer.hasCompileJava: Boolean
    get() = findByName("compileJava") != null

internal fun TaskContainer.compileJava(action: (JavaCompile) -> Unit) {
    withType(JavaCompile::class.java) {
        action(it)
    }
}

internal fun TaskContainer.kspKotlin(action: (Task) -> Unit) {
    getByName(KSP_TASK_NAME) {
        action(it)
    }
}