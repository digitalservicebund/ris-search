package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.mapper.LiteratureLdmlToOpenSearchMapper;
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
 * Service class for indexing literature files (selbstständige and unselbstständige literature) into
 * the system. This class provides functionality for mapping and processing literature files.
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
    if (LiteratureType.getByDocumentNumber(filename) == LiteratureType.UNKNOWN) {
      String msg = "unknown literaturetype " + filename;
      logger.error(msg);
      return Optional.empty();
    }
    try {
      return Optional.of(LiteratureLdmlToOpenSearchMapper.mapLdml(fileContent, Instant.now()));
    } catch (OpenSearchMapperException e) {
      logger.error("unable to parse file {}", filename, e);
      return Optional.empty();
    }
  }
}
