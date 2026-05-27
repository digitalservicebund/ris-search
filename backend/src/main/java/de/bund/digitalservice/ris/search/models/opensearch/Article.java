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
  @Field(name = "id")
  private String id;

  @Nullable
  @Field(name = "eid")
  String eId;

  @Field(name = Norm.Fields.EXPRESSION_ELI)
  private String expressionEli;

  @Field(name = Norm.Fields.WORK_ELI)
  private String workEli;

  @Field(name = "name")
  private String name;

  @Field(name = "text")
  String text;

  @Nullable
  @Field(name = "entry_into_force_date", type = FieldType.Date, format = DateFormat.date)
  LocalDate entryIntoForceDate;

  @Nullable
  @Field(name = "expiry_date", type = FieldType.Date, format = DateFormat.date)
  LocalDate expiryDate;

  @Nullable
  @Field(name = "guid")
  String guid;

  @Nullable
  @Field(name = "manifestation_eli")
  String manifestationEli;

  /*
   * A helper field that allows higher-ranked retrieval of articles through a combination of article number and norm
   * abbreviation.
   */
  @Nullable
  @Field(name = "search_keyword")
  String searchKeyword;

  @Field(name = "indexed_at")
  private String indexedAt;
}
