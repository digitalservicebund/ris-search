package de.bund.digitalservice.ris.search.controller;

import de.bund.digitalservice.ris.search.controller.api.ControllerExceptionHandler;
import de.bund.digitalservice.ris.search.models.errors.CustomErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller class to handle custom error responses in the application. Implements the Spring Boot
 * {@link ErrorController} interface to provide a centralized mechanism for returning appropriate
 * error responses based on HTTP status codes.
 */
@Controller
public class CustomErrorController implements ErrorController {

  /**
   * Handles error requests and returns the appropriate error response based on the HTTP status
   * code.
   *
   * @param request the HttpServletRequest containing error details, such as the HTTP status code
   * @return a ResponseEntity containing a CustomErrorResponse specific to the detected error code
   */
  @RequestMapping("/error")
  public ResponseEntity<CustomErrorResponse> handleErrors(HttpServletRequest request) {
    int statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

    if (statusCode == HttpStatus.FORBIDDEN.value()) {
      return ControllerExceptionHandler.return403();
    }
    if (statusCode == HttpStatus.TOO_MANY_REQUESTS.value()) {
      return ControllerExceptionHandler.return429();
    }
    return ControllerExceptionHandler.return500();
  }
}
