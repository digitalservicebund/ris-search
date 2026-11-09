package de.bund.digitalservice.ris.search.config;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import tools.jackson.core.exc.JacksonIOException;

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

  /**
   * Throws the exception chain observed in production when a client hangs up while Jackson flushes
   * the response: HttpMessageNotWritableException -> JacksonIOException ->
   * AsyncRequestNotUsableException -> ClientAbortException -> IOException "Broken pipe".
   */
  @GetMapping("/clientAbortDuringJsonFlush")
  public void throwClientAbortDuringJsonFlush() {
    IOException brokenPipe = new IOException("Broken pipe");
    ClientAbortException clientAbort = new ClientAbortException(brokenPipe);
    AsyncRequestNotUsableException asyncNotUsable =
        new AsyncRequestNotUsableException(
            "ServletOutputStream failed to write: " + brokenPipe, clientAbort);
    throw new HttpMessageNotWritableException(
        "Could not write JSON", JacksonIOException.construct(asyncNotUsable));
  }

  /** Client abort not wrapped by Jackson, e.g. while writing a byte[] or Resource response. */
  @GetMapping("/clientAbortUnwrapped")
  public void throwUnwrappedClientAbort() throws ClientAbortException {
    throw new ClientAbortException(new IOException("Broken pipe"));
  }

  /** Async request not usable anymore, not wrapped by Jackson. */
  @GetMapping("/asyncRequestNotUsable")
  public void throwAsyncRequestNotUsable() throws AsyncRequestNotUsableException {
    throw new AsyncRequestNotUsableException(
        "ServletOutputStream failed to write: java.io.IOException: Broken pipe",
        new ClientAbortException(new IOException("Broken pipe")));
  }

  @GetMapping("throwHttpMessageNotWritableException")
  public void throwHttpMessageNotWritableException() {
    throw new HttpMessageNotWritableException("message not writable");
  }

  /** A DTO designed to throw a ClientAbortException during JSON serialization */
  public static class ClientAbortedResponse {
    /**
     * When Jackson invokes this getter, it simulates a broken pipe mid-stream. Like Tomcat does,
     * the ClientAbortException wraps the underlying IOException.
     *
     * @return String property that is never actually being returned
     */
    public String getBrokenStreamProperty() throws ClientAbortException {
      throw new ClientAbortException(new IOException("Broken pipe"));
    }
  }
}
