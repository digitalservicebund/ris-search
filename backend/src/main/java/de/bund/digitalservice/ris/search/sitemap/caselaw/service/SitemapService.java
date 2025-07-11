package de.bund.digitalservice.ris.search.sitemap.caselaw.service;

import de.bund.digitalservice.ris.search.repository.objectstorage.PortalBucket;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.Sitemap;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.Sitemapindex;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.Url;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.UrlSet;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.ecli.Document;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.ecli.Identifier;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.ecli.Metadata;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

@Service
public class SitemapService {

  CaseLawService caseLawService;

  PortalBucket portalBucket;

  JAXBContext jaxbCtx;

  public SitemapService(CaseLawService service, PortalBucket portalBucket) throws JAXBException {
    this.caseLawService = service;
    this.portalBucket = portalBucket;
    this.jaxbCtx = JAXBContext.newInstance(Sitemapindex.class, UrlSet.class);
  }

  public List<UrlSet> createUrlSets(HashSet<String> changed, HashSet<String> deleted)
      throws JAXBException {

    List<ChangedDocument> changedDocuments =
        new ArrayList<>(
            this.caseLawService
                .getByDocumentNumbers(changed.stream().map(f -> f.replace(".xml", "")).toList())
                .stream()
                .map(d -> new ChangedDocument(ChangedDocument.CHANGED, d))
                .toList());

    changedDocuments.addAll(
        this.caseLawService
            .getByDocumentNumbers(deleted.stream().map(f -> f.replace(".xml", "")).toList())
            .stream()
            .map(d -> new ChangedDocument(ChangedDocument.DELETED, d))
            .toList());

    if (changedDocuments.isEmpty()) {
      return List.of();
    }
    List<List<ChangedDocument>> parititonChanged = ListUtils.partition(changedDocuments, 5000);

    return parititonChanged.stream().map(this::createChangedSitemap).toList();
  }

  private UrlSet createChangedSitemap(List<ChangedDocument> changed) {
    UrlSet set = new UrlSet();
    set.setUrl(
        changed.stream()
            .map(
                doc -> {
                  Url url = new Url().setLoc("location/to/" + doc.document().id());
                  url.setDocument(
                      new Document()
                          .setMetadata(
                              new Metadata()
                                  .setIdentifier(new Identifier().setValue(doc.document().id()))));
                  if (doc.status().equals(ChangedDocument.DELETED)) {
                    url.getDocument().setStatus(Document.STATUS_DELETED);
                  }
                  return url;
                })
            .toList());
    return set;
  }

  public Sitemapindex createSitemapIndex(List<String> sitemapLocations) {
    Sitemapindex index = new Sitemapindex();
    index.setSitemaps(sitemapLocations.stream().map(loc -> new Sitemap().setLoc(loc)).toList());

    return index;
  }

  public String writeSitemapIndex(Sitemapindex index, LocalDate date) throws JAXBException {
    StringWriter sw = new StringWriter();
    Marshaller m = jaxbCtx.createMarshaller();
    m.setProperty(
        Marshaller.JAXB_SCHEMA_LOCATION,
        "http://www.sitemaps.org/schemas/sitemap/0.9 "
            + "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd ");
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.marshal(index, sw);

    String filename = String.format("sitemaps/%s/sitemap_index_1.xml", getDatePartition(date));
    portalBucket.save(filename, sw.toString());

    return filename;
  }

  private String getDatePartition(LocalDate date) {
    return String.format("%s/%s/%s", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
  }

  public List<String> writeUrlSets(List<UrlSet> sets, LocalDate date) throws JAXBException {
    Marshaller m = jaxbCtx.createMarshaller();
    m.setProperty(
        Marshaller.JAXB_SCHEMA_LOCATION,
        "http://www.sitemaps.org/schemas/sitemap/0.9 "
            + "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd "
            + "https://e-justice.europa.eu/eclisearch "
            + "https://e-justice.europa.eu/eclisearch/ecli.xsd");
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

    List<String> locations = new ArrayList<>();
    for (int i = 0; i < sets.size(); i++) {
      StringWriter sw = new StringWriter();
      int sitemapNr = i + 1;
      String filename =
          String.format("sitemaps/%s/sitemap_%s.xml", getDatePartition(date), sitemapNr);
      m.marshal(sets.get(i), sw);
      portalBucket.save(filename, sw.toString());
      locations.add(filename);
    }

    return locations;
  }
}
