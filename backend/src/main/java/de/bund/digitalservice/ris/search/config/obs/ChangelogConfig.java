package de.bund.digitalservice.ris.search.config.obs;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.repository.objectstorage.AdministrativeDirectiveBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registering ChangelogService Beans for specific document types */
@Configuration
public class ChangelogConfig {

  /**
   * @param bucket root bucket of case law files
   * @param om global ObjectMapper to parse the changelog files
   * @return ChangelogService configured to manage case law files
   */
  @Bean
  public ChangelogService<CaseLawBucket> caseLawChangelogService(
      CaseLawBucket bucket, ObjectMapper om) {
    return new ChangelogService<>(bucket, om);
  }

  /**
   * @param bucket root bucket of norms files
   * @param om global ObjectMapper to parse the changelog files
   * @return IndexedChangelogService configured to manage legislation files
   */
  @Bean
  public ChangelogService<NormsBucket> normsChangelogService(NormsBucket bucket, ObjectMapper om) {
    return new ChangelogService<>(bucket, om);
  }

  /**
   * @param bucket root bucket of literature files
   * @param om global ObjectMapper to parse the changelog files
   * @return IndexedChangelogService configured to manage literature files
   */
  @Bean
  public ChangelogService<LiteratureBucket> literatureChangelogService(
      LiteratureBucket bucket, ObjectMapper om) {
    return new ChangelogService<>(bucket, om);
  }

  /**
   * @param bucket root bucket of administrative directive files
   * @param om global ObjectMapper to parse the changelog files
   * @return IndexedChangelogService configured to manage administrative directive files
   */
  @Bean
  public ChangelogService<AdministrativeDirectiveBucket> administrativeDirectiveChangelogService(
      AdministrativeDirectiveBucket bucket, ObjectMapper om) {
    return new ChangelogService<>(bucket, om);
  }
}
