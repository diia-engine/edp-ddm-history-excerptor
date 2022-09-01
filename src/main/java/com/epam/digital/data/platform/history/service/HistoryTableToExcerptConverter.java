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
import com.epam.digital.data.platform.history.repository.DdmSourceApplicationRepository;
import com.epam.digital.data.platform.history.repository.DdmSourceSystemRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class HistoryTableToExcerptConverter {

  private final UserInfoRetrieveService userInfoRetrieveService;
  private final DdmSourceApplicationRepository ddmSourceApplicationRepository;
  private final DdmSourceSystemRepository ddmSourceSystemRepository;

  public HistoryTableToExcerptConverter(
      UserInfoRetrieveService userInfoRetrieveService,
      DdmSourceApplicationRepository ddmSourceApplicationRepository,
      DdmSourceSystemRepository ddmSourceSystemRepository) {
    this.userInfoRetrieveService = userInfoRetrieveService;
    this.ddmSourceApplicationRepository = ddmSourceApplicationRepository;
    this.ddmSourceSystemRepository = ddmSourceSystemRepository;
  }

  public HistoryExcerptData convert(HistoryTableData historyTableData) {
    var tableRows = historyTableData.getTableRows();
    var ddmApplicationsMap = getDdmApplicationsMap(tableRows);
    var ddmSystemsMap = getDdmSystemsMap(tableRows);
    var excerptRows =
        tableRows.stream()
            .map(tableRow -> mapRow(tableRow, ddmApplicationsMap, ddmSystemsMap))
            .collect(Collectors.toList());
    return new HistoryExcerptData(historyTableData.getOperationalTableFields(), excerptRows);
  }

  private Map<UUID, String> getDdmApplicationsMap(List<HistoryTableRow> tableRows) {
    var ddmApplicationIds =
        tableRows.stream()
            .map(HistoryTableRow::getDdmInfo)
            .map(HistoryTableRowDdmInfo::getApplicationId)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    return ddmApplicationIds.isEmpty()
        ? Collections.emptyMap()
        : ddmSourceApplicationRepository.findByApplicationIdIn(ddmApplicationIds).stream()
            .collect(
                Collectors.toMap(
                    DdmSourceApplication::getApplicationId,
                    DdmSourceApplication::getApplicationName));
  }

  private Map<UUID, String> getDdmSystemsMap(List<HistoryTableRow> tableRows) {
    var ddmSystemIds =
        tableRows.stream()
            .map(HistoryTableRow::getDdmInfo)
            .map(HistoryTableRowDdmInfo::getSystemId)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    return ddmSystemIds.isEmpty()
        ? Collections.emptyMap()
        : ddmSourceSystemRepository.findBySystemIdIn(ddmSystemIds).stream()
            .collect(
                Collectors.toMap(DdmSourceSystem::getSystemId, DdmSourceSystem::getSystemName));
  }

  private HistoryExcerptRow mapRow(
      HistoryTableRow tableRow,
      Map<UUID, String> ddmApplicationsMap,
      Map<UUID, String> ddmSystemsMap) {
    var excerptRow = new HistoryExcerptRow();
    excerptRow.setOperationalTableData(tableRow.getOperationalTableData());
    excerptRow.setDdmInfo(mapDdmInfo(tableRow.getDdmInfo(), ddmApplicationsMap, ddmSystemsMap));
    excerptRow.setUserInfo(userInfoRetrieveService.getUserInfo(tableRow.getDdmInfo()));
    return excerptRow;
  }

  private HistoryExcerptRowDdmInfo mapDdmInfo(
      HistoryTableRowDdmInfo tableRowDdmInfo,
      Map<UUID, String> ddmApplicationsMap,
      Map<UUID, String> ddmSystemsMap) {
    var excerptRowDdmInfo = new HistoryExcerptRowDdmInfo();
    excerptRowDdmInfo.setCreatedAt(
        Optional.ofNullable(tableRowDdmInfo.getCreatedAt()).map(Objects::toString).orElse(null));
    excerptRowDdmInfo.setCreatedBy(tableRowDdmInfo.getCreatedBy());
    excerptRowDdmInfo.setDmlOp(tableRowDdmInfo.getDmlOp());
    excerptRowDdmInfo.setSystem(ddmSystemsMap.get(tableRowDdmInfo.getSystemId()));
    excerptRowDdmInfo.setApplication(ddmApplicationsMap.get(tableRowDdmInfo.getApplicationId()));
    excerptRowDdmInfo.setBusinessProcessId(
        Optional.ofNullable(tableRowDdmInfo.getBusinessProcessId())
            .map(UUID::toString)
            .orElse(null));
    excerptRowDdmInfo.setBusinessActivity(tableRowDdmInfo.getBusinessActivity());
    return excerptRowDdmInfo;
  }
}
