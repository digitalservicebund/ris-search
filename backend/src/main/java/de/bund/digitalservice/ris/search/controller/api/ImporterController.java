package de.bund.digitalservice.ris.search.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.exception.ObjectStoreException;
import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.IndexingState;
import de.bund.digitalservice.ris.search.repository.objectstorage.PersistedIndexingState;
import de.bund.digitalservice.ris.search.service.ImportService;
import de.bund.digitalservice.ris.search.service.IndexNormsService;
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

  private final ImportService normsImporter;
  private final IndexNormsService normsIndexer;

  @Autowired
  public ImporterController(ImportService importer, IndexNormsService normsIndexer) {
    this.normsImporter = importer;
    this.normsIndexer = normsIndexer;
  }

  @PostMapping(path = "/norms/changelog")
  public ResponseEntity<String> importNorms(@RequestBody String changelog)
      throws ObjectStoreException {
    try {
      Changelog changelogContent = new ObjectMapper().readValue(changelog, Changelog.class);
      IndexingState state =
          new IndexingState(null, ImportService.NORM_STATUS_FILENAME, normsIndexer);
      state.setPersistedIndexingState(new PersistedIndexingState(null, null, "apiRequest", null));
      normsImporter.importChangelogContent(changelogContent, state);
      return ResponseEntity.noContent().build();
    } catch (JsonProcessingException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
