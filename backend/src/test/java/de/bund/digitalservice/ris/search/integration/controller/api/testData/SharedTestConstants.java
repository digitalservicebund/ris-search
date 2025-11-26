package de.bund.digitalservice.ris.search.integration.controller.api.testData;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/** Shared test constants and utility methods for test data generation */
public class SharedTestConstants {
  public static final LocalDate DATE_1_1 = LocalDate.of(2023, 1, 2);
  public static final LocalDate DATE_2_1 = LocalDate.of(2024, 1, 1);
  public static final LocalDate DATE_2_2 = LocalDate.of(2024, 1, 2);
  public static final LocalDate DATE_2_3 = LocalDate.of(2024, 1, 3);

  /**
   * Generates XML from a Pebble template with the given context.
   *
   * @param context
   * @param template
   * @return XML string
   * @throws IOException
   */
  public static String getXmlFromTemplate(Map<String, Object> context, String template)
      throws IOException {
    PebbleEngine engine = new PebbleEngine.Builder().build();
    PebbleTemplate compiledTemplate = engine.getTemplate(template);
    if (context == null) {
      context = new HashMap<>();
    }
    Writer writer = new StringWriter();
    compiledTemplate.evaluate(writer, context);
    return writer.toString();
  }
}
