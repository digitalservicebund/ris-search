package de.bund.digitalservice.ris.search.utils;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class that provides methods for processing and generating XML content from templates
 * specifically related to case law.
 */
public class CaseLawLdmlTemplateUtils {
  public static final String CASELAW_LDML_TEMPLATE = "templates/case-law/case-law-template.xml";

  /**
   * Generates an XML string based on a predefined template and a given context. The method uses a
   * template rendering engine to populate the template with values provided in the context map.
   *
   * @param context a map containing key-value pairs representing the context variables to be
   *     injected into the template. If the context is null, an empty map will be used.
   * @return a string containing the generated XML after populating the template with the provided
   *     context values.
   * @throws IOException if an error occurs during template processing.
   */
  public String getXmlFromTemplate(Map<String, Object> context) throws IOException {
    PebbleEngine engine = new PebbleEngine.Builder().build();
    PebbleTemplate compiledTemplate = engine.getTemplate(CASELAW_LDML_TEMPLATE);
    if (context == null) {
      context = new HashMap<>();
    }
    Writer writer = new StringWriter();
    compiledTemplate.evaluate(writer, context);
    return writer.toString();
  }
}
