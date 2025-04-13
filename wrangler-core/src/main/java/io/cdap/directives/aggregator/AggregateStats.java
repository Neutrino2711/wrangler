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

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.DirectiveParseException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Optional;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.TransientVariableScope;
import io.cdap.wrangler.api.annotations.Categories;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A directive for aggregating statistics from byte size and time duration
 * columns.
 */
@Plugin(type = Directive.TYPE)
@Name("aggregate-stats")
@Categories(categories = { "aggregator" })
@Description("Aggregates byte size and time duration statistics.")
public class AggregateStats implements Directive {
  public static final String NAME = "aggregate-stats";
  private String byteSizeColumn;
  private String timeDurationColumn;
  private String totalSizeColumn;
  private String totalTimeColumn;
  private String byteSizeUnit;
  private String timeUnit;

  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder(NAME);
    builder.define("byte_size_column", TokenType.COLUMN_NAME);
    builder.define("time_duration_column", TokenType.COLUMN_NAME);
    builder.define("total_size_column", TokenType.COLUMN_NAME);
    builder.define("total_time_column", TokenType.COLUMN_NAME);
    builder.define("byte_size_unit", TokenType.TEXT, Optional.TRUE);
    builder.define("time_unit", TokenType.TEXT, Optional.TRUE);
    return builder.build();
  }

  @Override
  public void initialize(Arguments args) throws DirectiveParseException {
    byteSizeColumn = ((ColumnName) args.value("byte_size_column")).value();
    timeDurationColumn = ((ColumnName) args.value("time_duration_column")).value();
    totalSizeColumn = ((ColumnName) args.value("total_size_column")).value();
    totalTimeColumn = ((ColumnName) args.value("total_time_column")).value();

    if (args.contains("byte_size_unit")) {
      byteSizeUnit = ((Text) args.value("byte_size_unit")).value();
    } else {
      byteSizeUnit = "MB"; // Default to MB
    }

    if (args.contains("time_unit")) {
      timeUnit = ((Text) args.value("time_unit")).value();
    } else {
      timeUnit = "s"; // Default to seconds
    }
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
    // Get or initialize aggregation variables
    Object totalBytesObj = context.getTransientStore().get( "total_bytes");
    Object totalNanosObj = context.getTransientStore().get( "total_nanos");
    Object rowCountObj = context.getTransientStore().get( "row_count");

    long totalBytes = totalBytesObj != null ? (Long) totalBytesObj : 0L;
    long totalNanos = totalNanosObj != null ? (Long) totalNanosObj : 0L;
    long rowCount = rowCountObj != null ? (Long) rowCountObj : 0L;

    // Process each row
    for (Row row : rows) {
      int bsIdx = row.find(byteSizeColumn);
      int tdIdx = row.find(timeDurationColumn);

      if (bsIdx != -1) {
        Object bsObj = row.getValue(bsIdx);
        if (bsObj instanceof String) {
          ByteSize bs = new ByteSize((String) bsObj);
          totalBytes += bs.getBytes();
        }
      }

      if (tdIdx != -1) {
        Object tdObj = row.getValue(tdIdx);
        if (tdObj instanceof String) {
          TimeDuration td = new TimeDuration((String) tdObj);
          totalNanos += td.getNanos();
        }
      }

      rowCount++;
    }

    // Update the store
    context.getTransientStore().set(TransientVariableScope.LOCAL, "total_bytes", totalBytes);
    context.getTransientStore().set(TransientVariableScope.LOCAL, "total_nanos", totalNanos);
    context.getTransientStore().set(TransientVariableScope.LOCAL, "row_count", rowCount);

    // Check if this is the final batch of data
    boolean isLastBatch = context.getTransientStore().get( "phase") != null &&
        "finalize".equals(context.getTransientStore().get( "phase"));

    // If this is the last batch, create a result row
    if (isLastBatch) {
      Row result = new Row();

      // Convert bytes to the specified unit
      double totalSize;
      switch (byteSizeUnit.toUpperCase()) {
        case "B":
          totalSize = totalBytes;
          break;
        case "KB":
          totalSize = totalBytes / 1024.0;
          break;
        case "MB":
          totalSize = totalBytes / (1024.0 * 1024.0);
          break;
        case "GB":
          totalSize = totalBytes / (1024.0 * 1024.0 * 1024.0);
          break;
        case "TB":
          totalSize = totalBytes / (1024.0 * 1024.0 * 1024.0 * 1024.0);
          break;
        default:
          throw new DirectiveExecutionException(NAME, "Unsupported byte unit: " + byteSizeUnit);
      }

      // Convert nanos to the specified unit
      double totalTime;
      switch (timeUnit.toLowerCase()) {
        case "ns":
          totalTime = totalNanos;
          break;
        case "us":
          totalTime = totalNanos / 1000.0;
          break;
        case "ms":
          totalTime = totalNanos / (1000.0 * 1000.0);
          break;
        case "s":
          totalTime = totalNanos / (1000.0 * 1000.0 * 1000.0);
          break;
        case "m":
          totalTime = totalNanos / (60.0 * 1000.0 * 1000.0 * 1000.0);
          break;
        case "h":
          totalTime = totalNanos / (60.0 * 60.0 * 1000.0 * 1000.0 * 1000.0);
          break;
        default:
          throw new DirectiveExecutionException(NAME, "Unsupported time unit: " + timeUnit);
      }

      result.add(totalSizeColumn, totalSize);
      result.add(totalTimeColumn, totalTime);

      return Collections.singletonList(result);
    }

    return rows; // Return the input rows for non-final batches
  }

  @Override
  public void destroy() {
    // No-op
  }
}
