package de.bund.digitalservice.ris.search.config.obs;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.bund.digitalservice.ris.search.repository.objectstorage.AdministrativeDirectiveBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.LiteratureBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.NormsBucket;
import de.bund.digitalservice.ris.search.service.ChangelogService;
import org.springframework.beans.factory.annotation.Value;
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
      CaseLawBucket bucket,
      @Value("${s3.file-storage.case-law.versionPrefix}") String versionPrefix,
      ObjectMapper om) {
    return new ChangelogService<>(bucket, versionPrefix, om);
  }

  /**
   * @param bucket root bucket of norms files
   * @param om global ObjectMapper to parse the changelog files
   * @return IndexedChangelogService configured to manage legislation files
   */
  @Bean
  public ChangelogService<NormsBucket> normsChangelogService(
      NormsBucket bucket,
      @Value("${s3.file-storage.norm.versionPrefix}") String versionPrefix,
      ObjectMapper om) {
    return new ChangelogService<>(bucket, versionPrefix, om);
  }

  /**
   * @param bucket root bucket of literature files
   * @param om global ObjectMapper to parse the changelog files
   * @return IndexedChangelogService configured to manage literature files
   */
  @Bean
  public ChangelogService<LiteratureBucket> literatureChangelogService(
      LiteratureBucket bucket,
      @Value("${s3.file-storage.literature.versionPrefix}") String versionPrefix,
      ObjectMapper om) {
    return new ChangelogService<>(bucket, versionPrefix, om);
  }

  /**
   * @param bucket root bucket of administrative directive files
   * @param om global ObjectMapper to parse the changelog files
   * @return IndexedChangelogService configured to manage administrative directive files
   */
  @Bean
  public ChangelogService<AdministrativeDirectiveBucket> administrativeDirectiveChangelogService(
      AdministrativeDirectiveBucket bucket,
      @Value("${s3.file-storage.administrative-directive.versionPrefix}") String versionPrefix,
      ObjectMapper om) {
    return new ChangelogService<>(bucket, versionPrefix, om);
  }
}
