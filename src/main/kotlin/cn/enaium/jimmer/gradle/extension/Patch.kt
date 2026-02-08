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

package cn.enaium.jimmer.gradle.extension

import org.gradle.api.internal.tasks.JvmConstants.IMPLEMENTATION_CONFIGURATION_NAME
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * @author Enaium
 */
open class Patch @Inject constructor(objects: ObjectFactory) {
    val enable: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    val nullableMark: Property<String> = objects.property(String::class.java).convention("N")
    val configuration: Property<String> =
        objects.property(String::class.java).convention(IMPLEMENTATION_CONFIGURATION_NAME)
}