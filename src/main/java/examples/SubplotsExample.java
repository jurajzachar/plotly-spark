package examples;

import io.blueskiron.plotly.spark.Data;
import io.blueskiron.plotly.spark.Figure;
import io.blueskiron.plotly.spark.Output;
import io.blueskiron.plotly.spark.Plotly;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.File;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

public class SubplotsExample {
  public static void main(String[] args) {
    SparkSession spark = SparkSession.builder()
        .appName("SubplotsExample")
        .master("local[*]")
        .getOrCreate();

    StructType schema = new StructType(new StructField[]{
        new StructField("timestamp", DataTypes.TimestampType, false, Metadata.empty()),
        new StructField("low_price", DataTypes.DoubleType, false, Metadata.empty()),
        new StructField("high_price", DataTypes.DoubleType, false, Metadata.empty()),
        new StructField("spread", DataTypes.DoubleType, false, Metadata.empty()),
    });

    // Create some random in-memory data (Sql timestamp, low_freq, high_freq, mid_freq)
    final SecureRandom rand = new SecureRandom();
    List<Row> data = IntStream.rangeClosed(1, 100).boxed()
        .map(i -> {
          final var lowPrice = rand.nextDouble() + 100;
          final var highPrice = rand.nextDouble() + 101;
          final var spread = highPrice - lowPrice;
          return RowFactory.create(Timestamp.from(Instant.now().plusSeconds(i)), lowPrice, highPrice, spread);
        })
        .toList();

    Dataset<Row> df = spark.createDataFrame(data, schema);

    Plotly.instance()
        .data(
            Data.builder()
                .session(spark)
                .df(df)
                .build()
        )
        .figure(Figure.builder()
            .xAxisLabel("timestamp")
            .yAxisLabel("spread")
            .yAxisLabel("low_price")
            .yAxisLabel("high_price")
            // create two coupled plots
            .layoutOverrides(
                """
                      { 
                        "title": { "text": "My Plotly Chart"},
                        "xaxis": { "title": { "text": "time" }},
                        "yaxis": { 
                          "title": { 
                            "text": "price" 
                          },
                          "domain": [0.00, 0.70]
                        },
                         "yaxis2": { 
                          "title": { 
                            "text": "spread" 
                          },
                          "domain": [0.75, 1.00]
                        },
                        "grid": {
                          "rows": 2,
                          "columns": 1,
                          "roworder": "bottom to top",
                          "pattern": "coupled"
                        }
                      }
                    """
            )
            .traceOverrides(
                """
                      [
                       {
                         "name": "spread",
                         "mode": "lines+markers",
                         "marker": {
                           "symbol": "cross-open",
                           "size" : 5
                         },
                         "xaxis": "x",
                         "yaxis": "y2"
                       },
                       {
                         "name": "low_price",
                         "line": {
                           "color": "red",
                           "width": 1.0
                         }
                       },
                       {
                         "name": "high_price",
                         "line": {
                           "color": "green",
                           "width": 1.0
                         }
                       }
                      ]
                    """
            )
            .build()
        )
        .output(Output.builder()
            .renderInBrowser(true)
            .pathToOutputFolder(new File("./target/testoutput"))
            .outputFilename("my_plot")
            .build()
        )
        .show();

    spark.stop();
  }
}
