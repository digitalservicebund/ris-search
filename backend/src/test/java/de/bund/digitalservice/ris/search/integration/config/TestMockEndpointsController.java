package de.bund.digitalservice.ris.search.integration.config;

import java.nio.file.AccessDeniedException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
