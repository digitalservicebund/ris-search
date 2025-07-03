package de.bund.digitalservice.ris.search.sitemap.caselaw.service;

import de.bund.digitalservice.ris.search.importer.changelog.Changelog;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.service.CaseLawService;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.Url;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.UrlSet;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.ecli.Document;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.ecli.Identifier;
import de.bund.digitalservice.ris.search.sitemap.caselaw.schema.ecli.Metadata;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SitemapService {

  CaseLawService caseLawService;

  public SitemapService(CaseLawService service) {
    this.caseLawService = service;
  }

  public Optional<String> createSitemaps(Changelog changelog) throws JAXBException {
    HashSet<String> changed = changelog.getChanged();
    List<CaseLawDocumentationUnit> changedDocuments =
        this.caseLawService.getByDocumentNumbers(changed.stream().toList());

    if (changedDocuments.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(this.createChangedSitemap(changedDocuments));
  }

  private String createChangedSitemap(List<CaseLawDocumentationUnit> docs) throws JAXBException {
    UrlSet set = new UrlSet();
    set.setUrl(
        docs.stream()
            .map(
                doc -> {
                  Url url = new Url().setLoc("location/to/" + doc.id());
                  url.setDocument(
                      new Document()
                          .setMetadata(
                              new Metadata().setIdentifier(new Identifier().setValue(doc.id()))));
                  return url;
                })
            .toList());

    StringWriter sw = new StringWriter();
    JAXBContext ctx = JAXBContext.newInstance(UrlSet.class);
    Marshaller m = ctx.createMarshaller();
    m.setProperty(
        Marshaller.JAXB_SCHEMA_LOCATION,
        "http://www.sitemaps.org/schemas/sitemap/0.9 "
            + "http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd "
            + "https://e-justice.europa.eu/eclisearch "
            + "https://e-justice.europa.eu/eclisearch/ecli.xsd");
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    m.marshal(set, sw);

    return sw.toString();
  }
}
