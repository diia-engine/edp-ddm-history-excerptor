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

package com.epam.digital.data.platform.history.util;

import java.util.Set;

public final class HistoryTableUtil {

  public static final String DDM_CREATED_AT_COLUMN = "ddm_created_at";
  public static final String DDM_CREATED_BY_COLUMN = "ddm_created_by";
  public static final String DDM_DML_OP_COLUMN = "ddm_dml_op";
  public static final String DDM_SYSTEM_ID_COLUMN = "ddm_system_id";
  public static final String DDM_APPLICATION_ID_COLUMN = "ddm_application_id";
  public static final String DDM_BUSINESS_PROCESS_ID_COLUMN = "ddm_business_process_id";
  public static final String DDM_BUSINESS_PROCESS_DEFINITION_ID_COLUMN = "ddm_business_process_definition_id";
  public static final String DDM_BUSINESS_PROCESS_INSTANCE_ID_COLUMN = "ddm_business_process_instance_id";
  public static final String DDM_BUSINESS_ACTIVITY_COLUMN = "ddm_business_activity";
  public static final String DDM_BUSINESS_ACTIVITY_INSTANCE_ID_COLUMN = "ddm_business_activity_instance_id";
  public static final String DDM_DIGITAL_SIGN_COLUMN = "ddm_digital_sign";
  public static final String DDM_DIGITAL_SIGN_DERIVED_COLUMN = "ddm_digital_sign_derived";
  public static final String DDM_DIGITAL_SIGN_CHECKSUM_COLUMN = "ddm_digital_sign_checksum";
  public static final String DDM_DIGITAL_SIGN_DERIVED_CHECKSUM_COLUMN = "ddm_digital_sign_derived_checksum";

  public static final Set<String> DDM_COLUMNS =
      Set.of(
          DDM_CREATED_AT_COLUMN,
          DDM_CREATED_BY_COLUMN,
          DDM_DML_OP_COLUMN,
          DDM_SYSTEM_ID_COLUMN,
          DDM_APPLICATION_ID_COLUMN,
          DDM_BUSINESS_PROCESS_ID_COLUMN,
          DDM_BUSINESS_PROCESS_DEFINITION_ID_COLUMN,
          DDM_BUSINESS_PROCESS_INSTANCE_ID_COLUMN,
          DDM_BUSINESS_ACTIVITY_COLUMN,
          DDM_BUSINESS_ACTIVITY_INSTANCE_ID_COLUMN,
          DDM_DIGITAL_SIGN_COLUMN,
          DDM_DIGITAL_SIGN_DERIVED_COLUMN,
          DDM_DIGITAL_SIGN_CHECKSUM_COLUMN,
          DDM_DIGITAL_SIGN_DERIVED_CHECKSUM_COLUMN);

  private HistoryTableUtil() {}
}
