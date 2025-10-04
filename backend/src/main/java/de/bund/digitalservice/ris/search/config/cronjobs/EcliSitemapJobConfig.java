package de.bund.digitalservice.ris.search.config.cronjobs;

import de.bund.digitalservice.ris.search.repository.objectstorage.CaseLawBucket;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliCrawlerDocumentService;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapJob;
import de.bund.digitalservice.ris.search.service.eclicrawler.EcliSitemapService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EcliSitemapJobConfig {

  EcliSitemapService sitemapService;
  PortalBucket portalBucket;
  CaseLawBucket caseLawBucket;
  CaseLawIndexSyncJob syncJob;
  EcliCrawlerDocumentService documentService;
  final String frontEndUrl;

  public EcliSitemapJobConfig(
      EcliSitemapService sitemapService,
      PortalBucket portalBucket,
      CaseLawBucket caseLawBucket,
      CaseLawIndexSyncJob syncJob,
      EcliCrawlerDocumentService documentService,
      @Value("${server.front-end-url}") String frontEndUrl) {
    this.sitemapService = sitemapService;
    this.portalBucket = portalBucket;
    this.caseLawBucket = caseLawBucket;
    this.syncJob = syncJob;
    this.documentService = documentService;
    this.frontEndUrl = frontEndUrl;
  }

  @Bean
  @Profile("!staging")
  public EcliSitemapJob getSitemapJob() {
    return new EcliSitemapJob(
        sitemapService,
        portalBucket,
        caseLawBucket,
        syncJob,
        documentService,
        frontEndUrl + "v1/eclicrawler/");
  }

  @Bean
  @Profile("staging")
  public EcliSitemapJob getStagingSitemapJob() {
    return new EcliSitemapJob(
        sitemapService,
        portalBucket,
        caseLawBucket,
        syncJob,
        documentService,
        frontEndUrl + "api/v1/eclicrawler/");
  }
}
