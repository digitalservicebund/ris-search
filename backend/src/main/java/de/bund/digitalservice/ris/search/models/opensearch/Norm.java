package de.bund.digitalservice.ris.search.models.opensearch;

import de.bund.digitalservice.ris.search.config.ApiConfig;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Model class representing a norms to opensearch index. This class is annotated with Lombok
 * annotations for generating getters, setters, constructors, and builder methods.
 */
@Data
@Builder // need only for unit tests
@NoArgsConstructor // need only for unit tests
@AllArgsConstructor // need only for unit tests
@EqualsAndHashCode
@Document(indexName = "#{@configurations.getNormsIndexName()}")
public final class Norm implements AbstractSearchEntity {
  @Id
  @Field(name = Fields.ID)
  private String id;

  @Field(name = Fields.WORK_ELI)
  private String workEli;

  @Field(name = Fields.EXPRESSION_ELI)
  private String expressionEli;

  /**
   * The documents represented by this class represent legislation expressions, meaning concrete
   * versions of legislation, but in no specific format. Nevertheless, we also save a sample
   * manifestation ELI, which will usually be the latest available XML version of this particular
   * expression. Example: eli/bund/bgbl-1/2010/s622/2010-04-27/1/deu/2010-04-27/regelungstext-1.xml
   */
  @Field(name = Fields.LATEST_MANIFESTATION_ELI)
  private String manifestationEliExample;

  @Field(name = Fields.PUBLISHED_IN)
  private String publishedIn;

  @Field(name = Fields.OFFICIAL_TITLE)
  private String officialTitle;

  @Field(name = Fields.OFFICIAL_SHORT_TITLE)
  private String officialShortTitle;

  @Field(name = Fields.OFFICIAL_ABBREVIATION)
  private String officialAbbreviation;

  /**
   * The date of adoption or signature of the legislation. This is the date at which the text is
   * officially acknowledged to be a legislation, even though it might not even be published or in
   * force ("Ausfertigungsdatum").
   */
  @Field(name = Fields.NORMS_DATE, type = FieldType.Date, format = DateFormat.date)
  private LocalDate normsDate;

  /** "Verkündungsdatum", the date when the law was published in the official gazette. */
  @Field(name = Fields.DATE_PUBLISHED, type = FieldType.Date, format = DateFormat.date)
  private LocalDate datePublished;

  @Field(name = Fields.EXPIRY_DATE, type = FieldType.Date, format = DateFormat.date)
  private LocalDate expiryDate;

  @Field(name = Fields.ENTRY_INTO_FORCE_DATE, type = FieldType.Date, format = DateFormat.date)
  private LocalDate entryIntoForceDate;

  @Field(name = Fields.ARTICLES, type = FieldType.Nested)
  private List<Article> articles;

  @Field(name = Fields.ARTICLE_NAMES)
  private List<String> articleNames;

  @Field(name = Fields.ARTICLE_TEXTS)
  private List<String> articleTexts;

  @Field(name = Fields.INDEXED_AT)
  private String indexedAt;

  @Field(name = Fields.TABLE_OF_CONTENTS, index = false)
  private List<TableOfContentsItem> tableOfContents;

  @Field(name = Fields.CONCLUSIONS_FORMULA)
  private List<String> conclusionsFormula;

  @Field(name = Fields.PREAMBLE_FORMULA)
  private List<String> preambleFormula;

  public String getHtmlContentUrl() {
    String contentBaseUrl = ApiConfig.Paths.LEGISLATION + "/";
    return (contentBaseUrl + getManifestationEliExample()).replace(".xml", ".html");
  }

  public static class Fields {
    private Fields() {}

    public static final String ARTICLES = "articles";
    public static final String ARTICLE_NAMES = "article_names";
    public static final String ARTICLE_TEXTS = "article_texts";
    public static final String ENTRY_INTO_FORCE_DATE = "entry_into_force_date";
    public static final String EXPIRY_DATE = "expiry_date";
    public static final String ID = "id";
    public static final String INDEXED_AT = "indexed_at";
    public static final String NORMS_DATE = "norms_date";
    public static final String DATE_PUBLISHED = "date_published";
    public static final String OFFICIAL_ABBREVIATION = "official_abbreviation";
    public static final String OFFICIAL_ABBREVIATION_KEYWORD = "official_abbreviation.keyword";
    public static final String OFFICIAL_SHORT_TITLE = "official_short_title";
    public static final String OFFICIAL_SHORT_TITLE_KEYWORD = "official_short_title.keyword";
    public static final String OFFICIAL_TITLE = "official_title";
    public static final String OFFICIAL_TITLE_KEYWORD = "official_title.keyword";
    public static final String PUBLISHED_IN = "published_in";
    public static final String WORK_ELI = "work_eli";
    public static final String WORK_ELI_KEYWORD = "work_eli.keyword";
    public static final String EXPRESSION_ELI = "expression_eli";
    public static final String EXPRESSION_ELI_KEYWORD = "expression_eli.keyword";
    public static final String LATEST_MANIFESTATION_ELI = "latest_manifestation_eli";
    public static final String TABLE_OF_CONTENTS = "table_of_contents";
    public static final String CONCLUSIONS_FORMULA = "conclusions_formula";
    public static final String PREAMBLE_FORMULA = "preamble_formula";
  }
}
