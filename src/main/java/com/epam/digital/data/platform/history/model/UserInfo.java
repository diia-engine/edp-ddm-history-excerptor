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

package com.epam.digital.data.platform.history.model;

import java.util.Objects;

public class UserInfo {

  private String fullName;
  private String drfo;
  private String edrpou;

  public UserInfo() {
  }

  public UserInfo(String fullName, String drfo, String edrpou) {
    this.fullName = fullName;
    this.drfo = drfo;
    this.edrpou = edrpou;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getDrfo() {
    return drfo;
  }

  public void setDrfo(String drfo) {
    this.drfo = drfo;
  }

  public String getEdrpou() {
    return edrpou;
  }

  public void setEdrpou(String edrpou) {
    this.edrpou = edrpou;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserInfo userInfo = (UserInfo) o;
    return Objects.equals(fullName, userInfo.fullName) && Objects.equals(drfo,
        userInfo.drfo) && Objects.equals(edrpou, userInfo.edrpou);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fullName, drfo, edrpou);
  }
}
