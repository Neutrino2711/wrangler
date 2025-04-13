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

package io.cdap.directives.aggregator;

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.RecipeException;
import io.cdap.wrangler.api.Row;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests {@link AggregateStats}
 */
public class AggregateStatsTest {

  @Test
  public void testBasicAggregation() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_mb total_time_sec"
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "100KB").add("response_time", "200ms"));
    rows.add(new Row("data_transfer_size", "200KB").add("response_time", "300ms"));
    rows.add(new Row("data_transfer_size", "1.5MB").add("response_time", "1.5s"));

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());

    // 100KB + 200KB + 1.5MB = 1.8MB approximately
    double expectedSizeMB = (100 * 1024 + 200 * 1024 + 1.5 * 1024 * 1024) / (1024.0 * 1024.0);
    // 200ms + 300ms + 1.5s = 2s
    double expectedTimeSec = (200 * 1000000 + 300 * 1000000 + 1.5 * 1000000000) / 1000000000.0;

    Assert.assertEquals(expectedSizeMB, (Double) results.get(0).getValue("total_size_mb"), 0.001);
    Assert.assertEquals(expectedTimeSec, (Double) results.get(0).getValue("total_time_sec"), 0.001);
  }

  @Test
  public void testCustomUnits() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_kb total_time_ms KB ms"
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "1MB").add("response_time", "2s"));
    rows.add(new Row("data_transfer_size", "2MB").add("response_time", "3s"));

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());

    // 1MB + 2MB = 3MB = 3072KB
    double expectedSizeKB = 3072;
    // 2s + 3s = 5s = 5000ms
    double expectedTimeMs = 5000;

    Assert.assertEquals(expectedSizeKB, (Double) results.get(0).getValue("total_size_kb"), 0.001);
    Assert.assertEquals(expectedTimeMs, (Double) results.get(0).getValue("total_time_ms"), 0.001);
  }

  @Test
  public void testEmptyInput() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_mb total_time_sec"
    };

    List<Row> rows = new ArrayList<>();
    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());
    Assert.assertEquals(0.0, (Double) results.get(0).getValue("total_size_mb"), 0.001);
    Assert.assertEquals(0.0, (Double) results.get(0).getValue("total_time_sec"), 0.001);
  }

  @Test
  public void testMissingColumns() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_mb total_time_sec"
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "100KB")); // Missing response_time
    rows.add(new Row("response_time", "300ms")); // Missing data_transfer_size

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());

    // 100KB = 0.098MB
    double expectedSizeMB = 100 * 1024 / (1024.0 * 1024.0);
    // 300ms = 0.3s
    double expectedTimeSec = 300 * 1000000 / 1000000000.0;

    Assert.assertEquals(expectedSizeMB, (Double) results.get(0).getValue("total_size_mb"), 0.001);
    Assert.assertEquals(expectedTimeSec, (Double) results.get(0).getValue("total_time_sec"), 0.001);
  }

  @Test
  public void testLargeValues() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_gb total_time_m GB m"
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "1TB").add("response_time", "1h"));
    rows.add(new Row("data_transfer_size", "500GB").add("response_time", "30m"));

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());

    // 1TB + 500GB = 1024GB + 500GB = 1524GB
    double expectedSizeGB = 1024 + 500;
    // 1h + 30m = 60m + 30m = 90m
    double expectedTimeMin = 90;

    Assert.assertEquals(expectedSizeGB, (Double) results.get(0).getValue("total_size_gb"), 0.001);
    Assert.assertEquals(expectedTimeMin, (Double) results.get(0).getValue("total_time_m"), 0.001);
  }

  @Test
  public void testMixedUnitCases() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_mb total_time_sec"
    };

    List<Row> rows = new ArrayList<>();
    // Test different unit case variations (uppercase, lowercase, mixed case)
    rows.add(new Row("data_transfer_size", "100kb").add("response_time", "200MS"));
    rows.add(new Row("data_transfer_size", "200KB").add("response_time", "300ms"));
    rows.add(new Row("data_transfer_size", "1.5Mb").add("response_time", "1.5S"));

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());

    // 100KB + 200KB + 1.5MB = 1.8MB approximately
    double expectedSizeMB = (100 * 1024 + 200 * 1024 + 1.5 * 1024 * 1024) / (1024.0 * 1024.0);
    // 200ms + 300ms + 1.5s = 2s
    double expectedTimeSec = (200 * 1000000 + 300 * 1000000 + 1.5 * 1000000000) / 1000000000.0;

    Assert.assertEquals(expectedSizeMB, (Double) results.get(0).getValue("total_size_mb"), 0.001);
    Assert.assertEquals(expectedTimeSec, (Double) results.get(0).getValue("total_time_sec"), 0.001);
  }

  @Test
  public void testZeroValues() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_mb total_time_sec"
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "0KB").add("response_time", "0ms"));
    rows.add(new Row("data_transfer_size", "0MB").add("response_time", "0s"));

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());
    Assert.assertEquals(0.0, (Double) results.get(0).getValue("total_size_mb"), 0.001);
    Assert.assertEquals(0.0, (Double) results.get(0).getValue("total_time_sec"), 0.001);
  }

  @Test
  public void testMicroUnits() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_mb total_time_us MB us"
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "1KB").add("response_time", "500ns"));
    rows.add(new Row("data_transfer_size", "2KB").add("response_time", "1500ns"));

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());

    // 1KB + 2KB = 3KB = 0.00293MB
    double expectedSizeMB = 3 * 1024 / (1024.0 * 1024.0);
    // 500ns + 1500ns = 2000ns = 2us
    double expectedTimeUs = 2;

    Assert.assertEquals(expectedSizeMB, (Double) results.get(0).getValue("total_size_mb"), 0.001);
    Assert.assertEquals(expectedTimeUs, (Double) results.get(0).getValue("total_time_us"), 0.001);
  }

  @Test
  public void testNanoAndTeraUnits() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_tb total_time_ns TB ns"
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "0.5TB").add("response_time", "1ms"));
    rows.add(new Row("data_transfer_size", "0.25TB").add("response_time", "2ms"));

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());

    // 0.5TB + 0.25TB = 0.75TB
    double expectedSizeTB = 0.75;
    // 1ms + 2ms = 3ms = 3,000,000ns
    double expectedTimeNs = 3000000;

    Assert.assertEquals(expectedSizeTB, (Double) results.get(0).getValue("total_size_tb"), 0.001);
    Assert.assertEquals(expectedTimeNs, (Double) results.get(0).getValue("total_time_ns"), 0.001);
  }

  @Test
  public void testDecimalPrecision() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_mb total_time_sec"
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "1.33KB").add("response_time", "2.67ms"));
    rows.add(new Row("data_transfer_size", "4.25KB").add("response_time", "3.89ms"));
    rows.add(new Row("data_transfer_size", "0.42KB").add("response_time", "1.45ms"));

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());

    // 1.33KB + 4.25KB + 0.42KB = 6KB = 0.00586MB
    double expectedSizeMB = (1.33 + 4.25 + 0.42) * 1024 / (1024.0 * 1024.0);
    // 2.67ms + 3.89ms + 1.45ms = 8.01ms = 0.00801s
    double expectedTimeSec = (2.67 + 3.89 + 1.45) * 1000000 / 1000000000.0;

    Assert.assertEquals(expectedSizeMB, (Double) results.get(0).getValue("total_size_mb"), 0.001);
    Assert.assertEquals(expectedTimeSec, (Double) results.get(0).getValue("total_time_sec"), 0.001);
  }

  @Test
  public void testAllByteSizeUnits() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_b total_time_sec B s"
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "10B").add("response_time", "1s"));
    rows.add(new Row("data_transfer_size", "2KB").add("response_time", "0.5s"));
    rows.add(new Row("data_transfer_size", "0.003MB").add("response_time", "0.25s"));
    rows.add(new Row("data_transfer_size", "0.000004GB").add("response_time", "0.1s"));
    rows.add(new Row("data_transfer_size", "0.000000005TB").add("response_time", "0.05s"));

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());

    // Calculate expected total in bytes:
    // 10B + 2KB + 0.003MB + 0.000004GB + 0.000000005TB
    double expectedSizeB = 10 + // 10B
        (2 * 1024) + // 2KB
        (0.003 * 1024 * 1024) + // 0.003MB
        (0.000004 * 1024 * 1024 * 1024) + // 0.000004GB
        (0.000000005 * 1024 * 1024 * 1024 * 1024); // 0.000000005TB

    // 1s + 0.5s + 0.25s + 0.1s + 0.05s = 1.9s
    double expectedTimeSec = 1.9;

    Assert.assertEquals(expectedSizeB, (Double) results.get(0).getValue("total_size_b"), 0.001);
    Assert.assertEquals(expectedTimeSec, (Double) results.get(0).getValue("total_time_sec"), 0.001);
  }

  @Test
  public void testAllTimeDurationUnits() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_kb total_time_ns KB ns"
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "1KB").add("response_time", "10ns"));
    rows.add(new Row("data_transfer_size", "1KB").add("response_time", "20us"));
    rows.add(new Row("data_transfer_size", "1KB").add("response_time", "30ms"));
    rows.add(new Row("data_transfer_size", "1KB").add("response_time", "0.04s"));
    rows.add(new Row("data_transfer_size", "1KB").add("response_time", "0.05m"));
    rows.add(new Row("data_transfer_size", "1KB").add("response_time", "0.06h"));

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());

    // 6 * 1KB = 6KB
    double expectedSizeKB = 6;

    // Calculate expected total in nanoseconds:
    // 10ns + 20us + 30ms + 0.04s + 0.05m + 0.06h
    double expectedTimeNs = 10 + // 10ns
        (20 * 1000) + // 20us
        (30 * 1000 * 1000) + // 30ms
        (0.04 * 1000 * 1000 * 1000) + // 0.04s
        (0.05 * 60 * 1000 * 1000 * 1000) + // 0.05m
        (0.06 * 60 * 60 * 1000 * 1000 * 1000); // 0.06h

    Assert.assertEquals(expectedSizeKB, (Double) results.get(0).getValue("total_size_kb"), 0.001);
    Assert.assertEquals(expectedTimeNs, (Double) results.get(0).getValue("total_time_ns"), 0.001);
  }

  @Test
  public void testRealisticLogData() throws Exception {
    // Create a more realistic dataset that resembles server logs
    String[] directives = new String[] {
        "aggregate-stats size response_time total_data_mb avg_response_sec MB s"
    };

    List<Row> rows = new ArrayList<>();
    // Simulate 10 API requests with varying data sizes and response times
    rows.add(new Row("req_id", "1").add("endpoint", "/api/users").add("size", "45KB").add("response_time", "120ms"));
    rows.add(
        new Row("req_id", "2").add("endpoint", "/api/products").add("size", "1.2MB").add("response_time", "350ms"));
    rows.add(new Row("req_id", "3").add("endpoint", "/api/orders").add("size", "89KB").add("response_time", "210ms"));
    rows.add(new Row("req_id", "4").add("endpoint", "/api/products/details").add("size", "2.5MB").add("response_time",
        "780ms"));
    rows.add(
        new Row("req_id", "5").add("endpoint", "/api/checkout").add("size", "156KB").add("response_time", "450ms"));
    rows.add(new Row("req_id", "6").add("endpoint", "/api/payments").add("size", "78KB").add("response_time", "180ms"));
    rows.add(
        new Row("req_id", "7").add("endpoint", "/api/dashboard").add("size", "3.7MB").add("response_time", "1.2s"));
    rows.add(
        new Row("req_id", "8").add("endpoint", "/api/analytics").add("size", "5.1MB").add("response_time", "1.8s"));
    rows.add(new Row("req_id", "9").add("endpoint", "/api/settings").add("size", "65KB").add("response_time", "90ms"));
    rows.add(new Row("req_id", "10").add("endpoint", "/api/logout").add("size", "12KB").add("response_time", "45ms"));

    List<Row> results = TestingRig.execute(directives, rows);

    Assert.assertEquals(1, results.size());

    // Calculate expected total in MB:
    double totalBytes = 45 * 1024 + // 45KB
        1.2 * 1024 * 1024 + // 1.2MB
        89 * 1024 + // 89KB
        2.5 * 1024 * 1024 + // 2.5MB
        156 * 1024 + // 156KB
        78 * 1024 + // 78KB
        3.7 * 1024 * 1024 + // 3.7MB
        5.1 * 1024 * 1024 + // 5.1MB
        65 * 1024 + // 65KB
        12 * 1024; // 12KB
    double expectedSizeMB = totalBytes / (1024.0 * 1024.0);

    // Calculate expected total in seconds:
    double totalNanos = 120 * 1000000 + // 120ms
        350 * 1000000 + // 350ms
        210 * 1000000 + // 210ms
        780 * 1000000 + // 780ms
        450 * 1000000 + // 450ms
        180 * 1000000 + // 180ms
        1.2 * 1000000000 + // 1.2s
        1.8 * 1000000000 + // 1.8s
        90 * 1000000 + // 90ms
        45 * 1000000; // 45ms
    double expectedTimeSec = totalNanos / 1000000000.0;

    Assert.assertEquals(expectedSizeMB, (Double) results.get(0).getValue("total_data_mb"), 0.001);
    Assert.assertEquals(expectedTimeSec, (Double) results.get(0).getValue("avg_response_sec"), 0.001);
  }

  @Test(expected = RecipeException.class)
  public void testInvalidByteUnit() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_mb total_time_sec XB s" // XB is invalid
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "100KB").add("response_time", "200ms"));

    TestingRig.execute(directives, rows);
  }

  @Test(expected = RecipeException.class)
  public void testInvalidTimeUnit() throws Exception {
    String[] directives = new String[] {
        "aggregate-stats data_transfer_size response_time total_size_mb total_time_sec MB ks" // ks is invalid
    };

    List<Row> rows = new ArrayList<>();
    rows.add(new Row("data_transfer_size", "100KB").add("response_time", "200ms"));

    TestingRig.execute(directives, rows);
  }

  @Test
  public void testSpecificationExample() throws Exception {
    // Following the test case specification provided
    String[] recipe = new String[] {
        "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
    };

    List<Row> rows = new ArrayList<>();
    // Create sample log/transaction data
    rows.add(new Row("data_transfer_size", "512KB").add("response_time", "150ms"));
    rows.add(new Row("data_transfer_size", "2.5MB").add("response_time", "300ms"));
    rows.add(new Row("data_transfer_size", "128KB").add("response_time", "50ms"));
    rows.add(new Row("data_transfer_size", "1.2MB").add("response_time", "500ms"));

    // Expected values calculated as per specification
    double expectedTotalSizeInMB = (512 * 1024 + 2.5 * 1024 * 1024 +
        128 * 1024 + 1.2 * 1024 * 1024) / (1024.0 * 1024.0);

    double expectedTotalTimeInSeconds = (150 * 1000000 + 300 * 1000000 +
        50 * 1000000 + 500 * 1000000) / 1000000000.0;

    List<Row> results = TestingRig.execute(recipe, rows);

    // Assertions following the specification structure
    Assert.assertEquals(1, results.size());
    Assert.assertEquals(expectedTotalSizeInMB,
        (Double) results.get(0).getValue("total_size_mb"), 0.001);
    Assert.assertEquals(expectedTotalTimeInSeconds,
        (Double) results.get(0).getValue("total_time_sec"), 0.001);
  }

  @Test
  public void testWithNonStandardInputValues() throws Exception {
    String[] recipe = new String[] {
        "aggregate-stats :size :time total_size_gb total_time_ms GB ms"
    };

    List<Row> rows = new ArrayList<>();
    // Create data with varying formats and using different cases for units
    rows.add(new Row("size", "10mb").add("time", "1.5S"));
    rows.add(new Row("size", "0.5GB").add("time", "500MS"));
    rows.add(new Row("size", "2048kb").add("time", "0.75s"));

    // Convert to canonical units, then to requested output units
    double expectedTotalSizeInGB = (10 * 1024 * 1024 + 0.5 * 1024 * 1024 * 1024 +
        2048 * 1024) / (1024.0 * 1024.0 * 1024.0);

    double expectedTotalTimeInMs = (1.5 * 1000 * 1000 * 1000 + 500 * 1000 * 1000 +
        0.75 * 1000 * 1000 * 1000) / 1000000.0;

    List<Row> results = TestingRig.execute(recipe, rows);

    Assert.assertEquals(1, results.size());
    Assert.assertEquals(expectedTotalSizeInGB,
        (Double) results.get(0).getValue("total_size_gb"), 0.001);
    Assert.assertEquals(expectedTotalTimeInMs,
        (Double) results.get(0).getValue("total_time_ms"), 0.001);
  }

  @Test
  public void testWithSimulatedServerLogs() throws Exception {
    String[] recipe = new String[] {
        "aggregate-stats :traffic :latency total_traffic_mb avg_latency_ms"
    };

    List<Row> rows = new ArrayList<>();
    // Simulate server logs with endpoint, status code, traffic size, and latency
    rows.add(new Row("endpoint", "/api/users")
        .add("status", 200)
        .add("traffic", "45KB")
        .add("latency", "120ms"));

    rows.add(new Row("endpoint", "/api/products")
        .add("status", 200)
        .add("traffic", "1.2MB")
        .add("latency", "350ms"));

    rows.add(new Row("endpoint", "/api/orders")
        .add("status", 500)
        .add("traffic", "12KB")
        .add("latency", "780ms"));

    double expectedTotalTrafficMB = (45 * 1024 + 1.2 * 1024 * 1024 + 12 * 1024) / (1024.0 * 1024.0);

    List<Row> results = TestingRig.execute(recipe, rows);

    Assert.assertEquals(1, results.size());
    Assert.assertEquals(expectedTotalTrafficMB,
        (Double) results.get(0).getValue("total_traffic_mb"), 0.001);
    // For average, divide by row count
    double expectedAvgLatencyMs = (120 + 350 + 780) / 3.0;
    Assert.assertEquals(expectedAvgLatencyMs,
        (Double) results.get(0).getValue("avg_latency_ms"), 0.001);
  }
}
