package de.bund.digitalservice.ris.utils;

import static com.apicatalog.jsonld.JsonLdOptions.ProcessingPolicy.Fail;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.loader.DocumentLoader;
import jakarta.json.JsonArray;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import org.springframework.test.web.servlet.ResultMatcher;

public class JsonldResultMatchers {

  private static final Document CACHED_CONTEXT_DOC;

  static {
    try (InputStream is = JsonldResultMatchers.class.getResourceAsStream("/jsonld/1_1/v1.jsonld")) {
      if (is == null) {
        throw new IllegalStateException("context.jsonld resource not found");
      }
      CACHED_CONTEXT_DOC = JsonDocument.of(new InputStreamReader(is, StandardCharsets.UTF_8));
    } catch (Exception e) {
      throw new ExceptionInInitializerError("Failed to load JSON-LD context: " + e.getMessage());
    }
  }

  private static final DocumentLoader CACHED_LOADER = (_, _) -> CACHED_CONTEXT_DOC;

  public static ResultMatcher isJsonLdCompliant() {
    return result -> {
      String jsonResponse = result.getResponse().getContentAsString();
      JsonDocument document = JsonDocument.of(new StringReader(jsonResponse));

      JsonArray expanded =
          JsonLd.expand(document).loader(CACHED_LOADER).undefinedTermsPolicy(Fail).get();

      if (expanded.isEmpty()) {
        throw new AssertionError(
            "Expected JSON-LD response to expand to a non-empty array, but was empty.");
      }
    };
  }
}
