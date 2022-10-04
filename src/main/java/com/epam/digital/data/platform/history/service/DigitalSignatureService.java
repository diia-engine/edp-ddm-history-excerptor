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

import com.epam.digital.data.platform.dso.api.dto.SignRequestDto;
import com.epam.digital.data.platform.dso.client.DigitalSealRestClient;
import com.epam.digital.data.platform.storage.form.dto.FormDataDto;
import com.epam.digital.data.platform.storage.form.service.FormDataStorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DigitalSignatureService {

  private final Logger log = LoggerFactory.getLogger(DigitalSignatureService.class);

  private final ObjectMapper objectMapper;
  private final DigitalSealRestClient digitalSealRestClient;
  private final FormDataStorageService requestSignatureFormDataStorageService;

  public DigitalSignatureService(
      ObjectMapper objectMapper,
      DigitalSealRestClient digitalSealRestClient,
      FormDataStorageService requestSignatureFormDataStorageService) {
    this.objectMapper = objectMapper;
    this.digitalSealRestClient = digitalSealRestClient;
    this.requestSignatureFormDataStorageService = requestSignatureFormDataStorageService;
  }

  public <I> String sign(I input) {
    var signRequestDto = new SignRequestDto();
    try {
      signRequestDto.setData(objectMapper.writeValueAsString(input));
      log.info("Signing data");
      var signResponse = digitalSealRestClient.sign(signRequestDto);
      return signResponse.getSignature();
    } catch (JsonProcessingException e) {
      throw new RuntimeJsonMappingException(e.getMessage());
    }
  }

  public String saveSignature(String value) {
    var key = UUID.randomUUID().toString();
    log.info("Storing to storage");
    log.debug("Generated storage key: {}", key);
    requestSignatureFormDataStorageService.putFormData(key, FormDataDto.builder().signature(value).build());
    return key;
  }
}
