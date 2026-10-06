package de.bund.digitalservice.ris.search.miscellaneous;

import static org.assertj.core.api.Assertions.assertThat;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.config.ratelimiting.DefaultRateLimitFilter;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.convention.TestBean;

/**
 * Uses a real HTTP client instead of MockMvc, so that the embedded server performs the async
 * re-dispatch of streaming responses, which also passes through the rate limit filter.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StreamingRateLimitIntegrationTest extends ContainersIntegrationBase {

  private static final String DOCUMENT_NUMBER = "BFRE000107055";

  @LocalServerPort private int port;

  @TestBean private DefaultRateLimitFilter testFilter;

  static DefaultRateLimitFilter testFilter() {
    return new DefaultRateLimitFilter(2, 10);
  }

  @BeforeEach
  void setUp() {
    caseLawBucket.save(DOCUMENT_NUMBER + "/" + DOCUMENT_NUMBER + ".xml", "<xml/>");
  }

  private HttpResponse<byte[]> get(HttpClient client, String path) throws Exception {
    HttpRequest request =
        HttpRequest.newBuilder().uri(URI.create("http://localhost:" + port + path)).GET().build();
    return client.send(request, HttpResponse.BodyHandlers.ofByteArray());
  }

  @Test
  void streamingResponseCountsAsSingleRequest() throws Exception {
    try (HttpClient client = HttpClient.newHttpClient()) {
      // first call is streamed via StreamingResponseBody, which triggers an async re-dispatch
      HttpResponse<byte[]> zipResponse =
          get(client, ApiConfig.Paths.CASELAW + "/" + DOCUMENT_NUMBER + ".zip");
      assertThat(zipResponse.statusCode()).isEqualTo(200);
      try (var zip = new ZipInputStream(new ByteArrayInputStream(zipResponse.body()))) {
        assertThat(zip.getNextEntry()).isNotNull();
      }

      // second call still goes through, as the async re-dispatch was not counted
      assertThat(get(client, ApiConfig.Paths.CASELAW).statusCode()).isEqualTo(200);

      // third one is being rate limited
      assertThat(get(client, ApiConfig.Paths.CASELAW).statusCode()).isEqualTo(429);
    }
  }
}
