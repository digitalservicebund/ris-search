package de.bund.digitalservice.ris.search.controller;

import de.bund.digitalservice.ris.search.controller.api.ControllerExceptionHandler;
import de.bund.digitalservice.ris.search.models.errors.CustomErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

  @RequestMapping("/error")
  public ResponseEntity<CustomErrorResponse> handleErrors(HttpServletRequest request) {
    int statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

    if (statusCode == HttpStatus.FORBIDDEN.value()) {
      return ControllerExceptionHandler.return403();
    } else {
      return ControllerExceptionHandler.return500();
    }
  }
}
