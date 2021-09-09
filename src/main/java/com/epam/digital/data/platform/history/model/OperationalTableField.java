package com.epam.digital.data.platform.history.model;

import java.util.Objects;

public class OperationalTableField {
  private String value;
  private OperationalTableFieldType type;

  public OperationalTableField() {}

  public OperationalTableField(String value, OperationalTableFieldType type) {
    this.value = value;
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public OperationalTableFieldType getType() {
    return type;
  }

  public void setType(OperationalTableFieldType type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    OperationalTableField that = (OperationalTableField) o;
    return Objects.equals(value, that.value) && type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, type);
  }

  @Override
  public String toString() {
    return "OperationalTableField{" + "data='" + value + '\'' + ", type=" + type + '}';
  }
}
