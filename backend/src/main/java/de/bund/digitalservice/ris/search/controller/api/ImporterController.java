package de.bund.digitalservice.ris.search.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.exception.ObjectStoreServiceException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.service.NormIndexSyncJob;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("default")
@RequestMapping(path = "/internal/import")
public class ImporterController {

  private final NormIndexSyncJob normIndexSyncJob;

  @Autowired
  public ImporterController(NormIndexSyncJob normIndexSyncJob) {
    this.normIndexSyncJob = normIndexSyncJob;
  }

  @PostMapping(path = "/norms/changelog")
  public ResponseEntity<String> importNorms(@RequestBody String changelog) {
    try {
      Changelog changelogContent = new ObjectMapper().readValue(changelog, Changelog.class);
      normIndexSyncJob.importChangelogContent(
          changelogContent, Instant.now().toString(), "apiRequest");
      return ResponseEntity.noContent().build();
    } catch (JsonProcessingException | ObjectStoreServiceException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
