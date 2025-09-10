package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.mapper.CaseLawLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.CaseLawRepository;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexCaselawService extends BaseIndexService<CaseLawDocumentationUnit> {

  private static final Logger logger = LogManager.getLogger(IndexCaselawService.class);

  @Autowired
  public IndexCaselawService(CaseLawBucket bucket, CaseLawRepository repository) {
    super(bucket, repository, logger);
  }

  protected String extractIdFromFilename(String filename) {
    return filename.substring(0, filename.lastIndexOf('.'));
  }

  protected Optional<CaseLawDocumentationUnit> mapFileToEntity(
      String filename, String fileContent) {
    try {
      return Optional.of(CaseLawLdmlToOpenSearchMapper.fromString(fileContent));
    } catch (OpenSearchMapperException e) {
      logger.error("unable to parse file {}", filename, e);
      return Optional.empty();
    }
  }

  protected List<String> getAllIndexableFilenames() {
    return bucket.getAllKeys().stream()
        .filter(s -> s.endsWith(".xml") && !s.contains(IndexSyncJob.CHANGELOGS_PREFIX))
        .toList();
  }
}
