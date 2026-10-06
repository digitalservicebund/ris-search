package de.bund.digitalservice.ris.search.config.ratelimiting;

import jakarta.servlet.DispatcherType;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RateLimitInterceptorTest {

  private final RateLimitInterceptor interceptor = new DefaultRateLimitInterceptor(1, 60);

  private static MockHttpServletRequest request(DispatcherType dispatcherType) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setDispatcherType(dispatcherType);
    return request;
  }

  @Test
  void itRejectsRequestsExceedingTheLimit() throws IOException {
    Assertions.assertTrue(
        interceptor.preHandle(
            request(DispatcherType.REQUEST), new MockHttpServletResponse(), new Object()));

    MockHttpServletResponse response = new MockHttpServletResponse();
    Assertions.assertFalse(
        interceptor.preHandle(request(DispatcherType.REQUEST), response, new Object()));
    Assertions.assertEquals(429, response.getStatus());
  }

  @Test
  void itDoesNotSendErrorOnAsyncDispatchWhenResponseIsCommitted() throws IOException {
    interceptor.preHandle(
        request(DispatcherType.REQUEST), new MockHttpServletResponse(), new Object());

    MockHttpServletResponse committedResponse = new MockHttpServletResponse();
    committedResponse.setCommitted(true);

    Assertions.assertTrue(
        interceptor.preHandle(request(DispatcherType.ASYNC), committedResponse, new Object()));
    Assertions.assertEquals(200, committedResponse.getStatus());
  }

  @Test
  void itDoesNotCountNonRequestDispatches() throws IOException {
    interceptor.preHandle(
        request(DispatcherType.ASYNC), new MockHttpServletResponse(), new Object());
    interceptor.preHandle(
        request(DispatcherType.ERROR), new MockHttpServletResponse(), new Object());

    Assertions.assertTrue(
        interceptor.preHandle(
            request(DispatcherType.REQUEST), new MockHttpServletResponse(), new Object()));
  }
}
