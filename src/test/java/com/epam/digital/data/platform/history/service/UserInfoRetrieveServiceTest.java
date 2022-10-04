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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.dso.api.dto.ErrorDto;
import com.epam.digital.data.platform.dso.api.dto.OwnerResponseDto;
import com.epam.digital.data.platform.dso.api.dto.VerificationRequestDto;
import com.epam.digital.data.platform.dso.client.DigitalSignatureRestClient;
import com.epam.digital.data.platform.dso.client.exception.BadRequestException;
import com.epam.digital.data.platform.dso.client.exception.InternalServerErrorException;
import com.epam.digital.data.platform.dso.client.exception.SignatureValidationException;
import com.epam.digital.data.platform.history.model.HistoryTableRowDdmInfo;
import com.epam.digital.data.platform.history.model.UserInfo;
import com.epam.digital.data.platform.storage.form.dto.FormDataDto;
import com.epam.digital.data.platform.storage.form.service.FormDataStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserInfoRetrieveServiceTest {

  private static final UserInfo EMPTY_USER_INFO = new UserInfo();
  private static final ErrorDto ERROR_DTO = new ErrorDto("code", "msg", "local_msg");
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private static final String DATA = "{\"data\":\"test_data\"}";
  private static final String SIGNATURE = "test_signature";
  private static final String STORAGE_KEY = "storage_key";
  private static FormDataDto formDataDto;

  private UserInfoRetrieveService userInfoRetrieveService;

  private HistoryTableRowDdmInfo historyTableRowDdmInfo;

  @Mock
  private DigitalSignatureRestClient digitalSignatureRestClient;
  @Mock
  private FormDataStorageService historicSignatureStorageService;

  @BeforeAll
  static void beforeAll() {
    LinkedHashMap<String, Object> data = new LinkedHashMap<>();
    data.put("data", "test_data");
    formDataDto = FormDataDto.builder().data(data).signature(SIGNATURE).build();
  }

  @BeforeEach
  void beforeEach() {
    userInfoRetrieveService = new UserInfoRetrieveService(true, OBJECT_MAPPER,
        digitalSignatureRestClient, historicSignatureStorageService);

    historyTableRowDdmInfo = new HistoryTableRowDdmInfo();
    historyTableRowDdmInfo.setDigitalSign(STORAGE_KEY);
  }

  @Test
  void shouldEnrichWithEmptyUserInfoWhenThereIsNoFileInCephBucket() {
    when(historicSignatureStorageService.getFormData(STORAGE_KEY))
        .thenReturn(Optional.empty());
    assertEmptyUserInfo();
  }

  @Test
  void shouldEnrichWithEmptyUserInfoWhenInvalidSignature() {

    when(historicSignatureStorageService.getFormData(STORAGE_KEY))
            .thenReturn(Optional.of(formDataDto));
    assertEmptyUserInfo();
  }

  @Test
  void shouldEnrichWithCorrectUserInfo() {
    when(historicSignatureStorageService.getFormData(STORAGE_KEY))
            .thenReturn(Optional.of(formDataDto));
    when(digitalSignatureRestClient.getOwnerInfinite(new VerificationRequestDto(SIGNATURE, DATA)))
        .thenReturn(new OwnerResponseDto("name", "drfo", "edrpou"));

    var userInfo = userInfoRetrieveService.getUserInfo(historyTableRowDdmInfo);

    assertThat(userInfo).isEqualTo(new UserInfo("name", "drfo", "edrpou"));
  }

  @Test
  void shouldReturnEmptyUserInfoWhenSignatureDisabled() {
    userInfoRetrieveService = new UserInfoRetrieveService(false, OBJECT_MAPPER,
            digitalSignatureRestClient, historicSignatureStorageService);
    assertEmptyUserInfo();
  }

  @Test
  void shouldReturnEmptyUserInfoWhenBadRequestException() {
    when(historicSignatureStorageService.getFormData(STORAGE_KEY))
            .thenReturn(Optional.of(formDataDto));
    when(digitalSignatureRestClient.getOwnerInfinite(any()))
        .thenThrow(new BadRequestException(ERROR_DTO));
    assertEmptyUserInfo();
  }

  @Test
  void shouldReturnEmptyUserInfoWhenInternalServerErrorException() {
    when(historicSignatureStorageService.getFormData(STORAGE_KEY))
            .thenReturn(Optional.of(formDataDto));
    when(digitalSignatureRestClient.getOwnerInfinite(any()))
        .thenThrow(new InternalServerErrorException(ERROR_DTO));
    assertEmptyUserInfo();
  }

  @Test
  void shouldReturnEmptyUserInfoWhenSignatureValidationException() {
    when(historicSignatureStorageService.getFormData(STORAGE_KEY))
            .thenReturn(Optional.of(formDataDto));
    when(digitalSignatureRestClient.getOwnerInfinite(any()))
        .thenThrow(new SignatureValidationException(ERROR_DTO));
    assertEmptyUserInfo();
  }

  private void assertEmptyUserInfo() {
    var userInfo = userInfoRetrieveService.getUserInfo(historyTableRowDdmInfo);

    assertThat(userInfo).isEqualTo(EMPTY_USER_INFO);
  }
}