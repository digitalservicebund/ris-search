package de.bund.digitalservice.ris.search.utils;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class CaseLawLdmlTemplateUtils {
  public static final String CASELAW_LDML_TEMPLATE = "templates/case-law/ldml-base.xml";

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
