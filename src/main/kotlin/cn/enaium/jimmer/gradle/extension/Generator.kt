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
import javax.inject.Inject

/**
 * @author Enaium
 */
open class Generator @Inject constructor(objects: ObjectFactory) {
    val target: Target = objects.newInstance(Target::class.java)
    val jdbc: JDBC = objects.newInstance(JDBC::class.java)
    val table: Table = objects.newInstance(Table::class.java)
    val poet: Poet = objects.newInstance(Poet::class.java)


    fun target(action: Target.() -> Unit) {
        action.invoke(target)
    }

    fun jdbc(action: JDBC.() -> Unit) {
        action.invoke(jdbc)
    }

    fun table(action: Table.() -> Unit) {
        action.invoke(table)
    }

    fun poet(action: Poet.() -> Unit) {
        action.invoke(poet)
    }
}