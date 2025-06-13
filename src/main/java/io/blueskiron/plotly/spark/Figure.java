package io.blueskiron.plotly.spark;

import com.google.gson.*;
import lombok.Builder;
import lombok.Singular;
import lombok.experimental.Accessors;
import org.tinylog.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Builder
@Accessors(fluent = true, chain = true)
public class Figure implements ContextAware {

  enum Type {
    SCATTER, HISTOGRAM
  }

  @Builder.Default
  Type type = Type.SCATTER;
  String xAxisLabel;
  @Singular
  List<String> yAxisLabels;
  @Builder.Default
  String layoutOverrides = "{}";
  @Builder.Default
  String traceOverrides = "[]";

  @Override
  public Map<String, Object> getContext() {
    var layoutOverridesAsJavascript = new JsonObject();
    try {
      layoutOverridesAsJavascript = JsonParser.parseString(layoutOverrides).getAsJsonObject();
    } catch (JsonSyntaxException e) {
      Logger.error("invalid layout overrides detected: ", e);
      //suppress
    }
    var traceOverridesAsJavaScript = new JsonArray();
    try {
      traceOverridesAsJavaScript = JsonParser.parseString(traceOverrides).getAsJsonArray();
    } catch (JsonSyntaxException e) {
      Logger.error("invalid trace overrides detected: ", e);
      //suppress
    }
    return Map.of(
        "xAxisLabel", new JsonPrimitive(this.xAxisLabel).toString(),
        "yAxisLabels", asJsonArray(this.yAxisLabels),
        "layoutOverrides", layoutOverridesAsJavascript.toString(),
        "traceOverrides", traceOverridesAsJavaScript.toString()
    );
  }

  private String asJsonArray(List<String> yAxisLabels) {
    final var array = new JsonArray();
    for (String elem : yAxisLabels) {
      array.add(elem);
    }
    return array.toString();
  }

}
