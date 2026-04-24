package de.bund.digitalservice.ris.search.config.obs;

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
   * @return ChangelogService configured to manage case law files
   */
  @Bean
  public ChangelogService caseLawChangelogService(CaseLawBucket bucket) {
    return new ChangelogService(bucket);
  }

  /**
   * @param bucket root bucket of norms files
   * @return ChangelogService configured to manage norms files
   */
  @Bean
  public ChangelogService normsChangelogService(NormsBucket bucket) {
    return new ChangelogService(bucket);
  }

  /**
   * @param bucket root bucket of literature files
   * @return ChangelogService configured to manage literature files
   */
  @Bean
  public ChangelogService literatureChangelogService(LiteratureBucket bucket) {
    return new ChangelogService(bucket);
  }

  /**
   * @param bucket root bucket of administrative directive files
   * @return ChangelogService configured to manage administrative directive files
   */
  @Bean
  public ChangelogService administrativeDirectiveChangelogService(
      AdministrativeDirectiveBucket bucket) {
    return new ChangelogService(bucket);
  }
}
