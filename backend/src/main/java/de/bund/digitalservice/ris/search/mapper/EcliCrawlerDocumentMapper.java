package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.Courts;
import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.Coverage;
import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.Creator;
import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.Document;
import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.Identifier;
import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.IsVersionOf;
import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.Language;
import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.Metadata;
import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.Publisher;
import de.bund.digitalservice.ris.search.models.eclicrawler.ecli.Type;
import de.bund.digitalservice.ris.search.models.eclicrawler.sitemap.Url;
import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.models.opensearch.EcliCrawlerDocument;
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
                            .setType(new Type())));
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

  private static Creator getCreator(EcliCrawlerDocument doc) {
    return new Creator().setValue(Courts.supportedCourtNames.get(doc.courtType()));
  }
}
