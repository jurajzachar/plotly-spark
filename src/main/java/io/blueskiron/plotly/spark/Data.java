package io.blueskiron.plotly.spark;

import lombok.Builder;
import lombok.experimental.Accessors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Accessors(fluent = true, chain = true)
public class Data implements ContextAware {
  @Builder.Default
  SparkSession session = SparkSession.builder()
      .appName("plotly-spark")
      .getOrCreate();
  Dataset<Row> df;

  @Override
  public Map<String, Object> getContext() {
    if (df == null) {
      return Map.of("data", "");
    }
    List<String> headers = java.util.Arrays.asList(df.columns());

    // Fetch all rows into memory
    List<Row> rows = df.collectAsList();

    // Create CSV rows as strings
    List<String> csvLines = rows.stream().map(row -> headers.stream()
        .map(col -> {
          Object value = row.getAs(col);
          return value != null ? value.toString() : "";
        })
        .collect(Collectors.joining(","))).collect(Collectors.toList());

    // Add header
    csvLines.addFirst(String.join(",", headers));

    // Combine lines
    return Map.of("data", String.join("\n", csvLines));
  }
}
