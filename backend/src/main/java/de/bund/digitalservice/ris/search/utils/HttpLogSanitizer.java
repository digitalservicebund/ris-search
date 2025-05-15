package de.bund.digitalservice.ris.search.utils;

import org.json.JSONObject;

public class HttpLogSanitizer {

  private HttpLogSanitizer() {
    throw new IllegalStateException("Utility class");
  }

  public static JSONObject sanitizeLogJson(JSONObject json) {

    json.remove("headers");
    json.remove("remote");
    json.remove("origin");
    json.remove("host");
    json.remove("type");

    return json;
  }
}
