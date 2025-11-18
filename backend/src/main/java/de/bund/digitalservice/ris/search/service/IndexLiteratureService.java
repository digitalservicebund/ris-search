package de.bund.digitalservice.ris.search.service;

import de.bund.digitalservice.ris.search.exception.OpenSearchMapperException;
import de.bund.digitalservice.ris.search.mapper.LiteratureLdmlToOpenSearchMapper;
import de.bund.digitalservice.ris.search.models.opensearch.Literature;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.opensearch.LiteratureRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexLiteratureService extends BaseIndexService<Literature> {

  @Autowired
  public IndexLiteratureService(LiteratureBucket bucket, LiteratureRepository repository) {
    super(bucket, repository);
  }

  @Override
  protected Optional<Literature> mapFileToEntity(String filename, String fileContent) {
    if (!isDependentLiterature(filename)) {
      logger.warn(
          "Skipping literature file {} because it is not of type 'dependent literature'", filename);
      return Optional.empty();
    }

    try {
      return Optional.of(LiteratureLdmlToOpenSearchMapper.mapLdml(fileContent));
    } catch (OpenSearchMapperException e) {
      logger.error("unable to parse file {}", filename, e);
      return Optional.empty();
    }
  }

  /**
   * Determines the filename belongs to a 'dependent literature document' (unselbständige
   * Literatur). All dependent literature documents can be identified by looking at the 3rd and 4th
   * letter in their filename. If that substring is "LU" it is a dependent literature document.
   *
   * @param filename filename of a literature document
   * @return boolean
   */
  private boolean isDependentLiterature(String filename) {
    return Optional.ofNullable(filename)
        .map(f -> "LU".equals(filename.substring(2, 4)))
        .orElse(false);
  }
}
