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

import java.time.OffsetDateTime;
import java.util.UUID;

public class HistoryTableRowDdmInfo {
  private OffsetDateTime createdAt;
  private String createdBy;
  private String dmlOp;
  private UUID systemId;
  private UUID applicationId;
  private UUID businessProcessId;
  private String businessProcessDefinitionId;
  private String businessProcessInstanceId;
  private String businessActivity;
  private String businessActivityInstanceId;
  private String digitalSign;
  private String digitalSignDerived;
  private String digitalSignChecksum;
  private String digitalSignDerivedChecksum;

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(OffsetDateTime createdAt) {
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

  public UUID getSystemId() {
    return systemId;
  }

  public void setSystemId(UUID systemId) {
    this.systemId = systemId;
  }

  public UUID getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(UUID applicationId) {
    this.applicationId = applicationId;
  }

  public UUID getBusinessProcessId() {
    return businessProcessId;
  }

  public void setBusinessProcessId(UUID businessProcessId) {
    this.businessProcessId = businessProcessId;
  }

  public String getBusinessProcessDefinitionId() {
    return businessProcessDefinitionId;
  }

  public void setBusinessProcessDefinitionId(String businessProcessDefinitionId) {
    this.businessProcessDefinitionId = businessProcessDefinitionId;
  }

  public String getBusinessProcessInstanceId() {
    return businessProcessInstanceId;
  }

  public void setBusinessProcessInstanceId(String businessProcessInstanceId) {
    this.businessProcessInstanceId = businessProcessInstanceId;
  }

  public String getBusinessActivity() {
    return businessActivity;
  }

  public void setBusinessActivity(String businessActivity) {
    this.businessActivity = businessActivity;
  }

  public String getBusinessActivityInstanceId() {
    return businessActivityInstanceId;
  }

  public void setBusinessActivityInstanceId(String businessActivityInstanceId) {
    this.businessActivityInstanceId = businessActivityInstanceId;
  }

  public String getDigitalSign() {
    return digitalSign;
  }

  public void setDigitalSign(String digitalSign) {
    this.digitalSign = digitalSign;
  }

  public String getDigitalSignDerived() {
    return digitalSignDerived;
  }

  public void setDigitalSignDerived(String digitalSignDerived) {
    this.digitalSignDerived = digitalSignDerived;
  }

  public String getDigitalSignChecksum() {
    return digitalSignChecksum;
  }

  public void setDigitalSignChecksum(String digitalSignChecksum) {
    this.digitalSignChecksum = digitalSignChecksum;
  }

  public String getDigitalSignDerivedChecksum() {
    return digitalSignDerivedChecksum;
  }

  public void setDigitalSignDerivedChecksum(String digitalSignDerivedChecksum) {
    this.digitalSignDerivedChecksum = digitalSignDerivedChecksum;
  }
}
