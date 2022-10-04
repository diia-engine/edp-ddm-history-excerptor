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

import com.epam.digital.data.platform.dso.api.dto.OwnerResponseDto;
import com.epam.digital.data.platform.dso.api.dto.VerificationRequestDto;
import com.epam.digital.data.platform.dso.client.DigitalSignatureRestClient;
import com.epam.digital.data.platform.dso.client.exception.BadRequestException;
import com.epam.digital.data.platform.dso.client.exception.InternalServerErrorException;
import com.epam.digital.data.platform.dso.client.exception.SignatureValidationException;
import com.epam.digital.data.platform.history.model.HistoryTableRowDdmInfo;
import com.epam.digital.data.platform.history.model.UserInfo;
import com.epam.digital.data.platform.storage.form.service.FormDataStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserInfoRetrieveService {

  private static final UserInfo EMPTY_USER_INFO = new UserInfo();

  private final Logger log = LoggerFactory.getLogger(UserInfoRetrieveService.class);

  private final boolean signatureEnabled;
  private final ObjectMapper objectMapper;
  private final DigitalSignatureRestClient digitalSignatureRestClient;
  private final FormDataStorageService historicSignatureFormDataStorageService;

  public UserInfoRetrieveService(
      @Value("${signature.enabled}") boolean signatureEnabled,
      ObjectMapper objectMapper,
      DigitalSignatureRestClient digitalSignatureRestClient,
      FormDataStorageService historicSignatureFormDataStorageService) {
    this.signatureEnabled = signatureEnabled;
    this.objectMapper = objectMapper;
    this.digitalSignatureRestClient = digitalSignatureRestClient;
    this.historicSignatureFormDataStorageService = historicSignatureFormDataStorageService;
  }

  public UserInfo getUserInfo(HistoryTableRowDdmInfo tableDdmInfo) {
    if (!signatureEnabled) {
      log.info("Signature processing is disabled");
      return EMPTY_USER_INFO;
    }
    var key = tableDdmInfo.getDigitalSign();
    if (key == null) {
      log.error("Signature not saved. Key == null");
      return EMPTY_USER_INFO;
    }
    var content = historicSignatureFormDataStorageService.getFormData(key);
    if (content.isEmpty()) {
      log.error("Signature not found for key {}", key);
      return EMPTY_USER_INFO;
    }

    String signature;
    String data;
    try {
      var formDataDto = content.get();
      data = objectMapper.writeValueAsString(formDataDto.getData());
      signature = formDataDto.getSignature();
    } catch (JsonProcessingException e) {
      throw new RuntimeJsonMappingException(e.getMessage());
    }

    OwnerResponseDto owner;
    try {
      owner = digitalSignatureRestClient.getOwnerInfinite(
          new VerificationRequestDto(signature, data));
    } catch (BadRequestException e) {
      log.error("Bad request. {}", e.getErrorDto().getMessage());
      return EMPTY_USER_INFO;
    } catch (InternalServerErrorException e) {
      log.error("Internal server error. {}", e.getErrorDto().getMessage());
      return EMPTY_USER_INFO;
    } catch (SignatureValidationException e) {
      log.error("Signature validation error. {}", e.getErrorDto().getMessage());
      return EMPTY_USER_INFO;
    }

    if (owner == null) {
      log.error("Failed to get the owner of the digital signature");
      return EMPTY_USER_INFO;
    }

    return new UserInfo(owner.getFullName(), owner.getDrfo(), owner.getEdrpou());
  }
}
