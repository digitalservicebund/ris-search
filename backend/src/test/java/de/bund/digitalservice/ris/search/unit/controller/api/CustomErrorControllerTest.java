package de.bund.digitalservice.ris.search.unit.controller.api;

import de.bund.digitalservice.ris.search.controller.CustomErrorController;
import jakarta.servlet.RequestDispatcher;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.mock.web.MockHttpServletRequest;

class CustomErrorControllerTest {

  CustomErrorController controller = new CustomErrorController();

  @Test
  void itMapsTooManyRequestsToProperErrorResponse() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 429);
    var resp = controller.handleErrors(request);

    Assertions.assertEquals(HttpStatusCode.valueOf(429), resp.getStatusCode());
    var body = Objects.requireNonNull(resp.getBody());
    Assertions.assertEquals(1, body.errors().size());
    Assertions.assertEquals("too_many_requests", body.errors().getFirst().code());
    Assertions.assertEquals(
        "Too many requests. Please try again later", body.errors().getFirst().message());
  }
}
