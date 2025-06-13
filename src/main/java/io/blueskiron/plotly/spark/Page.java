package io.blueskiron.plotly.spark;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public final class Page implements ContextAware {
  //override default background color
  @Builder.Default
  String backgroundColor = "#F1CEBE";
  //override default location of D3.js
  @Builder.Default
  String d3JsUrl = "https://d3js.org/d3.v5.min.js";
  //override default location of Plotly.js
  @Builder.Default
  String plotlyJsUrl = "https://cdn.plot.ly/plotly-3.0.1.min.js";

  public Map<String, Object> getContext() {
    return Map.of(
        "backgroundColor", this.backgroundColor,
        "d3JsUrl", this.d3JsUrl,
        "plotlyJsUrl", this.plotlyJsUrl
    );
  }
}
