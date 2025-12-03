package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.mapper.LiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.mapper.SilLiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for indexing "dependent literature" files into the system. This class provides
 * functionality for mapping and processing literature files that specifically belong to the
 * category of "dependent literature documents."
 *
 * <p>Extends the abstract {@code BaseIndexService} to inherit common indexing operations and
 * overrides specific methods to handle literature-related data mapping.
 */
@Service
public class IndexLiteratureService extends BaseIndexService<Literature> {

  @Autowired
  public IndexLiteratureService(LiteratureBucket bucket, LiteratureRepository repository) {
    super(bucket, repository);
  }

  @Override
  protected Optional<Literature> mapFileToEntity(String filename, String fileContent) {
    try {
      // determine type of literature document based on prefix
      switch (filename.substring(2, 4)) {
        case "LU" -> {
          return Optional.of(LiteratureLdmlToOpenSearchMapper.mapLdml(fileContent));
        }
        case "LS" -> {
          return Optional.of(
              SilLiteratureLdmlToOpenSearchMapper.mapLdml(fileContent, Instant.now()));
        }
        default -> {
          String msg = "unknown literaturetype " + filename;
          logger.error(msg);
        }
      }
      return Optional.empty();
    } catch (OpenSearchMapperException e) {
      logger.error("unable to parse file {}", filename, e);
      return Optional.empty();
    }
  }
}
