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
@ConfigurationProperties(prefix = "openshift")
public class OpenshiftProperties {

  private boolean enabled;
  private String namespace;
  private JobProperties parentJob;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public JobProperties getParentJob() {
    return parentJob;
  }

  public void setParentJob(JobProperties parentJob) {
    this.parentJob = parentJob;
  }

  public static class JobProperties {

    private String name;
    private String resultField;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getResultField() {
      return resultField;
    }

    public void setResultField(String resultField) {
      this.resultField = resultField;
    }
  }
}
