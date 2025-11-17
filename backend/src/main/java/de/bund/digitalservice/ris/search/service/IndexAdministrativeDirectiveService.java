package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.mapper.AdministrativeDirectiveLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.AdministrativeDirective;
import de.bund.digitalservice.ris.search.repository.objectstorage.AdministrativeDirectiveBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.AdministrativeDirectiveRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class IndexAdministrativeDirectiveService extends BaseIndexService<AdministrativeDirective> {

  public IndexAdministrativeDirectiveService(
      AdministrativeDirectiveBucket bucket, AdministrativeDirectiveRepository repository) {
    super(bucket, repository);
  }

  @Override
  protected Optional<AdministrativeDirective> mapFileToEntity(String filename, String fileContent) {
    try {
      return Optional.of(AdministrativeDirectiveLdmlToOpenSearchMapper.map(fileContent));
    } catch (OpenSearchMapperException e) {
      logger.error("unable to parse file {}", filename, e);
      return Optional.empty();
    }
  }
}
