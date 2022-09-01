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
import com.epam.digital.data.platform.history.model.HistoryExcerptData;
import com.epam.digital.data.platform.history.model.HistoryTableData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraintColumn;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoryDataRepositoryTest {

  private static final String SCHEMA_NAME = "public";
  private static final String HST_TABLE_SUFFIX = "_hst";

  private static final String TABLE_NAME = "table";
  private static final String ID_COLUMN = "id";
  private static final UUID SEARCH_ID = UUID.fromString("3cc262c1-0cd8-4d45-be66-eb0fca821e0a");

  private HistoryDataRepository historyDataRepository;

  @Mock
  private Catalog catalog;
  @Mock
  private Schema schema;
  @Mock
  private HistoryTableSelectRepository historyTableSelectRepository;

  @Mock
  private Table operationalTable;
  @Mock
  private PrimaryKey operationalTablePk;
  @Mock
  private TableConstraintColumn operationalTablePkColumn;

  @Mock
  private Table historicalTable;
  @Mock
  private Column historicalTableSearchColumn;

  @BeforeEach
  void beforeEach() {
    historyDataRepository =
        new HistoryDataRepository(mockSchemaProperties(), catalog, schema, historyTableSelectRepository);
  }

  @Test
  void expectRepositoryResultReturnedWhenValidationsPassed() {
    when(catalog.lookupTable(schema, TABLE_NAME)).thenReturn(Optional.of(operationalTable));
    when(operationalTable.getPrimaryKey()).thenReturn(operationalTablePk);
    when(operationalTablePk.getColumns()).thenReturn(Collections.singletonList(operationalTablePkColumn));
    when(operationalTablePkColumn.getName()).thenReturn(ID_COLUMN);

    when(catalog.lookupTable(schema, TABLE_NAME + HST_TABLE_SUFFIX)).thenReturn(Optional.of(historicalTable));
    when(historicalTable.lookupColumn(ID_COLUMN))
            .thenReturn(Optional.of(historicalTableSearchColumn));

    var mockExcerptData = new HistoryTableData();
    when(historyTableSelectRepository.getHistoryData(
            TABLE_NAME + HST_TABLE_SUFFIX, ID_COLUMN, SEARCH_ID))
        .thenReturn(mockExcerptData);

    HistoryTableData actual = historyDataRepository.getHistoryData(TABLE_NAME, SEARCH_ID);

    assertThat(actual).isEqualTo(mockExcerptData);
  }

  @Test
  void expectExceptionWhenNoSearchedTable() {
    when(catalog.lookupTable(schema, TABLE_NAME)).thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () -> historyDataRepository.getHistoryData(TABLE_NAME, SEARCH_ID));
  }

  @Test
  void expectExceptionWhenNoIdInSearchedTable() {
    when(catalog.lookupTable(schema, TABLE_NAME)).thenReturn(Optional.of(operationalTable));
    when(operationalTable.getPrimaryKey()).thenReturn(null);

    assertThrows(
        IllegalArgumentException.class,
        () -> historyDataRepository.getHistoryData(TABLE_NAME, SEARCH_ID));
  }

  @Test
  void expectExceptionWhenNoHistoricalTable() {
    when(catalog.lookupTable(schema, TABLE_NAME)).thenReturn(Optional.of(operationalTable));
    when(operationalTable.getPrimaryKey()).thenReturn(operationalTablePk);
    when(operationalTablePk.getColumns()).thenReturn(Collections.singletonList(operationalTablePkColumn));
    when(operationalTablePkColumn.getName()).thenReturn(ID_COLUMN);
    when(catalog.lookupTable(schema, TABLE_NAME + HST_TABLE_SUFFIX))
            .thenReturn(Optional.empty());

    assertThrows(
            IllegalArgumentException.class,
            () -> historyDataRepository.getHistoryData(TABLE_NAME, SEARCH_ID));
  }

  @Test
  void expectExceptionWhenNoSearchColumnInHistoricalTable() {
    when(catalog.lookupTable(schema, TABLE_NAME)).thenReturn(Optional.of(operationalTable));
    when(operationalTable.getPrimaryKey()).thenReturn(operationalTablePk);
    when(operationalTablePk.getColumns())
        .thenReturn(Collections.singletonList(operationalTablePkColumn));
    when(operationalTablePkColumn.getName()).thenReturn(ID_COLUMN);

    when(catalog.lookupTable(schema, TABLE_NAME + HST_TABLE_SUFFIX))
        .thenReturn(Optional.of(historicalTable));
    when(historicalTable.lookupColumn(ID_COLUMN)).thenReturn(Optional.empty());

    assertThrows(
        IllegalArgumentException.class,
        () -> historyDataRepository.getHistoryData(TABLE_NAME, SEARCH_ID));
  }

  private SchemaProperties mockSchemaProperties() {
    var schemaProperties = new SchemaProperties();
    schemaProperties.setName(SCHEMA_NAME);
    schemaProperties.setHistoryTableSuffix(HST_TABLE_SUFFIX);
    return schemaProperties;
  }
}
