package io.blueskiron.plotly.spark;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultBrowser {

  public static void browse(File file) throws IOException {
    String os = System.getProperty("os.name").toLowerCase();
    String absolutePath = file.getAbsolutePath();

    if (os.contains("win")) {
      Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start", "\"\"", absolutePath });
    } else if (os.contains("mac")) {
      Runtime.getRuntime().exec(new String[] { "open", absolutePath });
    } else if (os.contains("nux") || os.contains("nix")) {
      Runtime.getRuntime().exec(new String[] { "xdg-open", absolutePath });
    } else {
      throw new UnsupportedOperationException("Cannot open file on this OS: " + os);
    }
  }
}
