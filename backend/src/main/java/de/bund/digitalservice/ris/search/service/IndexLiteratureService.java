package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.mapper.LiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.mapper.SliLiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureType;
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
  protected Optional<String> getIdFromFilename(String filename) {
    return DocumentKind.extractIdFromFileName(filename, DocumentKind.LITERATURE);
  }

  @Override
  protected Optional<Literature> mapFileToEntity(String filename, String fileContent) {
    try {
      switch (LiteratureType.getByDocumentNumber(filename)) {
        case SLI -> {
          return Optional.of(
              SliLiteratureLdmlToOpenSearchMapper.mapLdml(fileContent, Instant.now()));
        }
        case ULI -> {
          return Optional.of(LiteratureLdmlToOpenSearchMapper.mapLdml(fileContent));
        }
        default -> {
          String msg = "unknown literaturetype " + filename;
          logger.error(msg);
          return Optional.empty();
        }
      }
    } catch (OpenSearchMapperException e) {
      logger.error("unable to parse file {}", filename, e);
      return Optional.empty();
    }
  }
}
