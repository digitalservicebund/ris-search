package de.bund.digitalservice.ris.search.sitemap.caselaw.service;

import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.CaseLawIndexSyncJob;
import de.bund.digitalservice.ris.search.service.IndexCaselawService;
import de.bund.digitalservice.ris.search.service.IndexSyncJob;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.Sitemapindex;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.UrlSet;
import jakarta.xml.bind.JAXBException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SitemapJob {

  SitemapService service;

  IndexSyncJob indexJob;

  PortalBucket portalBucket;

  IndexCaselawService indexCaselawService;

  public SitemapJob(
      SitemapService service,
      CaseLawIndexSyncJob indexJob,
      PortalBucket portalBucket,
      IndexCaselawService indexCaselawService) {
    this.service = service;
    this.indexJob = indexJob;
    this.indexCaselawService = indexCaselawService;
    this.portalBucket = portalBucket;
  }

  public void run() throws JAXBException {
    List<String> allCaseLawFiles =
        indexCaselawService.getAllCaseLawFilenames().stream()
            .map(f -> f.replace(".xml", ""))
            .toList();
    Changelog changelog = new Changelog();
    HashSet<String> created = new HashSet<>(allCaseLawFiles);
    changelog.setChanged(created);

    LocalDate now = LocalDate.now();
    changelog.setChanged(created);
    List<UrlSet> urlSets = service.createUrlSets(changelog);
    List<String> urlSetLocations = service.writeUrlSets(urlSets, now);

    Sitemapindex index = service.createSitemapIndex(urlSetLocations);
    service.writeSitemapIndex(index, now);
  }
}
