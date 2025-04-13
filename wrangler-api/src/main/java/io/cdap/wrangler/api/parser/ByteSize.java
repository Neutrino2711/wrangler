/*
 * Copyright © 2023 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.cdap.wrangler.api.annotations.PublicEvolving;

/**
 * A token representing byte sizes with units (e.g., "10KB", "1.5MB").
 */
@PublicEvolving
public class ByteSize implements Token {
  private final double value;
  private final String unit;
  private final long bytes;

  public ByteSize(String text) {
    // Parse a string like "10KB"
    String trimmedText = text.trim();
    // Find where the numeric part ends and the unit begins
    int i;
    for (i = 0; i < trimmedText.length(); i++) {
      char c = trimmedText.charAt(i);
      if (!Character.isDigit(c) && c != '.') {
        break;
      }
    }

    // Extract the numeric part and unit
    String valueStr = trimmedText.substring(0, i);
    this.unit = trimmedText.substring(i).toUpperCase();
    this.value = Double.parseDouble(valueStr);

    // Convert to bytes
    this.bytes = toBytes(this.value, this.unit);
  }

  /**
   * @return the raw numeric value before unit conversion
   */
  public double getValue() {
    return value;
  }

  /**
   * @return the unit string (e.g., "KB", "MB")
   */
  public String getUnit() {
    return unit;
  }

  /**
   * @return the value converted to bytes
   */
  public long getBytes() {
    return bytes;
  }

  /**
   * Converts a value with a unit to bytes.
   *
   * @param value the numeric value
   * @param unit  the unit (B, KB, MB, GB, TB)
   * @return the value in bytes
   */
  private long toBytes(double value, String unit) {
    switch (unit) {
      case "B":
        return (long) value;
      case "KB":
        return (long) (value * 1024);
      case "MB":
        return (long) (value * 1024 * 1024);
      case "GB":
        return (long) (value * 1024 * 1024 * 1024);
      case "TB":
        return (long) (value * 1024 * 1024 * 1024 * 1024);
      default:
        throw new IllegalArgumentException("Unsupported byte unit: " + unit);
    }
  }

  @Override
  public Object value() {
    return bytes;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", "byte_size");
    object.addProperty("value", value);
    object.addProperty("unit", unit);
    object.addProperty("bytes", bytes);
    return object;
  }

  @Override
  public String toString() {
    return value + unit;
  }
}
