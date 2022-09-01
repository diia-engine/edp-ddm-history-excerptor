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

package com.epam.digital.data.platform.history;

import com.epam.digital.data.platform.history.service.HistoryExcerptCreationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoryExcerptorApplicationTest {

  private static final String TABLE_NAME = "table";
  private static final String ENTITY_ID = "bd807a28-986a-4cc7-99bc-3a0511c20bfc";

  private HistoryExcerptorApplication historyExcerptorApplication;

  @Mock
  private HistoryExcerptCreationService historyExcerptCreationService;

  @Mock
  private ApplicationArguments applicationArguments;

  @BeforeEach
  void beforeEach() {
    historyExcerptorApplication = new HistoryExcerptorApplication(historyExcerptCreationService);
  }

  @Test
  void expectExceptionWhenNoTableName() {
    when(applicationArguments.getOptionValues("tableName"))
            .thenReturn(null);

    assertThrows(
        IllegalArgumentException.class,
        () -> historyExcerptorApplication.run(applicationArguments));
  }

  @Test
  void expectExceptionWhenTwoIds() {
    when(applicationArguments.getOptionValues("tableName"))
            .thenReturn(Collections.singletonList(TABLE_NAME));
    when(applicationArguments.getOptionValues("id"))
        .thenReturn(
            List.of(
                "bd807a28-986a-4cc7-99bc-3a0511c20bfc", "ed807a28-986a-4cc7-99bc-3a0511c20bfb"));

    assertThrows(
            IllegalArgumentException.class,
            () -> historyExcerptorApplication.run(applicationArguments));
  }

  @Test
  void expectExceptionWhenNotUuidEntityId() {
    when(applicationArguments.getOptionValues("tableName"))
            .thenReturn(Collections.singletonList(TABLE_NAME));
    when(applicationArguments.getOptionValues("id"))
            .thenReturn(Collections.singletonList("1"));

    assertThrows(
            IllegalArgumentException.class,
            () -> historyExcerptorApplication.run(applicationArguments));
  }

  @Test
  void expectHistoryExcerptGenerationStarted() throws Exception {
    when(applicationArguments.getOptionValues("tableName"))
            .thenReturn(Collections.singletonList(TABLE_NAME));
    when(applicationArguments.getOptionValues("id"))
            .thenReturn(Collections.singletonList(ENTITY_ID));

    historyExcerptorApplication.run(applicationArguments);

    verify(historyExcerptCreationService).createExcerpt(TABLE_NAME, UUID.fromString(ENTITY_ID));
  }
}

