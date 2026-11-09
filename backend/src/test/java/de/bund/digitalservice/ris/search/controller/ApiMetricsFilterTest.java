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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    verify(postHogClient).submitApiEvent("/v1/case-law/KORE123", "test-agent", false);
  }

  @ParameterizedTest
  @CsvSource({"true, true", "TRUE, true", "false, false", "yes, false", "'', false"})
  void marksPortalCallsByHeader(String headerValue, boolean expectedIsPortalUser) throws Exception {
    var request = request("GET", "/v1/case-law/KORE123");
    request.addHeader(ApiMetricsFilter.PORTAL_HEADER, headerValue);

    filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

    verify(postHogClient).submitApiEvent("/v1/case-law/KORE123", null, expectedIsPortalUser);
  }

  @Test
  void marksPortalCallsAsFalseOnMissingHeader() throws Exception {
    var request = request("GET", "/v1/case-law/KORE123");

    filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

    verify(postHogClient).submitApiEvent("/v1/case-law/KORE123", null, false);
  }

  @ParameterizedTest
  @CsvSource({"OPTIONS, /v1/case-law/KORE123", "GET,/actuator/health", "GET, /robots.txt"})
  void skipsUntrackedCalls(String method, String path) throws Exception {
    var request = request(method, path);

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

    verify(postHogClient).submitApiEvent("/v1/feedback", null, false);
  }

  private static MockHttpServletRequest request(String method, String path) {
    var request = new MockHttpServletRequest(method, path);
    // DispatcherServlet is mapped to "/", so the servlet path is the full path within the app
    request.setServletPath(path);
    return request;
  }
}
