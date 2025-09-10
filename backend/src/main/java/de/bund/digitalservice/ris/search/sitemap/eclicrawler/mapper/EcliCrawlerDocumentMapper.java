package de.bund.digitalservice.ris.search.sitemap.eclicrawler.mapper;

import de.bund.digitalservice.ris.search.models.opensearch.CaseLawDocumentationUnit;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.model.EcliCrawlerDocumentOS;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.AccessRights;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Coverage;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Creator;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Document;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Identifier;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.IsVersionOf;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Language;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Metadata;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Publisher;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.ecli.Type;
import de.bund.digitalservice.ris.search.sitemap.eclicrawler.schema.sitemap.Url;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.Map;

public class EcliCrawlerDocumentMapper {

  private EcliCrawlerDocumentMapper() {}

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  private static final String URL_PREFIX = "http://placeholder_url/to/";
  private static final String COVERAGE = "Deutschland";
  private static final String LANGUAGE = "de";
  private static final String LANGUAGE_TYPE = "authoritative";

  private static final Map<String, String> courtLongName =
      Map.ofEntries(
          new AbstractMap.SimpleEntry<>("BGH", "Bundesgerichtshof"),
          new AbstractMap.SimpleEntry<>("BVerwG", "Bundesverwaltungsgericht"),
          new AbstractMap.SimpleEntry<>("BVerfG", "Bundesverfassungsgericht"),
          new AbstractMap.SimpleEntry<>("BFH", "Bundesfinanzhof"),
          new AbstractMap.SimpleEntry<>("BAG", "Bundesarbeitsgericht"),
          new AbstractMap.SimpleEntry<>("BSG", "Bundessozialgericht"),
          new AbstractMap.SimpleEntry<>("BPatG", "Bundespatentgericht"));

  public static EcliCrawlerDocumentOS fromCaseLawDocumentationUnit(CaseLawDocumentationUnit unit) {
    return new EcliCrawlerDocumentOS(
        unit.id(),
        unit.ecli(),
        unit.courtType(),
        unit.decisionDate().format(dateFormatter),
        unit.documentType(),
        true);
  }

  public static Url toSitemapUrl(EcliCrawlerDocumentOS doc) {
    Url url =
        new Url()
            .setLoc(URL_PREFIX + doc.id())
            .setDocument(
                new Document()
                    .setMetadata(
                        new Metadata()
                            .setIdentifier(getIdentifier(doc))
                            .setIsVersionOf(getIsVersionOf(doc))
                            .setCreator(getCreator(doc))
                            .setCoverage(getCoverage())
                            .setDate(doc.decisionDate())
                            .setLanguage(getLanguage())
                            .setAccessRights(AccessRights.PUBLIC)
                            .setPublisher(getPublisher())
                            .setType(getType(doc))));
    if (!doc.isPublished()) {
      url.getDocument().setStatus(Document.STATUS_DELETED);
    }
    return url;
  }

  private static IsVersionOf getIsVersionOf(EcliCrawlerDocumentOS doc) {
    return new IsVersionOf()
        .setValue(doc.ecli())
        .setCountry(IsVersionOf.COUNTRY_DE)
        .setCourt(doc.courtType());
  }

  private static Identifier getIdentifier(EcliCrawlerDocumentOS doc) {
    return new Identifier()
        .setLang(Identifier.LANG_DE)
        .setFormat(Identifier.FORMAT_HTML)
        .setValue(URL_PREFIX + doc.id());
  }

  private static Type getType(EcliCrawlerDocumentOS doc) {
    return new Type().setLang(LANGUAGE).setValue(doc.documentType());
  }

  private static Creator getCreator(EcliCrawlerDocumentOS doc) {
    return new Creator().setLang(LANGUAGE).setValue(courtLongName.get(doc.courtType()));
  }

  private static Coverage getCoverage() {
    return new Coverage().setLang(LANGUAGE).setValue(COVERAGE);
  }

  private static Language getLanguage() {
    return new Language().setLanguageType(LANGUAGE_TYPE).setValue(LANGUAGE);
  }

  private static Publisher getPublisher() {
    return new Publisher().setLang(LANGUAGE).setValue("placeholder_publisher");
  }
}
