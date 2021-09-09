package com.epam.digital.data.platform.history.repository;

import com.epam.digital.data.platform.history.model.HistoryExcerptData;
import com.epam.digital.data.platform.history.model.HistoryExcerptRowDdmInfo;
import com.epam.digital.data.platform.history.model.HistoryExcerptRow;
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
import java.util.Objects;
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

  HistoryExcerptData getHistoryData(String tableName, String searchColumn, UUID id) {
    var sql = String.format(SQL_HISTORY_REQUEST_PATTERN, tableName, searchColumn, id);
    var historyExcerptExtractor = getHistoryExcerptExtractor();
    return jdbcTemplate.query(sql, historyExcerptExtractor);
  }

  private ResultSetExtractor<HistoryExcerptData> getHistoryExcerptExtractor() {
    return resultSet -> {
      var operationalTableColumns = getOperationalTableColumns(resultSet);
      var historyExcerptRows = new ArrayList<HistoryExcerptRow>();
      while (resultSet.next()) {
        var ddmInfo = getDdmInfo(resultSet);
        var operationalTableData = getOperationalTableData(resultSet, operationalTableColumns);
        var historyExcerptRow = new HistoryExcerptRow(ddmInfo, operationalTableData);
        historyExcerptRows.add(historyExcerptRow);
      }
      return new HistoryExcerptData(operationalTableColumns, historyExcerptRows);
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

  private HistoryExcerptRowDdmInfo getDdmInfo(ResultSet resultSet) throws SQLException {
    var ddmInfo = new HistoryExcerptRowDdmInfo();
    ddmInfo.setCreatedAt(
        Optional.ofNullable(resultSet.getTimestamp(DDM_CREATED_AT_COLUMN))
            .map(DateUtils::getDateTimeFromSqlTimestamp)
            .map(Objects::toString)
            .orElse(null));
    ddmInfo.setCreatedBy(resultSet.getString(DDM_CREATED_BY_COLUMN));
    ddmInfo.setDmlOp(resultSet.getString(DDM_DML_OP_COLUMN));
    ddmInfo.setSystemId(resultSet.getString(DDM_SYSTEM_ID_COLUMN));
    ddmInfo.setApplicationId(resultSet.getString(DDM_APPLICATION_ID_COLUMN));
    ddmInfo.setBusinessProcessId(resultSet.getString(DDM_BUSINESS_PROCESS_ID_COLUMN));
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
