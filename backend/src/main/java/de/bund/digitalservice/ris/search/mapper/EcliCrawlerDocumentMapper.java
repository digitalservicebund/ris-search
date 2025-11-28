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

/**
 * Utility class responsible for mapping and transforming data between {@code EcliCrawlerDocument}
 * and other objects such as {@code Url}. This class provides static methods for creating and
 * converting case law documents for use in an ecli crawler application.
 *
 * <p>The primary functions include: 1. Creating an {@code EcliCrawlerDocument} instance from case
 * law metadata. 2. Converting an {@code EcliCrawlerDocument} into a {@code Url} object, suitable
 * for sitemap usage.
 *
 * <p>This class encapsulates methods that process metadata such as court types, decision dates,
 * ECLI identifiers, and publication status to facilitate the correct mapping of domain-specific
 * information.
 */
public class EcliCrawlerDocumentMapper {

  private EcliCrawlerDocumentMapper() {}

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Creates an instance of {@code EcliCrawlerDocument} from the provided URL, file path, and {@code
   * CaseLawDocumentationUnit}.
   *
   * @param url the base URL of the case law document, used to construct the document's full URL
   * @param filepath the file path where the case law document is located
   * @param unit the {@code CaseLawDocumentationUnit} containing detailed information about the case
   *     law, such as document number, ECLI, court type, and decision date
   * @return an {@code EcliCrawlerDocument} initialized with the details from the given {@code
   *     CaseLawDocumentationUnit}
   */
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

  /**
   * Converts an instance of {@code EcliCrawlerDocument} into a {@code Url} object, suitable for use
   * in a sitemap. The resulting {@code Url} includes the document's location (URL) and metadata
   * based on the provided document's attributes.
   *
   * @param doc the {@code EcliCrawlerDocument} object containing data to be mapped into a {@code
   *     Url}
   * @return a {@code Url} object containing the document's location and associated metadata; if the
   *     document is not published, its status is set to "deleted"
   */
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
