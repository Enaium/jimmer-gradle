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

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * @author Enaium
 */
val kotlinTypeMappings = mapOf(
    "smallint" to Short::class.qualifiedName!!,
    "integer" to Int::class.qualifiedName!!,
    "bigint" to Long::class.qualifiedName!!,
    "decimal" to BigDecimal::class.qualifiedName!!,
    "numeric" to BigDecimal::class.qualifiedName!!,
    "varchar" to String::class.qualifiedName!!,
    "text" to String::class.qualifiedName!!,
    "date" to LocalDate::class.qualifiedName!!,
    "time" to LocalTime::class.qualifiedName!!,
    "datetime" to LocalDateTime::class.qualifiedName!!,
    "timestamp" to LocalDateTime::class.qualifiedName!!,
    "bool" to Boolean::class.qualifiedName!!,
    "uuid" to UUID::class.qualifiedName!!,
)