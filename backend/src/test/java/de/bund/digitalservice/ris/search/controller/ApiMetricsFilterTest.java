package de.bund.digitalservice.ris.search.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import de.bund.digitalservice.ris.search.client.posthog.PostHogClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class ApiMetricsFilterTest {

  @Mock private PostHogClient postHogClient;

  private ApiMetricsFilter filter;

  @BeforeEach
  void setUp() {
    filter = new ApiMetricsFilter(postHogClient);
  }

  @Test
  void capturesApiCall() throws Exception {
    var request = request("GET", "/v1/case-law/KORE123");
    request.addHeader("User-Agent", "test-agent");

    filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

    verify(postHogClient).submitApiEvent("/v1/case-law/KORE123", "test-agent");
  }

  @Test
  void skipsActuatorCalls() throws Exception {
    var request = request("GET", "/actuator/health");

    filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

    verifyNoInteractions(postHogClient);
  }

  @Test
  void skipsNonApiCalls() throws Exception {
    var request = request("GET", "/robots.txt");

    filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

    verifyNoInteractions(postHogClient);
  }

  @Test
  void capturesApiCallWhenFilterChainThrows() throws Exception {
    var request = request("POST", "/v1/feedback");
    var response = new MockHttpServletResponse();
    var filterChain = mock(FilterChain.class);
    doThrow(new ServletException("boom")).when(filterChain).doFilter(any(), any());

    assertThrows(ServletException.class, () -> filter.doFilter(request, response, filterChain));

    verify(postHogClient).submitApiEvent("/v1/feedback", null);
  }

  private static MockHttpServletRequest request(String method, String path) {
    var request = new MockHttpServletRequest(method, path);
    // DispatcherServlet is mapped to "/", so the servlet path is the full path within the app
    request.setServletPath(path);
    return request;
  }
}
