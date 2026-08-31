package de.bund.digitalservice.ris.search.controller.api;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller to serve the jsonLd context */
@RestController
public class JsonLdContextController {

  private final Resource contextResource = new ClassPathResource("/jsonld/1_1/v1.jsonld");

  @GetMapping(path = "/v1/context.jsonld", produces = "application/ld+json")
  public ResponseEntity<Resource> getContext() {
    return ResponseEntity.ok()
        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
        .body(contextResource);
  }
}
