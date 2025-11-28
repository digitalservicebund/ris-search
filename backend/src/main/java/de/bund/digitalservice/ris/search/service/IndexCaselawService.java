package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.mapper.CaseLawLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Service for indexing caselaw into OpenSearch. */
@Service
public class IndexCaselawService extends BaseIndexService<CaseLawDocumentationUnit> {

  private final CaseLawLdmlToOpenSearchMapper marshaller;

  /** Constructor for IndexCaselawService. */
  @Autowired
  public IndexCaselawService(
      CaseLawBucket bucket,
      CaseLawRepository repository,
      CaseLawLdmlToOpenSearchMapper marshaller) {
    super(bucket, repository);
    this.marshaller = marshaller;
  }

  @Override
  protected Optional<CaseLawDocumentationUnit> mapFileToEntity(
      String filename, String fileContent) {
    try {
      return Optional.of(marshaller.fromString(fileContent));
    } catch (OpenSearchMapperException e) {
      logger.error("unable to parse file {}", filename, e);
      return Optional.empty();
    }
  }
}
