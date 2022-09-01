/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.history.util;

import com.epam.digital.data.platform.history.model.OperationalTableField;
import com.epam.digital.data.platform.history.model.OperationalTableFieldType;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public final class OperationalTableFieldResolvingUtil {

  private static final Map<Class<?>, Function<Object, OperationalTableField>>
          OPERATIONAL_TABLE_FIELD_TYPE_MAP =
          Map.of(
                  Timestamp.class,
                  column -> {
                    var dateTime = DateUtils.getDateTimeFromSqlTimestamp((Timestamp) column);
                    return new OperationalTableField(
                            dateTime.toString(), OperationalTableFieldType.DATETIME);
                  },
                  Date.class,
                  column -> {
                    var date = DateUtils.getDateFromSql((Date) column);
                    return new OperationalTableField(date.toString(), OperationalTableFieldType.DATE);
                  },
                  Time.class,
                  column -> {
                    var time = DateUtils.getTimeFromSql((Time) column);
                    return new OperationalTableField(time.toString(), OperationalTableFieldType.TIME);
                  });

  private OperationalTableFieldResolvingUtil() {}

  public static OperationalTableField resolveOperationalFieldValue(Object sqlColumnValue) {
    return OPERATIONAL_TABLE_FIELD_TYPE_MAP.entrySet().stream()
        .filter(entry -> entry.getKey().isInstance(sqlColumnValue))
        .findFirst()
        .map(entry -> entry.getValue().apply(sqlColumnValue))
        .orElse(
            new OperationalTableField(
                Optional.ofNullable(sqlColumnValue).map(Object::toString).orElse(null),
                OperationalTableFieldType.TEXT));
  }
}
