package de.bund.digitalservice.ris.search.config.ratelimiting;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.config.ApiConfig;
import de.bund.digitalservice.ris.search.config.ContainersIntegrationBase;
import de.bund.digitalservice.ris.search.controller.api.FeedbackController.FeedbackRequest;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

/**
 * Verifies the rate limit filter registrations in {@code WebMvcConfig} against the running server.
 * Uses a real HTTP client, so that the embedded server also performs async re-dispatches of
 * streaming responses.
 *
 * <p>All requests originate from the same IP address, so the request counters of all rate limit
 * filters are reset before each test.
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "rate-limit.default.requests=" + RateLimitIntegrationTest.DEFAULT_LIMIT,
      "rate-limit.default.seconds=60",
      "rate-limit.feedback.requests=" + RateLimitIntegrationTest.FEEDBACK_LIMIT,
      "rate-limit.feedback.seconds=60"
    })
class RateLimitIntegrationTest extends ContainersIntegrationBase {

  static final int DEFAULT_LIMIT = 5;
  static final int FEEDBACK_LIMIT = 2;

  private static final String DOCUMENT_NUMBER = "BFRE000107055";

  @LocalServerPort private int port;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private List<FilterRegistrationBean<?>> filterRegistrations;

  @BeforeEach
  void resetRateLimits() {
    filterRegistrations.stream()
        .map(FilterRegistrationBean::getFilter)
        .filter(RateLimitFilter.class::isInstance)
        .map(RateLimitFilter.class::cast)
        .forEach(RateLimitFilter::reset);
  }

  private RequestSpecification request() {
    return given().port(port);
  }

  private int getStatus(String path) {
    return request().get(path).statusCode();
  }

  private int postFeedbackStatus() throws Exception {
    var body = new FeedbackRequest("test feedback", "http://example.com", "test-distinct-id", null);
    return request()
        .contentType(ContentType.JSON)
        .body(objectMapper.writeValueAsString(body))
        .post(ApiConfig.Paths.FEEDBACK)
        .statusCode();
  }

  private void exhaustDefaultLimit() {
    for (int i = 0; i < DEFAULT_LIMIT; i++) {
      assertThat(getStatus(ApiConfig.Paths.LEGISLATION)).isEqualTo(200);
    }
  }

  @Test
  void defaultLimitAppliesToAllEndpoints() {
    exhaustDefaultLimit();

    assertThat(getStatus(ApiConfig.Paths.LEGISLATION)).isEqualTo(429);
    assertThat(getStatus(ApiConfig.Paths.RECHTSPRECHUNG)).isEqualTo(429);
  }

  @Test
  void feedbackLimitAppliesOnlyToFeedbackEndpoint() throws Exception {
    for (int i = 0; i < FEEDBACK_LIMIT; i++) {
      assertThat(postFeedbackStatus()).isEqualTo(200);
    }
    assertThat(postFeedbackStatus()).isEqualTo(429);

    // other endpoints are only subject to the higher default limit
    assertThat(getStatus(ApiConfig.Paths.LEGISLATION)).isEqualTo(200);
  }

  @Test
  void streamingResponseCountsAsSingleRequest() throws Exception {
    caseLawBucket.save(DOCUMENT_NUMBER + "/" + DOCUMENT_NUMBER + ".xml", "<xml/>");

    // streamed via StreamingResponseBody, which triggers an async re-dispatch
    byte[] zip =
        request()
            .get(ApiConfig.Paths.RECHTSPRECHUNG + "/" + DOCUMENT_NUMBER + ".zip")
            .then()
            .statusCode(200)
            .extract()
            .asByteArray();
    try (var zipStream = new ZipInputStream(new ByteArrayInputStream(zip))) {
      assertThat(zipStream.getNextEntry()).isNotNull();
    }

    // the async re-dispatch was not counted, so the remaining requests still go through
    for (int i = 1; i < DEFAULT_LIMIT; i++) {
      assertThat(getStatus(ApiConfig.Paths.RECHTSPRECHUNG)).isEqualTo(200);
    }
    assertThat(getStatus(ApiConfig.Paths.RECHTSPRECHUNG)).isEqualTo(429);
  }

  @Test
  void actuatorEndpointsAreNotRateLimited() {
    exhaustDefaultLimit();
    assertThat(getStatus(ApiConfig.Paths.LEGISLATION)).isEqualTo(429);

    assertThat(getStatus("/actuator/health")).isEqualTo(200);
  }
}
