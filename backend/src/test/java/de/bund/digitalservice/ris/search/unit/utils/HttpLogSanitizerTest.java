package de.bund.digitalservice.ris.search.unit.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.bund.digitalservice.ris.search.utils.HttpLogSanitizer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.zalando.logbook.HttpRequest;

class HttpLogSanitizerTest {

  @Test
  void testSanitizeLogJson_removesSensitiveFieldsAndRedactsEmail() {
    JSONObject input = new JSONObject();
    input.put("headers", new JSONObject().put("Cookie", "secret_token=abc123"));
    input.put("remote", "127.0.0.1");
    input.put("origin", "remote");
    input.put("host", "localhost");
    input.put("type", "request");

    HttpRequest request = mock(HttpRequest.class);
    when(request.getRequestUri())
        .thenReturn("http://localhost:8090/v1/legislation?searchTerm=urlaub&size=100&pageIndex=0");

    JSONObject sanitized = HttpLogSanitizer.sanitizeLogJson(input, request);

    assertFalse(sanitized.has("headers"));
    assertFalse(sanitized.has("remote"));
    assertFalse(sanitized.has("origin"));
    assertFalse(sanitized.has("host"));
    assertFalse(sanitized.has("type"));
    assertTrue(sanitized.has("queryParams"));

    assertEquals(
        "{\"size\":\"100\",\"pageIndex\":\"0\",\"searchTerm\":\"urlaub\"}",
        sanitized.get("queryParams").toString());
  }

  @Test
  void testGetQueryParamsAsTextHandlesNullRequest() {
    JSONObject input = new JSONObject();
    JSONObject sanitized = HttpLogSanitizer.sanitizeLogJson(input, null);

    assertEquals("{}", sanitized.get("queryParams").toString());
  }

  @Test
  void testPrivateConstructorThrowsException() throws Exception {
    Constructor<HttpLogSanitizer> constructor = HttpLogSanitizer.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    InvocationTargetException thrown =
        assertThrows(InvocationTargetException.class, constructor::newInstance);

    Throwable cause = thrown.getCause();
    assertNotNull(cause);
    assertInstanceOf(IllegalStateException.class, cause);
    assertEquals("Utility class", cause.getMessage());
  }
}
