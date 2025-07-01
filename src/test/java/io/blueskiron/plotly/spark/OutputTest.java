package io.blueskiron.plotly.spark;
import io.blueskiron.plotly.spark.Output;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class OutputTest {

  @TempDir
  File tempDir;

  @Test
  void testCreateFileStructure_createsHtmlAndJsFiles() {
    Output output = Output.builder()
        .pathToOutputFolder(tempDir)
        .outputFilename("test.html")
        .renderInBrowser(false)
        .build();

    File htmlFile = output.createFileStructure();

    assertTrue(htmlFile.exists());
    assertTrue(htmlFile.getName().endsWith(".html"));
    File jsFile = new File(tempDir, "js/load_plotly.js");
    assertTrue(jsFile.exists());
  }

  @Test
  void testCreateFileStructure_invalidDirectory_throws() {
    File fileInsteadOfDir = new File(tempDir, "notADir.txt");
    try {
      assertTrue(fileInsteadOfDir.createNewFile());
      Output output = Output.builder()
          .pathToOutputFolder(fileInsteadOfDir)
          .outputFilename("test.html")
          .renderInBrowser(false)
          .build();
      RuntimeException ex = assertThrows(RuntimeException.class, output::createFileStructure);
      assertTrue(ex.getMessage().contains("invalid output directory"));
    } catch (IOException e) {
      fail(e);
    }
  }

  @Test
  void testShow_writesHtmlContent() throws IOException {
    Output output = Output.builder()
        .pathToOutputFolder(tempDir)
        .outputFilename("test.html")
        .renderInBrowser(false)
        .build();

    String html = "<html><body>Test</body></html>";
    output.show(html);

    File htmlFile = new File(tempDir, "test.html");
    assertTrue(htmlFile.exists());
    String content = Files.readString(htmlFile.toPath());
    assertEquals(html, content);
  }

  @Test
  void testShow_withBrowser() {
    Output output = Output.builder()
        .pathToOutputFolder(tempDir)
        .outputFilename("test.html")
        .renderInBrowser(true)
        .build();

    // This will attempt to open the browser, which may not be desirable in tests.
    // You can mock DefaultBrowser.browse if needed.
    assertDoesNotThrow(() -> output.show("<html></html>"));
  }
}