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

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import javax.inject.Inject

/**
 * @author Enaium
 */
open class Generator @Inject constructor(objects: ObjectFactory) {
    internal val environment: Environment = objects.newInstance(Environment::class.java)
    internal val jdbc: JDBC = objects.newInstance(JDBC::class.java)
    internal val optional: Optional = objects.newInstance(Optional::class.java)
    val typeMappings: MapProperty<String, String> = objects.mapProperty(String::class.java, String::class.java)
    internal val poet: cn.enaium.jimmer.gradle.extension.Poet = objects.newInstance(cn.enaium.jimmer.gradle.extension.Poet::class.java)


    fun environment(action: Action<Environment>) {
        action.execute(environment)
    }

    fun jdbc(action: Action<JDBC>) {
        action.execute(jdbc)
    }

    fun optional(action: Action<Optional>) {
        action.execute(optional)
    }

    fun poet(action: Action<cn.enaium.jimmer.gradle.extension.Poet>) {
        action.execute(poet)
    }
}