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
 * Tests {@link ByteSize}
 */
public class ByteSizeTest {

  @Test
  public void testParseBytes() {
    ByteSize bs = new ByteSize("100B");
    Assert.assertEquals(100, bs.getBytes());
    Assert.assertEquals(100.0, bs.getValue(), 0.001);
    Assert.assertEquals("B", bs.getUnit());
  }

  @Test
  public void testParseKilobytes() {
    ByteSize bs = new ByteSize("10KB");
    Assert.assertEquals(10240, bs.getBytes());
    Assert.assertEquals(10.0, bs.getValue(), 0.001);
    Assert.assertEquals("KB", bs.getUnit());
  }

  @Test
  public void testParseMegabytes() {
    ByteSize bs = new ByteSize("1.5MB");
    Assert.assertEquals(1572864, bs.getBytes());
    Assert.assertEquals(1.5, bs.getValue(), 0.001);
    Assert.assertEquals("MB", bs.getUnit());
  }

  @Test
  public void testParseGigabytes() {
    ByteSize bs = new ByteSize("2GB");
    Assert.assertEquals(2147483648L, bs.getBytes());
    Assert.assertEquals(2.0, bs.getValue(), 0.001);
    Assert.assertEquals("GB", bs.getUnit());
  }

  @Test
  public void testParseTerabytes() {
    ByteSize bs = new ByteSize("0.1TB");
    Assert.assertEquals(109951162776L, bs.getBytes());
    Assert.assertEquals(0.1, bs.getValue(), 0.001);
    Assert.assertEquals("TB", bs.getUnit());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUnit() {
    new ByteSize("10XB");
  }

  @Test
  public void testLowercaseUnit() {
    ByteSize bs = new ByteSize("5mb");
    Assert.assertEquals(5242880, bs.getBytes());
    Assert.assertEquals("MB", bs.getUnit());
  }
}
