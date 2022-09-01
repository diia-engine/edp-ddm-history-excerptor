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
import com.epam.digital.data.platform.integration.ceph.service.CephService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserInfoRetrieveServiceTest {

  private static final UserInfo EMPTY_USER_INFO = new UserInfo();
  private static final ErrorDto ERROR_DTO = new ErrorDto("code", "msg", "local_msg");
  private static final String HISTORIC_BUCKET = "bucket";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private static final String DATA = "{\"data\":\"test_data\"}";
  private static final String SIGNATURE = "test_signature";
  private static final String CEPH_KEY = "ceph_key";
  private static final String CEPH_VALUE =
      "{\"data\":" + DATA + ",\"signature\":\"" + SIGNATURE + "\"}";

  private UserInfoRetrieveService userInfoRetrieveService;

  private HistoryTableRowDdmInfo historyTableRowDdmInfo;

  @Mock
  private DigitalSignatureRestClient digitalSignatureRestClient;
  @Mock
  private CephService historicSignatureCephService;

  @BeforeEach
  void beforeEach() {
    userInfoRetrieveService = new UserInfoRetrieveService(HISTORIC_BUCKET, true, OBJECT_MAPPER,
        digitalSignatureRestClient, historicSignatureCephService);

    historyTableRowDdmInfo = new HistoryTableRowDdmInfo();
    historyTableRowDdmInfo.setDigitalSign(CEPH_KEY);
  }

  @Test
  void shouldEnrichWithEmptyUserInfoWhenThereIsNoFileInCephBucket() {
    when(historicSignatureCephService.getAsString(HISTORIC_BUCKET, CEPH_KEY))
        .thenReturn(Optional.empty());
    assertEmptyUserInfo();
  }

  @Test
  void shouldEnrichWithEmptyUserInfoWhenInvalidSignature() {
    when(historicSignatureCephService.getAsString(HISTORIC_BUCKET, CEPH_KEY))
            .thenReturn(Optional.of(CEPH_VALUE));
    assertEmptyUserInfo();
  }

  @Test
  void shouldEnrichWithCorrectUserInfo() {
    when(historicSignatureCephService.getAsString(HISTORIC_BUCKET, CEPH_KEY))
            .thenReturn(Optional.of(CEPH_VALUE));
    when(digitalSignatureRestClient.getOwnerInfinite(new VerificationRequestDto(SIGNATURE, DATA)))
        .thenReturn(new OwnerResponseDto("name", "drfo", "edrpou"));

    var userInfo = userInfoRetrieveService.getUserInfo(historyTableRowDdmInfo);

    assertThat(userInfo).isEqualTo(new UserInfo("name", "drfo", "edrpou"));
  }

  @Test
  void shouldReturnEmptyUserInfoWhenSignatureDisabled() {
    userInfoRetrieveService = new UserInfoRetrieveService(HISTORIC_BUCKET, false, OBJECT_MAPPER,
            digitalSignatureRestClient, historicSignatureCephService);
    assertEmptyUserInfo();
  }

  @Test
  void shouldReturnEmptyUserInfoWhenBadRequestException() {
    when(historicSignatureCephService.getAsString(HISTORIC_BUCKET, CEPH_KEY))
            .thenReturn(Optional.of(CEPH_VALUE));
    when(digitalSignatureRestClient.getOwnerInfinite(any()))
        .thenThrow(new BadRequestException(ERROR_DTO));
    assertEmptyUserInfo();
  }

  @Test
  void shouldReturnEmptyUserInfoWhenInternalServerErrorException() {
    when(historicSignatureCephService.getAsString(HISTORIC_BUCKET, CEPH_KEY))
            .thenReturn(Optional.of(CEPH_VALUE));
    when(digitalSignatureRestClient.getOwnerInfinite(any()))
        .thenThrow(new InternalServerErrorException(ERROR_DTO));
    assertEmptyUserInfo();
  }

  @Test
  void shouldReturnEmptyUserInfoWhenSignatureValidationException() {
    when(historicSignatureCephService.getAsString(HISTORIC_BUCKET, CEPH_KEY))
            .thenReturn(Optional.of(CEPH_VALUE));
    when(digitalSignatureRestClient.getOwnerInfinite(any()))
        .thenThrow(new SignatureValidationException(ERROR_DTO));
    assertEmptyUserInfo();
  }

  private void assertEmptyUserInfo() {
    var userInfo = userInfoRetrieveService.getUserInfo(historyTableRowDdmInfo);

    assertThat(userInfo).isEqualTo(EMPTY_USER_INFO);
  }
}