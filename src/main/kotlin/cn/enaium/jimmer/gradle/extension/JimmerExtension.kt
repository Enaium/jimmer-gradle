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
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * @author Enaium
 */
open class JimmerExtension @Inject constructor(objects: ObjectFactory) {
    val generator: Generator = objects.newInstance(Generator::class.java)
    val client: Client = objects.newInstance(Client::class.java)
    val dto: Dto = objects.newInstance(Dto::class.java)
    val entry: Entry = objects.newInstance(Entry::class.java)
    val source: Source = objects.newInstance(Source::class.java)
    val language: Property<Language> = objects.property(Language::class.java)
    val version: Property<String> = objects.property(String::class.java).convention("+")
    val keepIsPrefix: Property<Boolean> = objects.property(Boolean::class.java)

    fun generator(action: Generator.() -> Unit) {
        action.invoke(generator)
    }

    fun client(action: Client.() -> Unit) {
        action.invoke(client)
    }

    fun dto(action: Dto.() -> Unit) {
        action.invoke(dto)
    }

    fun entry(action: Entry.() -> Unit) {
        action.invoke(entry)
    }

    fun source(action: Source.() -> Unit) {
        action.invoke(source)
    }
}


