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

public class HistoryExcerptRowDdmInfo {
  private String createdAt;
  private String createdBy;
  private String dmlOp;
  private String system;
  private String application;
  private String businessProcessId;
  private String businessActivity;

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getDmlOp() {
    return dmlOp;
  }

  public void setDmlOp(String dmlOp) {
    this.dmlOp = dmlOp;
  }

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public String getApplication() {
    return application;
  }

  public void setApplication(String application) {
    this.application = application;
  }

  public String getBusinessProcessId() {
    return businessProcessId;
  }

  public void setBusinessProcessId(String businessProcessId) {
    this.businessProcessId = businessProcessId;
  }

  public String getBusinessActivity() {
    return businessActivity;
  }

  public void setBusinessActivity(String businessActivity) {
    this.businessActivity = businessActivity;
  }
}
