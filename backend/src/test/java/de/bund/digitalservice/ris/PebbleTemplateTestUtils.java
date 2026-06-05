package de.bund.digitalservice.ris;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/** Utility methods for rendering Pebble XML templates in tests. */
public final class PebbleTemplateTestUtils {

  private PebbleTemplateTestUtils() {}

  /**
   * Renders a Pebble template from test resources with the provided context values.
   *
   * @param context template context variables, may be null.
   * @param template template path relative to test resources.
   * @return rendered template content as a string.
   * @throws IOException if the template cannot be loaded or rendered.
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
