/**
 * Load and plot a large CSV file using Plotly.
 *
 * @param {string} csvUrl - URL to the CSV file
 * @param {string} xKey - Column name for x-axis (e.g., 'Time')
 * @param {string[]} yKeys - Array of column names for y-axes (e.g., ['AAPL.High', 'AAPL.Low'])
 * @param {Object} [layoutOverrides] - Optional layout configuration to override defaults
 * @param {Object} [traceOverrides] - Optional trace configuration to override generated traces
 */
async function plotLargeCsv(xKey, yKeys, layoutOverrides = {}, traceOverrides = []) {
  try {
    const csvText = document.getElementById("csv-data").textContent.trim();
    const rawData = d3.csvParse(csvText);

    if (rawData.length === 0) {
      console.error("CSV is empty.");
      return;
    }

    // Normalize headers: trim all keys in each row
    const data = rawData.map(row => {
      const cleanRow = {};
      for (const [key, value] of Object.entries(row)) {
        cleanRow[key.trim()] = value;
      }
      return cleanRow;
    });

    // Extract x-axis values
    const xValues = data.map(row => row[xKey]);

    // Prepare traces
    function mergeTracesByName(generated, overrides) {
      const map = new Map();

      // First, add all objects from A using 'name' as key
      for (const obj of generated) {
        map.set(obj.name, { ...obj });
      }

      // Then, update or add objects from B
      for (const obj of overrides) {
        if (map.has(obj.name)) {
          map.set(obj.name, {
            ...map.get(obj.name), // original
            ...obj,               // overrides
          });
        } else {
          map.set(obj.name, { ...obj });
        }
      }

      return Array.from(map.values());
    }

    const default_traces = yKeys.map((yKey, index) => ({
      type: "scatter",
      mode: "lines",
      name: yKey,
      x: xValues,
      y: data.map(row => row[yKey]),
      line: { width: 1.0 }
    }));

    const traces = mergeTracesByName(default_traces, traceOverrides);

    // Layout with optional overrides
    const layout = Object.assign({
      title: { text: "CSV Data Plot" },
      xaxis: { title: xKey, type: "auto" },
      yaxis: { title: yKeys.join(", "), autorange: true },
      showlegend: true,
      autosize: true,
      automargin: true,
      paper_bgcolor: "#F1CEBE",
      plot_bgcolor: "#F1CEBE"
    }, layoutOverrides);

    const config = { responsive: true};

    console.log("traces: " + JSON.stringify(traces));

    Plotly.newPlot("myFigure", traces, layout, config);

  } catch (err) {
    console.error("Error loading or plotting CSV:", err);
  }
}
