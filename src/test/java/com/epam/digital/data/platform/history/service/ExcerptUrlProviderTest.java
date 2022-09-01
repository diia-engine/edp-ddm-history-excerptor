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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ExcerptUrlProviderTest {

  private static final String EXCERPT_URL = "http://url";

  private ExcerptUrlProvider excerptUrlProvider;

  @BeforeEach
  void beforeEach() {
    excerptUrlProvider = new ExcerptUrlProvider(EXCERPT_URL);
  }

  @Test
  void expectValidRetrieveUrlBuilt() {
    var id = UUID.fromString("3cc262c1-0cd8-4d45-be66-eb0fca821e0a");

    var actual = excerptUrlProvider.getRetrieveExcerptUrl(id);

    assertThat(actual).isEqualTo(EXCERPT_URL + "/excerpts/" + id.toString());
  }
}
