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

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * @author Enaium
 */
open class Table @Inject constructor(objects: ObjectFactory) {
    val name: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    val column: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    val primaryKey: Property<String> = objects.property(String::class.java).convention("id")
    val association: Property<Association> = objects.property(Association::class.java).convention(Association.REAL)
    val typeMappings: MapProperty<String, String> = objects.mapProperty(String::class.java, String::class.java)
    val comment: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    val idView: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    val joinTable: Property<Boolean> = objects.property(Boolean::class.java).convention(false)
    val idGeneratorType: Property<String> = objects.property(String::class.java)
}