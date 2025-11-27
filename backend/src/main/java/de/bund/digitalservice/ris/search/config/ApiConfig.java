package de.bund.digitalservice.ris.search.config;

/** Class to store the API path and version */
public class ApiConfig {

  private ApiConfig() {}

  public static class Paths {
    private Paths() {}

    public static final String BASE = "/v1";

    public static final String DOCUMENT = BASE + "/document";

    public static final String CASELAW = BASE + "/case-law";

    public static final String LITERATURE = BASE + "/literature";

    public static final String ADMINISTRATIVE_DIRECTIVE = BASE + "/administrative-directive";

    public static final String FEEDBACK = BASE + "/feedback";

    public static final String LEGISLATION = BASE + "/legislation";
    public static final String LEGISLATION_SINGLE = LEGISLATION + "/eli";

    public static final String LEGISLATION_TRANSLATION = BASE + "/translatedLegislation";

    public static final String DOCUMENT_ADVANCED_SEARCH = DOCUMENT + "/lucene-search";
    public static final String CASELAW_ADVANCED_SEARCH = DOCUMENT + "/lucene-search/case-law";
    public static final String LEGISLATION_ADVANCED_SEARCH =
        DOCUMENT + "/lucene-search/legislation";
    public static final String LITERATURE_ADVANCED_SEARCH = DOCUMENT + "/lucene-search/literature";
    public static final String ADMINISTRATIVE_DIRECTIVE_ADVANCED_SEARCH =
        DOCUMENT + "/lucene-search/administrative-directive";

    public static final String SITEMAP = BASE + "/sitemaps";
    public static final String ECLICRAWLER = BASE + "/eclicrawler";
    public static final String STATISTICS = BASE + "/statistics";
  }

  public static final String VERSION = "1";
  public static final String DEFAULT_API_PAGE_SIZE = "100";
}
