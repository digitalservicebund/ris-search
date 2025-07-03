package de.bund.digitalservice.ris.search.eclicrawler.mapper;

import de.bund.digitalservice.ris.search.eclicrawler.model.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.eclicrawler.schema.ecli.Courts;
import de.bund.digitalservice.ris.search.eclicrawler.schema.ecli.Coverage;
import de.bund.digitalservice.ris.search.eclicrawler.schema.ecli.Creator;
import de.bund.digitalservice.ris.search.eclicrawler.schema.ecli.Document;
import de.bund.digitalservice.ris.search.eclicrawler.schema.ecli.Identifier;
import de.bund.digitalservice.ris.search.eclicrawler.schema.ecli.IsVersionOf;
import de.bund.digitalservice.ris.search.eclicrawler.schema.ecli.Language;
import de.bund.digitalservice.ris.search.eclicrawler.schema.ecli.Metadata;
import de.bund.digitalservice.ris.search.eclicrawler.schema.ecli.Publisher;
import de.bund.digitalservice.ris.search.eclicrawler.schema.ecli.Type;
import de.bund.digitalservice.ris.search.eclicrawler.schema.sitemap.Url;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import java.time.format.DateTimeFormatter;

public class EcliCrawlerDocumentMapper {

  private EcliCrawlerDocumentMapper() {}

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static EcliCrawlerDocument fromCaseLawDocumentationUnit(
      String url, String filepath, CaseLawDocumentationUnit unit) {
    return new EcliCrawlerDocument(
        unit.documentNumber(),
        filepath,
        unit.ecli(),
        unit.courtType(),
        unit.decisionDate().format(dateFormatter),
        unit.documentType(),
        url + unit.documentNumber(),
        true);
  }

  public static Url toSitemapUrl(EcliCrawlerDocument doc) {
    Url url =
        new Url()
            .setLoc(doc.url())
            .setDocument(
                new Document()
                    .setMetadata(
                        new Metadata()
                            .setIdentifier(getIdentifier(doc.url()))
                            .setIsVersionOf(getIsVersionOf(doc))
                            .setCreator(getCreator(doc))
                            .setCoverage(new Coverage())
                            .setDate(doc.decisionDate())
                            .setLanguage(new Language())
                            .setPublisher(new Publisher())
                            .setType(getType(doc))));
    if (!doc.isPublished()) {
      url.getDocument().setStatus(Document.STATUS_DELETED);
    }
    return url;
  }

  private static IsVersionOf getIsVersionOf(EcliCrawlerDocument doc) {
    return new IsVersionOf().setValue(doc.ecli()).setCourt(doc.courtType());
  }

  private static Identifier getIdentifier(String location) {
    return new Identifier().setValue(location);
  }

  private static Type getType(EcliCrawlerDocument doc) {
    return new Type().setValue(doc.documentType());
  }

  private static Creator getCreator(EcliCrawlerDocument doc) {
    return new Creator().setValue(Courts.supportedCourtNames.get(doc.courtType()));
  }
}
