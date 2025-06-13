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
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class SimpleScatterExample {

  public static void main(String[] args) {
    //initialize spark and data
    SparkSession spark = SparkSession.builder()
        .appName("SimpleScatterExample")
        .master("local[*]")
        .getOrCreate();

    // Define schema: timestamp as Long, price as Double
    StructType schema = new StructType(new StructField[]{
        new StructField("timestamp", DataTypes.TimestampType, false, Metadata.empty()),
        new StructField("volume", DataTypes.DoubleType, false, Metadata.empty()),
    });

    List<Row> data = List.of(
        RowFactory.create(Timestamp.from(Instant.now()), 0.1D),
        RowFactory.create(Timestamp.from(Instant.now().plusSeconds(2)), 0.3D),
        RowFactory.create(Timestamp.from(Instant.now().plusSeconds(4)), 0.1D),
        RowFactory.create(Timestamp.from(Instant.now().plusSeconds(16)), 0.4D),
        RowFactory.create(Timestamp.from(Instant.now().plusSeconds(32)), 0.2D),
        RowFactory.create(Timestamp.from(Instant.now().plusSeconds(64)), 0.25D),
        RowFactory.create(Timestamp.from(Instant.now().plusSeconds(128)), 0.15D),
        RowFactory.create(Timestamp.from(Instant.now().plusSeconds(256)), 0.6D)
    );

    Dataset<Row> df = spark.createDataFrame(data, schema);

    Plotly.instance()
        //plug in Dataframe
        .data(
            Data.builder()
                .session(spark)
                .df(df)
                .build()
        )
        // configure X and Y axis
        .figure(Figure.builder()
            .xAxisLabel("timestamp")
            .yAxisLabel("volume")
            .build()
        )
        // specify where to save rendered HTMLa
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
