package de.bund.digitalservice.ris.search.controller.api;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller to serve the jsonLd context */
@RestController
public class JsonLdContextController {

  private final Resource contextResource = new ClassPathResource("/jsonld/1_1/v1.jsonld");

  @GetMapping(path = ApiConfig.Paths.JSONLD_CONTEXT, produces = "application/ld+json")
  public ResponseEntity<Resource> getContext() {
    return ResponseEntity.ok().body(contextResource);
  }
}
