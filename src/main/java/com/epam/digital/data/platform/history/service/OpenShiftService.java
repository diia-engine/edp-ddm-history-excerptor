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

import com.epam.digital.data.platform.history.config.properties.OpenshiftProperties;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class OpenShiftService {

  private final OpenshiftProperties openshiftProperties;
  private final Supplier<KubernetesClient> kubernetesClientFactory;

  public OpenShiftService(
      OpenshiftProperties openshiftProperties, Supplier<KubernetesClient> kubernetesClientFactory) {
    this.openshiftProperties = openshiftProperties;
    this.kubernetesClientFactory = kubernetesClientFactory;
  }

  public void updateParentJobWithResultValue(String result) {
    if (openshiftProperties.isEnabled()) {
      KubernetesClient kubernetesClient = null;
      try {
        kubernetesClient = kubernetesClientFactory.get();
        kubernetesClient
                .batch().v1()
                .jobs()
                .withName(openshiftProperties.getParentJob().getName())
                .edit(n -> new JobBuilder(n)
                    .editMetadata()
                      .addToAnnotations(openshiftProperties.getParentJob().getResultField(), result)
                    .endMetadata()
                    .build());
      } finally {
        if (kubernetesClient != null) {
          kubernetesClient.close();
        }
      }
    }
  }
}
