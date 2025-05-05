package de.bund.digitalservice.ris.search.unit.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.bund.digitalservice.ris.search.utils.HttpLogSanitizer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

class HttpLogSanitizerTest {

  @Test
  void testSanitizeLogJson_removesSensitiveFieldsAndRedactsEmail() {
    JSONObject input = new JSONObject();
    input.put("headers", new JSONObject().put("Cookie", "secret_token=abc123"));
    input.put("remote", "127.0.0.1");
    input.put("origin", "remote");
    input.put("host", "localhost");
    input.put("type", "request");

    JSONObject sanitized = HttpLogSanitizer.sanitizeLogJson(input);

    assertFalse(sanitized.has("headers"));
    assertFalse(sanitized.has("remote"));
    assertFalse(sanitized.has("origin"));
    assertFalse(sanitized.has("host"));
    assertFalse(sanitized.has("type"));
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
