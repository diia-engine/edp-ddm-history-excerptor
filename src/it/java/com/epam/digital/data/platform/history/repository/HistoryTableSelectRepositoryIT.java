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

import com.epam.digital.data.platform.history.config.DataSourceEnable;
import com.epam.digital.data.platform.history.model.HistoryTableData;
import com.epam.digital.data.platform.history.model.HistoryTableRow;
import com.epam.digital.data.platform.history.model.HistoryTableRowDdmInfo;
import com.epam.digital.data.platform.history.model.OperationalTableField;
import com.epam.digital.data.platform.history.service.UserInfoRetrieveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.epam.digital.data.platform.history.model.OperationalTableFieldType.DATE;
import static com.epam.digital.data.platform.history.model.OperationalTableFieldType.TEXT;
import static com.epam.digital.data.platform.history.model.OperationalTableFieldType.DATETIME;
import static com.epam.digital.data.platform.history.model.OperationalTableFieldType.TIME;
import static org.assertj.core.api.Assertions.assertThat;

@DataSourceEnable
@SpringBootTest(classes = { HistoryTableSelectRepository.class })
@MockBean(UserInfoRetrieveService.class)
class HistoryTableSelectRepositoryIT {

  @Autowired
  private HistoryTableSelectRepository historyTableSelectRepository;

  @Test
  void expectCorrectTableDataRetrieved() {
    var tableName = "pd_processing_consent_hst";
    var searchColumn = "consent_id";
    var entityId = UUID.fromString("3cc262c1-0cd8-4d45-be66-eb0fca821e0a");

    var actualHistoryData = historyTableSelectRepository.getHistoryData(tableName, searchColumn, entityId);

    var expectedHistoryData = createExpectedPdConsentHistoryTableData();
    assertThat(actualHistoryData).usingRecursiveComparison().isEqualTo(expectedHistoryData);
  }

  @Test
  void expectOperationalDateFieldMapped() {
    var tableName = "pd_processing_date_hst";
    var searchColumn = "consent_id";
    var entityId = UUID.fromString("3cc262c1-0cd8-4d45-be66-eb0fca821e0a");

    var actualHistoryData = historyTableSelectRepository
            .getHistoryData(tableName, searchColumn, entityId);

    var expectedHistoryData = createExpectedPdProcessingDateHistoryTableData();
    assertThat(actualHistoryData).usingRecursiveComparison().isEqualTo(expectedHistoryData);
  }

  private HistoryTableData createExpectedPdConsentHistoryTableData() {
    var expected = new HistoryTableData();
    expected.setOperationalTableFields(List.of(
            "consent_id",
            "consent_date",
            "person_gender",
            "person_full_name",
            "person_pass_number"));
    expected.setTableRows(getExpectedHistoryTableRows(getOperationalTableDataForPdConsent()));
    return expected;
  }

  private HistoryTableData createExpectedPdProcessingDateHistoryTableData() {
    var expected = new HistoryTableData();
    expected.setOperationalTableFields(List.of(
            "consent_id",
            "consent_date",
            "consent_time",
            "consent_datetime"));
    expected.setTableRows(getExpectedHistoryTableRows(getOperationalTableDataForPdProcessingDate()));
    return expected;
  }

  private List<HistoryTableRow> getExpectedHistoryTableRows(Map<String, OperationalTableField> operationalTableData) {
    var tableRow = new HistoryTableRow();
    var ddmInfo = new HistoryTableRowDdmInfo();
    ddmInfo.setCreatedAt(OffsetDateTime.parse("2021-08-20T15:00:07Z"));
    ddmInfo.setCreatedBy("user");
    ddmInfo.setDmlOp("I");
    ddmInfo.setSystemId(UUID.fromString("bd223413-214a-4d6d-9dee-39813f15dad0"));
    ddmInfo.setApplicationId(UUID.fromString("04a00dc5-904d-4043-b8b0-aeb2d4c73ee2"));
    ddmInfo.setBusinessProcessId(UUID.fromString("b533ab28-4068-4e48-9115-e3a74fcfa243"));
    ddmInfo.setBusinessProcessDefinitionId("BP_DEF");
    ddmInfo.setBusinessProcessInstanceId("d3d4db61-049c-4830-b77c-e8a7d2ebec89");
    ddmInfo.setBusinessActivity("B_ACT");
    ddmInfo.setBusinessActivityInstanceId("B_ACT_INST_ID");
    ddmInfo.setDigitalSign("DIGN_SIGN");
    ddmInfo.setDigitalSignDerived("DIGN_SIGN_DER");
    ddmInfo.setDigitalSignChecksum(
        "32b0cfd6c6e2dd5750268f634b788f845fd075103b007110d62c6fae0e94028c");
    ddmInfo.setDigitalSignDerivedChecksum(
        "6926f443a89f6aebe1fa2477e40d4c32b8f4aab524f6dc1dd2aa331304c320c4");
    tableRow.setDdmInfo(ddmInfo);
    tableRow.setOperationalTableData(operationalTableData);

    return Collections.singletonList(tableRow);
  }

  private Map<String, OperationalTableField> getOperationalTableDataForPdConsent() {
    Map<String, OperationalTableField> operationalTableData = new HashMap<>();
    operationalTableData.put(
            "consent_id", new OperationalTableField("3cc262c1-0cd8-4d45-be66-eb0fca821e0a", TEXT));
    operationalTableData.put("consent_date", new OperationalTableField("2020-01-15T10:00:01Z", DATETIME));
    operationalTableData.put("person_gender", new OperationalTableField("M", TEXT));
    operationalTableData.put("person_full_name", new OperationalTableField("John Doe Patronymic", TEXT));
    operationalTableData.put("person_pass_number", new OperationalTableField("AB123456", TEXT));
    return operationalTableData;
  }

  private Map<String, OperationalTableField> getOperationalTableDataForPdProcessingDate() {
    Map<String, OperationalTableField> operationalTableData = new HashMap<>();
    operationalTableData.put(
            "consent_id", new OperationalTableField("3cc262c1-0cd8-4d45-be66-eb0fca821e0a", TEXT));
    operationalTableData.put("consent_date", new OperationalTableField("2020-01-15", DATE));
    operationalTableData.put("consent_time", new OperationalTableField("10:00:01Z", TIME));
    operationalTableData.put("consent_datetime", new OperationalTableField(null, TEXT));
    return operationalTableData;
  }
}