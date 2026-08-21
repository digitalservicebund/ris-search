package de.bund.digitalservice.ris.search.models.opensearch;

import java.time.LocalDate;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Model class representing an article in the opensearch index. This class is annotated with Lombok
 * annotations for generating getters, setters, constructors, and builder methods.
 */
@Data
@Builder // need only for unit tests
@NoArgsConstructor // need only for unit tests
@AllArgsConstructor // need only for unit tests
@Document(indexName = "#{@configurations.getArticlesIndexName()}")
public final class Article implements AbstractSearchEntity {
  @Id
  @Field(name = Fields.ID)
  private String id;

  @Nullable
  @Field(name = Fields.EID)
  String eId;

  @Field(name = Fields.EXPRESSION_ELI)
  private String expressionEli;

  @Field(name = Fields.WORK_ELI)
  private String workEli;

  @Field(name = Fields.NAME)
  private String name;

  @Field(name = Fields.TEXT)
  String text;

  @Nullable
  @Field(name = Fields.ENTRY_INTO_FORCE_DATE, type = FieldType.Date, format = DateFormat.date)
  LocalDate entryIntoForceDate;

  @Nullable
  @Field(name = Fields.EXPIRY_DATE, type = FieldType.Date, format = DateFormat.date)
  LocalDate expiryDate;

  @Nullable
  @Field(name = Fields.GUID)
  String guid;

  @Nullable
  @Field(name = Fields.MANIFESTATION_ELI)
  String manifestationEli;

  /**
   * article_fingerprint is a combination of 2 parts : The article number and the norm abbreviation.
   * For example "§ 7 BGB". If the search has a fingerprint match there is a very high chance the
   * user is looking for this exact article. Therefore, we give a boost in this case. The result is
   * that the targeted article will be boosted to the top of the sub ranking making the targeted
   * article show at the top of the teaser texts. To increase the chance of a match, § has some
   * synonyms allowed such as "paragraf".
   */
  @Nullable
  @Field(name = Fields.ARTICLE_FINGERPRINT)
  String articleFingerprint;

  @Field(name = Fields.INDEXED_AT)
  private String indexedAt;

  /**
   * Builds the composite document id used for articles, combining the norm's expressionEli and the
   * article's eId.
   *
   * @param expressionEli expressionEli of the norm
   * @param eId eId of the article
   * @return the composite id
   */
  public static String buildId(String expressionEli, String eId) {
    return expressionEli + "/" + eId;
  }

  /**
   * The Fields class provides a centralized collection of constant field names that are used as
   * keys, primarily within the context of the Article entity in OpenSearch models. These constants
   * help ensure consistency and reduce errors when accessing or referencing specific fields
   * associated with norms and their elements.
   *
   * <p>The class is utility-oriented and cannot be instantiated.
   */
  public static class Fields {
    private Fields() {}

    public static final String ID = "id";
    public static final String EID = "eid";
    public static final String EXPRESSION_ELI = Norm.Fields.EXPRESSION_ELI;
    public static final String WORK_ELI = Norm.Fields.WORK_ELI;
    public static final String NAME = "name";
    public static final String TEXT = "text";
    public static final String ENTRY_INTO_FORCE_DATE = "entry_into_force_date";
    public static final String EXPIRY_DATE = "expiry_date";
    public static final String GUID = "guid";
    public static final String MANIFESTATION_ELI = "manifestation_eli";
    public static final String ARTICLE_FINGERPRINT = "article_fingerprint";
    public static final String INDEXED_AT = "indexed_at";
  }
}
