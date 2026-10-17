package de.bund.digitalservice.ris.search.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class ApiMetricsFilterTest {

  @Mock private PostHogClient postHogClient;
  @Mock private FilterChain filterChain;

  private ApiMetricsFilter filter;
  private final MockHttpServletResponse response = new MockHttpServletResponse();

  @BeforeEach
  void setUp() {
    filter = new ApiMetricsFilter(postHogClient, "/actuator");
  }

  @Test
  void submitsApiEventAfterRequest() throws ServletException, IOException {
    var request = new MockHttpServletRequest("GET", "/v1/legislation");
    request.addHeader("User-Agent", "test-agent");

    filter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(postHogClient).submitApiEvent("/v1/legislation", "GET", "test-agent");
  }

  @Test
  void submitsApiEventWithoutUserAgent() throws ServletException, IOException {
    var request = new MockHttpServletRequest("POST", "/v1/feedback");

    filter.doFilter(request, response, filterChain);

    verify(postHogClient).submitApiEvent("/v1/feedback", "POST", null);
  }

  @Test
  void submitsApiEventWhenRequestFails() throws ServletException, IOException {
    var request = new MockHttpServletRequest("GET", "/v1/legislation");
    doThrow(new ServletException("failed")).when(filterChain).doFilter(any(), any());

    assertThatThrownBy(() -> filter.doFilter(request, response, filterChain))
        .isInstanceOf(ServletException.class);

    verify(postHogClient).submitApiEvent("/v1/legislation", "GET", null);
  }

  @Test
  void skipsActuatorRequests() throws ServletException, IOException {
    var request = new MockHttpServletRequest("GET", "/actuator/health");

    filter.doFilter(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(postHogClient);
  }
}
