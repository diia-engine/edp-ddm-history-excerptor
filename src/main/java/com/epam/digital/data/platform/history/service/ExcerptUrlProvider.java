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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExcerptUrlProvider {

  private static final String EXCERPT_RETRIEVE_URL_PATTERN = "%s/excerpts/%s";

  private final String excerptUrl;

  public ExcerptUrlProvider(@Value("${excerpt.url}") String excerptUrl) {
    this.excerptUrl = excerptUrl;
  }

  public String getRetrieveExcerptUrl(UUID id) {
    return String.format(EXCERPT_RETRIEVE_URL_PATTERN, excerptUrl, id);
  }
}
