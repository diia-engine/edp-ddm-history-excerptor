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

package com.epam.digital.data.platform.history.repository;

import com.epam.digital.data.platform.history.config.properties.SchemaProperties;
import com.epam.digital.data.platform.history.model.HistoryTableData;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Component
public class HistoryDataRepository {

  private final SchemaProperties schemaProperties;
  private final Catalog catalog;
  private final Schema schema;
  private final HistoryTableSelectRepository historyTableSelectRepository;

  public HistoryDataRepository(
      SchemaProperties schemaProperties,
      Catalog catalog,
      Schema schema,
      HistoryTableSelectRepository historyTableSelectRepository) {
    this.schemaProperties = schemaProperties;
    this.catalog = catalog;
    this.schema = schema;
    this.historyTableSelectRepository = historyTableSelectRepository;
  }

  public HistoryTableData getHistoryData(String tableName, UUID id) {
    var idColumnName = getPkColumnName(tableName);
    var historicalTableName = tableName + schemaProperties.getHistoryTableSuffix();
    validateHistoricalTable(historicalTableName, idColumnName);
    return historyTableSelectRepository.getHistoryData(historicalTableName, idColumnName, id);
  }

  private Table getTable(String tableName) {
    return catalog
        .lookupTable(schema, tableName)
        .orElseThrow(() -> new IllegalArgumentException("No such table: " + tableName));
  }

  private String getPkColumnName(String tableName) {
    var table = catalog
            .lookupTable(schema, tableName)
            .orElseThrow(() -> new IllegalArgumentException("No such table: " + tableName));
    return Optional.ofNullable(table.getPrimaryKey())
        .map(TableConstraint::getColumns)
        .map(Collection::stream)
        .flatMap(Stream::findFirst)
        .orElseThrow(
            () -> new IllegalArgumentException("No primary key found in table: " + table.getName()))
        .getName();
  }

  private void validateHistoricalTable(String historicalTableName, String searchColumnName) {
    var historicalTable = getTable(historicalTableName);
    historicalTable
        .lookupColumn(searchColumnName)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    String.format(
                        "No such column %s in historical table %s",
                        historicalTable, searchColumnName)));
  }
}