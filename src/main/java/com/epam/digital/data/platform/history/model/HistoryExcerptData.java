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

package com.epam.digital.data.platform.history.model;

import java.util.List;

public class HistoryExcerptData {

  private List<String> operationalTableFields;
  private List<HistoryExcerptRow> excerptRows;

  public HistoryExcerptData() {
  }

  public HistoryExcerptData(List<String> operationalTableFields, List<HistoryExcerptRow> excerptRows) {
    this.operationalTableFields = operationalTableFields;
    this.excerptRows = excerptRows;
  }

  public List<String> getOperationalTableFields() {
    return operationalTableFields;
  }

  public void setOperationalTableFields(List<String> operationalTableFields) {
    this.operationalTableFields = operationalTableFields;
  }

  public List<HistoryExcerptRow> getExcerptRows() {
    return excerptRows;
  }

  public void setExcerptRows(List<HistoryExcerptRow> excerptRows) {
    this.excerptRows = excerptRows;
  }
}
