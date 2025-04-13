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

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link TimeDuration}
 */
public class TimeDurationTest {

  @Test
  public void testParseNanoseconds() {
    TimeDuration td = new TimeDuration("100ns");
    Assert.assertEquals(100, td.getNanos());
    Assert.assertEquals(100.0, td.getValue(), 0.001);
    Assert.assertEquals("ns", td.getUnit());
  }

  @Test
  public void testParseMicroseconds() {
    TimeDuration td = new TimeDuration("10us");
    Assert.assertEquals(10000, td.getNanos());
    Assert.assertEquals(10.0, td.getValue(), 0.001);
    Assert.assertEquals("us", td.getUnit());
  }

  @Test
  public void testParseMilliseconds() {
    TimeDuration td = new TimeDuration("1.5ms");
    Assert.assertEquals(1500000, td.getNanos());
    Assert.assertEquals(1.5, td.getValue(), 0.001);
    Assert.assertEquals("ms", td.getUnit());
  }

  @Test
  public void testParseSeconds() {
    TimeDuration td = new TimeDuration("2s");
    Assert.assertEquals(2000000000, td.getNanos());
    Assert.assertEquals(2.0, td.getValue(), 0.001);
    Assert.assertEquals("s", td.getUnit());
  }

  @Test
  public void testParseMinutes() {
    TimeDuration td = new TimeDuration("0.5m");
    Assert.assertEquals(30000000000L, td.getNanos());
    Assert.assertEquals(0.5, td.getValue(), 0.001);
    Assert.assertEquals("m", td.getUnit());
  }

  @Test
  public void testParseHours() {
    TimeDuration td = new TimeDuration("1h");
    Assert.assertEquals(3600000000000L, td.getNanos());
    Assert.assertEquals(1.0, td.getValue(), 0.001);
    Assert.assertEquals("h", td.getUnit());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUnit() {
    new TimeDuration("10x");
  }

  @Test
  public void testUppercaseUnit() {
    TimeDuration td = new TimeDuration("5MS");
    Assert.assertEquals(5000000, td.getNanos());
    Assert.assertEquals("ms", td.getUnit());
  }
}
