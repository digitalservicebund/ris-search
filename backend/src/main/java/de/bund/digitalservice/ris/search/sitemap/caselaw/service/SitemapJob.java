package de.bund.digitalservice.ris.search.sitemap.caselaw.service;

import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
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

  public SitemapJob(SitemapService service) {
    this.service = service;
  }

  public void run() throws JAXBException {
    Changelog changelog = new Changelog();
    HashSet<String> changedHashset = new HashSet<>();
    HashSet<String> deletedHashset = new HashSet<>();
    changedHashset.add("MWRE115810500");
    deletedHashset.add("BFRE001666955");

    LocalDate now = LocalDate.now();
    changelog.setChanged(changedHashset);
    changelog.setDeleted(deletedHashset);
    List<UrlSet> urlSets = service.createUrlSets(changelog);
    List<String> urlSetLocations = service.writeUrlSets(urlSets, now);

    Sitemapindex index = service.createSitemapIndex(urlSetLocations);
    service.writeSitemapIndex(index, now);
  }
}
