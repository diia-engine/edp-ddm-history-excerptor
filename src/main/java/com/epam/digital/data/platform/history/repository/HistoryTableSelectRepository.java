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

import com.epam.digital.data.platform.history.model.HistoryTableData;
import com.epam.digital.data.platform.history.model.HistoryTableRow;
import com.epam.digital.data.platform.history.model.HistoryTableRowDdmInfo;
import com.epam.digital.data.platform.history.model.OperationalTableField;
import com.epam.digital.data.platform.history.util.DateUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.epam.digital.data.platform.history.util.OperationalTableFieldResolvingUtil.resolveOperationalFieldValue;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_APPLICATION_ID_COLUMN;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_BUSINESS_ACTIVITY_COLUMN;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_BUSINESS_ACTIVITY_INSTANCE_ID_COLUMN;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_BUSINESS_PROCESS_DEFINITION_ID_COLUMN;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_BUSINESS_PROCESS_ID_COLUMN;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_BUSINESS_PROCESS_INSTANCE_ID_COLUMN;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_COLUMNS;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_CREATED_AT_COLUMN;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_CREATED_BY_COLUMN;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_DIGITAL_SIGN_CHECKSUM_COLUMN;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_DIGITAL_SIGN_COLUMN;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_DIGITAL_SIGN_DERIVED_CHECKSUM_COLUMN;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_DIGITAL_SIGN_DERIVED_COLUMN;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_DML_OP_COLUMN;
import static com.epam.digital.data.platform.history.util.HistoryTableUtil.DDM_SYSTEM_ID_COLUMN;

@Repository
public class HistoryTableSelectRepository {

  private static final String SQL_HISTORY_REQUEST_PATTERN =
      "select * from %s where %s='%s' order by ddm_created_at desc";

  private final JdbcTemplate jdbcTemplate;

  public HistoryTableSelectRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  HistoryTableData getHistoryData(String tableName, String searchColumn, UUID id) {
    var sql = String.format(SQL_HISTORY_REQUEST_PATTERN, tableName, searchColumn, id);
    var historyExcerptExtractor = getHistoryDataExtractor();
    return jdbcTemplate.query(sql, historyExcerptExtractor);
  }

  private ResultSetExtractor<HistoryTableData> getHistoryDataExtractor() {
    return resultSet -> {
      var operationalTableColumns = getOperationalTableColumns(resultSet);
      var historyExcerptRows = new ArrayList<HistoryTableRow>();
      while (resultSet.next()) {
        var ddmInfo = getDdmInfo(resultSet);
        var operationalTableData = getOperationalTableData(resultSet, operationalTableColumns);
        var historyExcerptRow = new HistoryTableRow(ddmInfo, operationalTableData);
        historyExcerptRows.add(historyExcerptRow);
      }
      return new HistoryTableData(operationalTableColumns, historyExcerptRows);
    };
  }

  private List<String> getOperationalTableColumns(ResultSet resultSet) throws SQLException {
    var metadata = resultSet.getMetaData();
    var columnCount = metadata.getColumnCount();
    List<String> columnNames = new ArrayList<>();
    for (int j = 1; j <= columnCount; j++) {
      columnNames.add(metadata.getColumnName(j));
    }
    return columnNames.stream()
        .filter(Predicate.not(DDM_COLUMNS::contains))
        .collect(Collectors.toList());
  }

  private HistoryTableRowDdmInfo getDdmInfo(ResultSet resultSet) throws SQLException {
    var ddmInfo = new HistoryTableRowDdmInfo();
    ddmInfo.setCreatedAt(
        Optional.ofNullable(resultSet.getTimestamp(DDM_CREATED_AT_COLUMN))
            .map(DateUtils::getDateTimeFromSqlTimestamp)
            .orElse(null));
    ddmInfo.setCreatedBy(resultSet.getString(DDM_CREATED_BY_COLUMN));
    ddmInfo.setDmlOp(resultSet.getString(DDM_DML_OP_COLUMN));
    ddmInfo.setSystemId(resultSet.getObject(DDM_SYSTEM_ID_COLUMN, UUID.class));
    ddmInfo.setApplicationId(resultSet.getObject(DDM_APPLICATION_ID_COLUMN, UUID.class));
    ddmInfo.setBusinessProcessId(resultSet.getObject(DDM_BUSINESS_PROCESS_ID_COLUMN, UUID.class));
    ddmInfo.setBusinessProcessDefinitionId(
        resultSet.getString(DDM_BUSINESS_PROCESS_DEFINITION_ID_COLUMN));
    ddmInfo.setBusinessProcessInstanceId(
        resultSet.getString(DDM_BUSINESS_PROCESS_INSTANCE_ID_COLUMN));
    ddmInfo.setBusinessActivity(resultSet.getString(DDM_BUSINESS_ACTIVITY_COLUMN));
    ddmInfo.setBusinessActivityInstanceId(
        resultSet.getString(DDM_BUSINESS_ACTIVITY_INSTANCE_ID_COLUMN));
    ddmInfo.setDigitalSign(resultSet.getString(DDM_DIGITAL_SIGN_COLUMN));
    ddmInfo.setDigitalSignDerived(resultSet.getString(DDM_DIGITAL_SIGN_DERIVED_COLUMN));
    ddmInfo.setDigitalSignChecksum(resultSet.getString(DDM_DIGITAL_SIGN_CHECKSUM_COLUMN));
    ddmInfo.setDigitalSignDerivedChecksum(
        resultSet.getString(DDM_DIGITAL_SIGN_DERIVED_CHECKSUM_COLUMN));
    return ddmInfo;
  }

  private Map<String, OperationalTableField> getOperationalTableData(
      ResultSet resultSet, List<String> operationalTableColumns) throws SQLException {
    var operationTableData = new HashMap<String, OperationalTableField>();
    for (String columnName : operationalTableColumns) {
      var sqlColumnValue = resultSet.getObject(columnName);
      operationTableData.put(columnName, resolveOperationalFieldValue(sqlColumnValue));
    }
    return operationTableData;
  }
}
