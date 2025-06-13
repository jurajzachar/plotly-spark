package io.blueskiron.plotly.spark;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static java.nio.charset.StandardCharsets.UTF_8;

@Builder
@Data
@Accessors(fluent = true, chain = true)
public final class Output {
  File pathToOutputFolder;
  @Builder.Default
  String outputFilename = "plotly-spark.html";
  boolean renderInBrowser;

  private void copyClasspathResourceToFile(String resourcePath, File destinationFile) throws IOException {
    try (InputStream in = Output.class.getClassLoader().getResourceAsStream(resourcePath)) {
      if (in == null) {
        throw new FileNotFoundException("Resource not found: " + resourcePath);
      }

      // Ensure parent directories exist
      destinationFile.getParentFile().mkdirs();

      // Copy resource to destination file
      Files.copy(in, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
  }

  private File createFileStructure() {
    // Check directory existence and type
    if (!pathToOutputFolder.exists() || !pathToOutputFolder.isDirectory()) {
      throw new RuntimeException(String.format("'%s' output directory does not exist", pathToOutputFolder));
    }

    // Check directory writability
    if (!pathToOutputFolder.canWrite()) {
      throw new RuntimeException(String.format("'%s' output directory is not writable", pathToOutputFolder));
    }

    //ensure outputFilename ends with .html
    if (!outputFilename.endsWith(".html")) {
      outputFilename += ".html";
    }
    // Construct target file path
    File targetFile = new File(pathToOutputFolder, outputFilename);
    try {
      targetFile.createNewFile();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    //construct JS folder and copy script
    File js = new File(targetFile.getParentFile(), "js");

    //copy JS script
    try {
      copyClasspathResourceToFile("templates/load_plotly.js", new File(js, "load_plotly.js"));
    } catch (IOException ioe) {
      throw new IllegalStateException(ioe);
    }

    // If the file already exists, check if it's writable
    if (!targetFile.exists()) {
      throw new RuntimeException(String.format("'%s target file cannot be retrieved'", targetFile.getAbsolutePath()));
    }

    return targetFile;
  }

  void show(String html) {
    //check file can be created
    final var outFile = createFileStructure();
    try (Writer writer = new OutputStreamWriter(new FileOutputStream(outFile), UTF_8)) {
      writer.write(html);

      if (renderInBrowser) {
        DefaultBrowser.browse(outFile);
      }
    } catch (IOException ioe) {
      //rethrow
      throw new RuntimeException("failed to render Plotly chart, reason: " + ioe.getMessage());
    }
  }
}
