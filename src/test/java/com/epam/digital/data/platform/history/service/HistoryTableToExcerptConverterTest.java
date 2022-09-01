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

package com.epam.digital.data.platform.history.service;

import com.epam.digital.data.platform.history.model.DdmSourceApplication;
import com.epam.digital.data.platform.history.model.DdmSourceSystem;
import com.epam.digital.data.platform.history.model.HistoryExcerptData;
import com.epam.digital.data.platform.history.model.HistoryExcerptRow;
import com.epam.digital.data.platform.history.model.HistoryExcerptRowDdmInfo;
import com.epam.digital.data.platform.history.model.HistoryTableData;
import com.epam.digital.data.platform.history.model.HistoryTableRow;
import com.epam.digital.data.platform.history.model.HistoryTableRowDdmInfo;
import com.epam.digital.data.platform.history.model.OperationalTableField;
import com.epam.digital.data.platform.history.model.UserInfo;
import com.epam.digital.data.platform.history.repository.DdmSourceApplicationRepository;
import com.epam.digital.data.platform.history.repository.DdmSourceSystemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.epam.digital.data.platform.history.model.OperationalTableFieldType.TEXT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoryTableToExcerptConverterTest {

  private static final UserInfo MOCK_USER_INFO = new UserInfo("name", "drfo", "edrpou");

  private static final UUID SYSTEM_ID = UUID.fromString("bd223413-214a-4d6d-9dee-39813f15dad0");
  private static final String SYSTEM_NAME = "sysName";
  private static final UUID APPLICATION_ID = UUID.fromString("04a00dc5-904d-4043-b8b0-aeb2d4c73ee2");
  private static final String APPLICATION_NAME = "appName";

  private static final OffsetDateTime CREATED_AT_DATETIME = OffsetDateTime.parse("2021-08-20T15:00:07Z");

  private HistoryTableToExcerptConverter historyTableToExcerptConverter;

  @Mock
  private UserInfoRetrieveService userInfoRetrieveService;
  @Mock
  private DdmSourceApplicationRepository ddmSourceApplicationRepository;
  @Mock
  private DdmSourceSystemRepository ddmSourceSystemRepository;

  @BeforeEach
  void beforeEach() {
    when(userInfoRetrieveService.getUserInfo(any()))
            .thenReturn(MOCK_USER_INFO);

    var mockDdmApp = new DdmSourceApplication();
    mockDdmApp.setApplicationId(APPLICATION_ID);
    mockDdmApp.setApplicationName(APPLICATION_NAME);
    when(ddmSourceApplicationRepository.findByApplicationIdIn(
            Collections.singletonList(APPLICATION_ID)))
        .thenReturn(Collections.singletonList(mockDdmApp));

    var mockDdmSystem = new DdmSourceSystem();
    mockDdmSystem.setSystemId(SYSTEM_ID);
    mockDdmSystem.setSystemName(SYSTEM_NAME);
    when(ddmSourceSystemRepository.findBySystemIdIn(Collections.singletonList(SYSTEM_ID)))
        .thenReturn(Collections.singletonList(mockDdmSystem));

    historyTableToExcerptConverter =
        new HistoryTableToExcerptConverter(
            userInfoRetrieveService, ddmSourceApplicationRepository, ddmSourceSystemRepository);
  }

  @Test
  void expectValidConversion() {
    var historyTableData = createMockPdConsentHistoryTableData();

    var actualExcerptData = historyTableToExcerptConverter.convert(historyTableData);

    var expectedExcerptData = createExpectedExcerptData(historyTableData);
    assertThat(actualExcerptData).usingRecursiveComparison().isEqualTo(expectedExcerptData);
  }

  private HistoryTableData createMockPdConsentHistoryTableData() {
    var expected = new HistoryTableData();
    expected.setOperationalTableFields(Collections.singletonList("table_field"));
    expected.setTableRows(getMockHistoryTableRows());
    return expected;
  }

  private List<HistoryTableRow> getMockHistoryTableRows() {
    var excerptRow = new HistoryTableRow();
    var ddmInfo = new HistoryTableRowDdmInfo();
    ddmInfo.setCreatedAt(CREATED_AT_DATETIME);
    ddmInfo.setCreatedBy("user");
    ddmInfo.setDmlOp("I");
    ddmInfo.setSystemId(SYSTEM_ID);
    ddmInfo.setApplicationId(APPLICATION_ID);
    ddmInfo.setBusinessProcessId(UUID.fromString("b533ab28-4068-4e48-9115-e3a74fcfa243"));
    ddmInfo.setBusinessActivity("B_ACT");
    excerptRow.setDdmInfo(ddmInfo);

    excerptRow.setOperationalTableData(Map.of("table_field", new OperationalTableField("value", TEXT)));

    return Collections.singletonList(excerptRow);
  }

  private HistoryExcerptData createExpectedExcerptData(HistoryTableData historyTableData) {
    var expectedExcerptData = new HistoryExcerptData();
    expectedExcerptData.setOperationalTableFields(historyTableData.getOperationalTableFields());
    var historyTableRow = historyTableData.getTableRows().get(0);
    var expectedExcerptRow = new HistoryExcerptRow();
    expectedExcerptRow.setOperationalTableData(historyTableRow.getOperationalTableData());
    expectedExcerptRow.setUserInfo(MOCK_USER_INFO);
    var expectedDdmInfo = new HistoryExcerptRowDdmInfo();
    expectedDdmInfo.setSystem(SYSTEM_NAME);
    expectedDdmInfo.setApplication(APPLICATION_NAME);
    expectedDdmInfo.setDmlOp(historyTableRow.getDdmInfo().getDmlOp());
    expectedDdmInfo.setCreatedAt(CREATED_AT_DATETIME.toString());
    expectedDdmInfo.setCreatedBy(historyTableRow.getDdmInfo().getCreatedBy());
    expectedDdmInfo.setBusinessActivity(historyTableRow.getDdmInfo().getBusinessActivity());
    expectedDdmInfo.setBusinessProcessId(
        historyTableRow.getDdmInfo().getBusinessProcessId().toString());
    expectedExcerptRow.setDdmInfo(expectedDdmInfo);
    expectedExcerptData.setExcerptRows(Collections.singletonList(expectedExcerptRow));
    return expectedExcerptData;
  }
}
