package de.bund.digitalservice.ris.search.sitemap.eclicrawler.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.Url;
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
import java.time.format.DateTimeFormatter;

public class RisToEcliMapper {

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final String URL_PREFIX = "placeholder_url/to/";

  private RisToEcliMapper() {}

  public static Url caselawDocumentationUnitToEcliUrl(CaseLawDocumentationUnit document) {

    Url url = new Url().setLoc(URL_PREFIX + document.id());
    url.setDocument(
        new Document()
            .setMetadata(
                new Metadata()
                    .setIdentifier(getIdentifier(document))
                    .setIsVersionOf(getIsVersionOf(document))
                    .setCreator(getCreator(document))
                    .setCoverage(getCoverage())
                    .setDate(document.decisionDate().format(dateFormatter))
                    .setLanguage(getLanguage())
                    .setPublisher(getPublisher(document))
                    .setAccessRights(AccessRights.PUBLIC)
                    .setType(getType(document))));
    return url;
  }

  public static Url deletedDocumentToEcliUrl(String identifier) {
    return new Url()
        .setLoc(URL_PREFIX + identifier)
        .setDocument(
            new Document()
                .setMetadata(new Metadata().setIdentifier(new Identifier().setValue(identifier)))
                .setStatus(Document.STATUS_DELETED));
  }

  private static Identifier getIdentifier(CaseLawDocumentationUnit doc) {
    return new Identifier()
        .setLang(Identifier.LANG_DE)
        .setFormat(Identifier.FORMAT_HTML)
        .setValue(URL_PREFIX + doc.id());
  }

  private static IsVersionOf getIsVersionOf(CaseLawDocumentationUnit doc) {
    return new IsVersionOf()
        .setValue(doc.ecli())
        .setCountry(SupportedLanguages.DE)
        .setCourt(doc.courtType());
  }

  private static Creator getCreator(CaseLawDocumentationUnit doc) {
    return new Creator().setLang(SupportedLanguages.DE).setValue("placeholder_court_langform");
  }

  private static Coverage getCoverage() {
    return new Coverage().setLang(SupportedLanguages.DE).setValue("placeholder_deutschland");
  }

  private static Language getLanguage() {
    return new Language()
        .setLanguageType(SupportedLanguages.TYPE_AUTHORITATIVE)
        .setValue(SupportedLanguages.DE);
  }

  private static Publisher getPublisher(CaseLawDocumentationUnit doc) {
    return new Publisher().setLang(SupportedLanguages.DE).setValue("placeholder_publisher");
  }

  private static Type getType(CaseLawDocumentationUnit doc) {
    return new Type().setLang(SupportedLanguages.DE).setValue(doc.documentType());
  }
}
