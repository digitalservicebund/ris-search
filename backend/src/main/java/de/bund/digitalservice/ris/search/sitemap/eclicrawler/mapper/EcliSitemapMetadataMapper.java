package de.bund.digitalservice.ris.search.sitemap.eclicrawler.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.repository.EcliSitemapMetadata;
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
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.service.EcliDocumentChange;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class EcliSitemapMetadataMapper {

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final String URL_PREFIX = "http://placeholder_url/to/";

  public static EcliSitemapMetadata fromCaseLawDocumentationUnit(CaseLawDocumentationUnit unit) {
    EcliSitemapMetadata metadata = new EcliSitemapMetadata();
    metadata.setId(unit.id());
    metadata.setEcli(unit.ecli());
    metadata.setCourtType(unit.courtType());
    metadata.setDecisionDate(unit.decisionDate().format(dateFormatter));
    metadata.setDocumentType(unit.documentType());
    return metadata;
  }

  public static Url toSitemapItem(EcliDocumentChange change) {
    EcliSitemapMetadata metadata = change.metadata();
    Url url =
        new Url()
            .setLoc(URL_PREFIX + metadata.getId())
            .setDocument(
                new Document()
                    .setMetadata(
                        new Metadata()
                            .setIdentifier(getIdentifier(metadata))
                            .setIsVersionOf(getIsVersionOf(metadata))
                            .setCreator(getCreator())
                            .setCoverage(getCoverage())
                            .setDate(metadata.getDecisionDate())
                            .setLanguage(getLanguage())
                            .setAccessRights(AccessRights.PUBLIC)
                            .setPublisher(getPublisher())
                            .setType(getType(metadata))));
    if (Objects.equals(change.type(), EcliDocumentChange.ChangeType.DELETE)) {
      url.getDocument().setStatus(Document.STATUS_DELETED);
    }
    return url;
  }

  private static IsVersionOf getIsVersionOf(EcliSitemapMetadata pub) {
    return new IsVersionOf()
        .setValue(pub.getEcli())
        .setCountry(IsVersionOf.COUNTRY_DE)
        .setCourt(pub.getCourtType());
  }

  private static Identifier getIdentifier(EcliSitemapMetadata pub) {
    return new Identifier()
        .setLang(Identifier.LANG_DE)
        .setFormat(Identifier.FORMAT_HTML)
        .setValue(URL_PREFIX + pub.getId());
  }

  private static Type getType(EcliSitemapMetadata pub) {
    return new Type().setLang(SupportedLanguages.DE).setValue(pub.getDocumentType());
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
