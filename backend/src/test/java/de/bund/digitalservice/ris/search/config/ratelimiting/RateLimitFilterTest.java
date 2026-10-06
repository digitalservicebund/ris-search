package de.bund.digitalservice.ris.search.config.ratelimiting;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.WebUtils;

class RateLimitFilterTest {

  private final RateLimitFilter filter = new DefaultRateLimitFilter(1, 60);

  private static MockHttpServletRequest request(DispatcherType dispatcherType, String uri) {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
    request.setDispatcherType(dispatcherType);
    if (dispatcherType == DispatcherType.ERROR) {
      // set by the servlet container on error dispatches, used by OncePerRequestFilter to skip them
      request.setAttribute(WebUtils.ERROR_REQUEST_URI_ATTRIBUTE, uri);
    }
    return request;
  }

  private static MockHttpServletRequest request(DispatcherType dispatcherType) {
    return request(dispatcherType, "/v1/legislation");
  }

  /** Runs the filter and returns whether the request was passed on down the chain. */
  private boolean passesFilter(MockHttpServletRequest request, MockHttpServletResponse response)
      throws ServletException, IOException {
    MockFilterChain chain = new MockFilterChain();
    filter.doFilter(request, response, chain);
    return chain.getRequest() != null;
  }

  @Test
  void itRejectsRequestsExceedingTheLimit() throws ServletException, IOException {
    Assertions.assertTrue(
        passesFilter(request(DispatcherType.REQUEST), new MockHttpServletResponse()));

    MockHttpServletResponse response = new MockHttpServletResponse();
    Assertions.assertFalse(passesFilter(request(DispatcherType.REQUEST), response));
    Assertions.assertEquals(429, response.getStatus());
  }

  @Test
  void itDoesNotSendErrorOnAsyncDispatchWhenResponseIsCommitted()
      throws ServletException, IOException {
    passesFilter(request(DispatcherType.REQUEST), new MockHttpServletResponse());

    MockHttpServletResponse committedResponse = new MockHttpServletResponse();
    committedResponse.setCommitted(true);

    Assertions.assertTrue(passesFilter(request(DispatcherType.ASYNC), committedResponse));
    Assertions.assertEquals(200, committedResponse.getStatus());
  }

  @Test
  void itDoesNotCountNonRequestDispatches() throws ServletException, IOException {
    passesFilter(request(DispatcherType.ASYNC), new MockHttpServletResponse());
    passesFilter(request(DispatcherType.ERROR), new MockHttpServletResponse());

    Assertions.assertTrue(
        passesFilter(request(DispatcherType.REQUEST), new MockHttpServletResponse()));
  }

  @Test
  void itDoesNotLimitActuatorEndpoints() throws ServletException, IOException {
    passesFilter(request(DispatcherType.REQUEST), new MockHttpServletResponse());

    MockHttpServletResponse response = new MockHttpServletResponse();
    Assertions.assertTrue(
        passesFilter(request(DispatcherType.REQUEST, "/actuator/health"), response));
    Assertions.assertEquals(200, response.getStatus());
  }
}
