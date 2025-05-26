package de.bund.digitalservice.ris.search.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.zalando.logbook.HttpRequest;

public class HttpLogSanitizer {

  private HttpLogSanitizer() {
    throw new IllegalStateException("Utility class");
  }

  public static JSONObject sanitizeLogJson(JSONObject json, HttpRequest request) {

    json.remove("headers");
    json.remove("remote");
    json.remove("origin");
    json.remove("host");
    json.remove("type");

    json.put("queryParams", getQueryParamsAsText(request));

    return json;
  }

  private static Map<String, String> getQueryParamsAsText(HttpRequest request) {
    if (request == null || request.getRequestUri() == null) {
      return new HashMap<>();
    }
    Map<String, String> queryParams = new HashMap<>();

    try {
      URI uri = new URI(request.getRequestUri());
      String query = uri.getQuery();

      if (query != null) {
        for (String pair : query.split("&")) {
          String[] parts = pair.split("=", 2);
          String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
          String value =
              parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
          queryParams.put(key, value);
        }
      }
    } catch (URISyntaxException e) {
      // Intentionally left empty: best-effort sanitization, invalid URIs are ignored
      // to avoid polluting logs or throwing during log processing (non-critical path) // NOSONAR
    }

    return queryParams;
  }
}
