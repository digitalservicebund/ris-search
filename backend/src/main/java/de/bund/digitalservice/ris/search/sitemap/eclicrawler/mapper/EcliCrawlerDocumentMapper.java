package de.bund.digitalservice.ris.search.sitemap.eclicrawler.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.model.EcliCrawlerDocument;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.AccessRights;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Coverage;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Creator;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Document;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Identifier;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.IsVersionOf;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Language;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Metadata;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Publisher;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.SupportedLanguages;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Type;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Url;
import java.time.format.DateTimeFormatter;

public class EcliCrawlerDocumentMapper {

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final String URL_PREFIX = "http://placeholder_url/to/";

  public static EcliCrawlerDocument fromCaseLawDocumentationUnit(CaseLawDocumentationUnit unit) {
    return new EcliCrawlerDocument(
        unit.id(),
        unit.ecli(),
        unit.courtType(),
        unit.decisionDate().format(dateFormatter),
        unit.documentType(),
        true);
  }

  public static Url toSitemapUrl(EcliCrawlerDocument sitemap) {
    Url url =
        new Url()
            .setLoc(URL_PREFIX + sitemap.getId())
            .setDocument(
                new Document()
                    .setMetadata(
                        new Metadata()
                            .setIdentifier(getIdentifier(sitemap))
                            .setIsVersionOf(getIsVersionOf(sitemap))
                            .setCreator(getCreator())
                            .setCoverage(getCoverage())
                            .setDate(sitemap.getDecisionDate())
                            .setLanguage(getLanguage())
                            .setAccessRights(AccessRights.PUBLIC)
                            .setPublisher(getPublisher())
                            .setType(getType(sitemap))));
    if (!sitemap.isPublished()) {
      url.getDocument().setStatus(Document.STATUS_DELETED);
    }
    return url;
  }

  private static IsVersionOf getIsVersionOf(EcliCrawlerDocument change) {
    return new IsVersionOf()
        .setValue(change.getEcli())
        .setCountry(IsVersionOf.COUNTRY_DE)
        .setCourt(change.getCourtType());
  }

  private static Identifier getIdentifier(EcliCrawlerDocument change) {
    return new Identifier()
        .setLang(Identifier.LANG_DE)
        .setFormat(Identifier.FORMAT_HTML)
        .setValue(URL_PREFIX + change.getId());
  }

  private static Type getType(EcliCrawlerDocument change) {
    return new Type().setLang(SupportedLanguages.DE).setValue(change.getDocumentType());
  }

  private static Creator getCreator() {
    return new Creator().setLang(SupportedLanguages.DE).setValue("placeholder_court_long");
  }

  private static Coverage getCoverage() {
    return new Coverage().setLang(SupportedLanguages.DE).setValue("placeholder_germany");
  }

  private static Language getLanguage() {
    return new Language()
        .setLanguageType(SupportedLanguages.TYPE_AUTHORITATIVE)
        .setValue(SupportedLanguages.DE);
  }

  private static Publisher getPublisher() {
    return new Publisher().setLang(SupportedLanguages.DE).setValue("placeholder_publisher");
  }
}
