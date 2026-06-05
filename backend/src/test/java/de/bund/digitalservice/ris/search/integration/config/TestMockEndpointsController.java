package de.bund.digitalservice.ris.search.integration.config;

import java.nio.file.AccessDeniedException;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller is only used in integration tests to mock endpoints that return specific error
 * responses.
 */
@TestConfiguration
@RestController
public class TestMockEndpointsController {

  @GetMapping("/forbidden")
  public ResponseEntity<String> forbiddenResponse() throws AccessDeniedException {
    throw new AccessDeniedException("forbidden");
  }

  @GetMapping("/internalError")
  public ResponseEntity<String> internalErrorResponse() throws Exception {
    throw new Exception("Unknown error");
  }

  @GetMapping("/clientAbort")
  public ClientAbortedResponse triggerNestedAbort() {
    return new ClientAbortedResponse();
  }

  @GetMapping("throwHttpMessageNotWritableException")
  public void throwHttpMessageNotWritableException() {
    throw new HttpMessageNotWritableException("message not writable");
  }

  /** A DTO designed to throw a ClientAbortException during JSON serialization */
  public static class ClientAbortedResponse {
    /**
     * When Jackson invokes this getter, it simulates a broken pipe mid-stream
     *
     * @return String property that is never actually being returned
     */
    public String getBrokenStreamProperty() throws ClientAbortException {
      throw new ClientAbortException("java.io.IOException: Broken pipe");
    }
  }
}
