package io.blueskiron.plotly.spark;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public final class Plotly {

  public static Plotly instance() {
    return new Plotly();
  }

  private Output output;
  private Figure figure;
  private Page page = Page.builder().build();
  private Data data;

  public void show() {
    if (this.output == null) {
      throw new RuntimeException("output not specified");
    }
    if (this.data == null) {
      throw new RuntimeException("data not specified");
    }

    //build template engine and write html to output file
    PebbleEngine engine = new PebbleEngine.Builder().autoEscaping(false).build();
    PebbleTemplate compiledTemplate;

    if (Objects.requireNonNull(figure.type) == Figure.Type.SCATTER) {
      compiledTemplate = engine.getTemplate("templates/time_series_template.html");
    } else {
      throw new UnsupportedOperationException("This plot type is not supported yet: " + figure.type);
    }

    Writer writer = new StringWriter();
    try {
      compiledTemplate.evaluate(writer, combinedContext());
    } catch (IOException ioe) {
      Logger.error("chart rendering has failed due to: ", ioe);
    }

    output.show(writer.toString());
  }

  private Map<String, Object> combinedContext() {
    final var combined = new HashMap<String, Object>();
    if (this.page != null) {
      combined.putAll(page.getContext());
    }
    if (this.figure != null) {
      combined.putAll(figure.getContext());
    }
    if (this.data != null) {
      combined.putAll(data.getContext());
    }
    return combined;
  }
}