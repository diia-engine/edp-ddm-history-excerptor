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

package com.epam.digital.data.platform.history.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "schema")
public class SchemaProperties {

  private String name;
  private String historyTableSuffix;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHistoryTableSuffix() {
    return historyTableSuffix;
  }

  public void setHistoryTableSuffix(String historyTableSuffix) {
    this.historyTableSuffix = historyTableSuffix;
  }
}
