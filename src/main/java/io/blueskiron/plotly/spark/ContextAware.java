package io.blueskiron.plotly.spark;

import java.util.Map;

interface ContextAware {
  Map<String, Object> getContext();
}
