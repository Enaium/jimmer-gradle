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

package cn.enaium.jimmer.gradle;

import java.sql.Timestamp;
import java.time.*;

/**
 * @author Enaium
 */
public class Utility {
    private static Object handleDateTime(Object value, ZoneId zoneId) {
        if (value instanceof Instant) {
            return new Timestamp(((Instant) value).toEpochMilli());
        }
        if (value instanceof LocalDateTime) {
            return new Timestamp(((LocalDateTime) value).atZone(zoneId).toInstant().toEpochMilli());
        }
        if (value instanceof LocalDate) {
            return new java.sql.Date(((LocalDate) value).atStartOfDay().atZone(zoneId).toInstant().toEpochMilli());
        }
        if (value instanceof LocalTime) {
            return new java.sql.Timestamp(((LocalTime) value).atDate(LocalDate.now()).atZone(zoneId).toInstant().toEpochMilli());
        }
        if (value instanceof java.util.Date) {
            return new Timestamp(((java.util.Date) value).getTime());
        }
        return value;
    }
}
