package de.bund.digitalservice.ris.search.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.zalando.logbook.HttpRequest;

/**
 * A utility class for handling HTTP-related logging processes. This class provides methods to
 * sanitize sensitive information from JSON objects and to extract query parameters from HTTP
 * requests as structured key-value pairs.
 *
 * <p>This class is not instantiable.
 */
public class HttpLog {

  private HttpLog() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Sanitizes the given JSON object by removing specific sensitive keys such as "headers",
   * "remote", "origin", "host", and "type".
   *
   * @param json the JSON object to sanitize, from which sensitive keys will be removed
   * @return the sanitized JSON object with the specified keys removed
   */
  public static JSONObject sanitizeLogJson(JSONObject json) {

    json.remove("headers");
    json.remove("remote");
    json.remove("origin");
    json.remove("host");
    json.remove("type");

    return json;
  }

  /**
   * Extracts query parameters from the given HTTP request as a map of key-value pairs. Keys and
   * values are URL-decoded. If the request is null, has a null URI, or the URI contains no query
   * parameters, an empty map is returned. Invalid URIs are ignored.
   *
   * @param request the HTTP request containing the URI with potential query parameters
   * @return a map containing the query parameters as key-value pairs, or an empty map if none exist
   */
  public static Map<String, String> getQueryParamsAsMap(HttpRequest request) {
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
