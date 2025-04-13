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
 * A token representing time durations with units (e.g., "150ms", "2.5s").
 */
@PublicEvolving
public class TimeDuration implements Token {
  private final double value;
  private final String unit;
  private final long nanos;

  public TimeDuration(String text) {
    // Parse a string like "150ms"
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
    this.unit = trimmedText.substring(i).toLowerCase();
    this.value = Double.parseDouble(valueStr);

    // Convert to nanoseconds
    this.nanos = toNanos(this.value, this.unit);
  }

  /**
   * @return the raw numeric value before unit conversion
   */
  public double getValue() {
    return value;
  }

  /**
   * @return the unit string (e.g., "ms", "s")
   */
  public String getUnit() {
    return unit;
  }

  /**
   * @return the value converted to nanoseconds
   */
  public long getNanos() {
    return nanos;
  }

  /**
   * Converts a value with a unit to nanoseconds.
   *
   * @param value the numeric value
   * @param unit  the unit (ns, us, ms, s, m, h)
   * @return the value in nanoseconds
   */
  private long toNanos(double value, String unit) {
    switch (unit) {
      case "ns":
        return (long) value;
      case "us":
        return (long) (value * 1000);
      case "ms":
        return (long) (value * 1000 * 1000);
      case "s":
        return (long) (value * 1000 * 1000 * 1000);
      case "m":
        return (long) (value * 60 * 1000 * 1000 * 1000);
      case "h":
        return (long) (value * 60 * 60 * 1000 * 1000 * 1000);
      default:
        throw new IllegalArgumentException("Unsupported time unit: " + unit);
    }
  }

  @Override
  public Object value() {
    return nanos;
  }

  @Override
  public TokenType type() {
    return TokenType.TIME_DURATION;
  }

  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", "time_duration");
    object.addProperty("value", value);
    object.addProperty("unit", unit);
    object.addProperty("nanos", nanos);
    return object;
  }

  @Override
  public String toString() {
    return value + unit;
  }
}
