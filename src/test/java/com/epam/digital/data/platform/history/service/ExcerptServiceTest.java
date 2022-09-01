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

import com.epam.digital.data.platform.excerpt.model.ExcerptEntityId;
import com.epam.digital.data.platform.excerpt.model.ExcerptEventDto;
import com.epam.digital.data.platform.excerpt.model.ExcerptProcessingStatus;
import com.epam.digital.data.platform.excerpt.model.StatusDto;
import com.epam.digital.data.platform.history.exception.HistoryExcerptGenerationException;
import com.epam.digital.data.platform.history.util.ThirdPartyHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcerptServiceTest {

  private static final int EXCERPT_STATUS_CHECK_MAX_ATTEMPTS = 2;
  private static final UUID EXCERPT_ID = UUID.fromString("f45f4010-bc09-4c98-8a8b-1aad4fa5a19f");

  private static final String SIGNATURE = "sign";
  private static final String CEPH_KEY = "key";

  private ExcerptService excerptService;

  @Mock
  private ExcerptRestClient excerptRestClient;
  @Mock
  private DigitalSignatureService digitalSignatureService;
  @Mock
  private ThreadSleepService threadSleepService;

  @Captor
  private ArgumentCaptor<Map<String, Object>> requestHeadersCaptor;

  @BeforeEach
  void beforeEach() {
    excerptService =
        new ExcerptService(
            EXCERPT_STATUS_CHECK_MAX_ATTEMPTS,
            true,
            excerptRestClient,
            digitalSignatureService,
            threadSleepService);
  }

  @Test
  void expectValidRequestToRestClient() {
    when(excerptRestClient.generate(any(), any())).thenReturn(new ExcerptEntityId(EXCERPT_ID));
    when(digitalSignatureService.sign(any())).thenReturn(SIGNATURE);
    when(digitalSignatureService.saveSignature(SIGNATURE)).thenReturn(CEPH_KEY);

    var eventDto = new ExcerptEventDto();

    var actual = excerptService.generate(eventDto);

    verify(excerptRestClient).generate(eq(eventDto), requestHeadersCaptor.capture());
    var actualHeaders = requestHeadersCaptor.getValue();
    assertThat(actualHeaders)
        .hasSize(2)
        .containsEntry(ThirdPartyHeader.X_DIGITAL_SIGNATURE.getHeaderName(), CEPH_KEY)
        .containsEntry(ThirdPartyHeader.X_DIGITAL_SIGNATURE_DERIVED.getHeaderName(), CEPH_KEY);

    assertThat(actual).isEqualTo(EXCERPT_ID);
  }

  @Test
  void expectExcerptCallWithoutSignHeadersIfDisabled() {
    excerptService =
            new ExcerptService(
                    EXCERPT_STATUS_CHECK_MAX_ATTEMPTS,
                    false,
                    excerptRestClient,
                    digitalSignatureService,
                    threadSleepService);
    when(excerptRestClient.generate(any(), any())).thenReturn(new ExcerptEntityId(EXCERPT_ID));

    var eventDto = new ExcerptEventDto();

    var actual = excerptService.generate(eventDto);

    verifyNoInteractions(digitalSignatureService);
    verify(excerptRestClient).generate(eq(eventDto), requestHeadersCaptor.capture());
    var actualHeaders = requestHeadersCaptor.getValue();
    assertThat(actualHeaders).isEmpty();

    assertThat(actual).isEqualTo(EXCERPT_ID);
  }

  @Test
  void expectStatusReturnedWhenNotInProgress() throws InterruptedException {
    var expectedStatus = new StatusDto(ExcerptProcessingStatus.COMPLETED, "");
    when(excerptRestClient.status(EXCERPT_ID)).thenReturn(expectedStatus);

    var actual = excerptService.getFinalProcessingStatus(EXCERPT_ID);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expectedStatus);
  }

  @Test
  void expectTimeoutUntilNotInProgress() throws InterruptedException {
    var expectedStatus = new StatusDto(ExcerptProcessingStatus.FAILED, "");

    when(excerptRestClient.status(EXCERPT_ID))
        .thenReturn(new StatusDto(ExcerptProcessingStatus.IN_PROGRESS, ""))
        .thenReturn(expectedStatus);

    var actual = excerptService.getFinalProcessingStatus(EXCERPT_ID);

    assertThat(actual).usingRecursiveComparison().isEqualTo(expectedStatus);

    verify(threadSleepService).sleep(5);
  }

  @Test
  void expectExceptionIfStatusCheckExceedsMaxAttempts() {
    var inProgressStatus = new StatusDto(ExcerptProcessingStatus.IN_PROGRESS, "");
    when(excerptRestClient.status(EXCERPT_ID)).thenReturn(inProgressStatus);

    assertThrows(
        HistoryExcerptGenerationException.class,
        () -> excerptService.getFinalProcessingStatus(EXCERPT_ID));
  }
}
