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
import com.epam.digital.data.platform.integration.ceph.service.CephService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DigitalSignatureService {

  private final String requestSignatureBucket;

  private final Logger log = LoggerFactory.getLogger(DigitalSignatureService.class);

  private final ObjectMapper objectMapper;
  private final DigitalSealRestClient digitalSealRestClient;
  private final CephService requestSignatureCephService;

  public DigitalSignatureService(
      @Value("${request-signature-ceph.bucket}") String requestSignatureBucket,
      ObjectMapper objectMapper,
      DigitalSealRestClient digitalSealRestClient,
      CephService requestSignatureCephService) {
    this.requestSignatureBucket = requestSignatureBucket;
    this.objectMapper = objectMapper;
    this.digitalSealRestClient = digitalSealRestClient;
    this.requestSignatureCephService = requestSignatureCephService;
  }

  public <I> String sign(I input) {
    var signRequestDto = new SignRequestDto();
    try {
      signRequestDto.setData(objectMapper.writeValueAsString(input));
      log.info("Signing data");
      var signResponse = digitalSealRestClient.sign(signRequestDto);
      return objectMapper.writeValueAsString(signResponse);
    } catch (JsonProcessingException e) {
      throw new RuntimeJsonMappingException(e.getMessage());
    }
  }

  public String saveSignature(String value) {
    var key = UUID.randomUUID().toString();
    log.info("Storing to ceph");
    log.debug("Generated ceph key: {}", key);
    requestSignatureCephService.putContent(requestSignatureBucket, key, value);
    return key;
  }
}
