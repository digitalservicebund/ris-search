package de.bund.digitalservice.ris.search.models.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.Setting;

/** Model class representing a literature to opensearch index. */
@Builder
@Document(indexName = "#{@configurations.getLiteratureIndexName()}")
@Setting(settingPath = "/openSearch/german_analyzer.json")
public record Literature(
    @JsonIgnore @Id @Field(name = Fields.ID) String id,
    @Field(name = Fields.DOCUMENT_NUMBER) String documentNumber,
    @Field(name = Fields.YEAR) String year,
    @Field(name = Fields.DOCUMENT_TYPE) String documentType,
    @Field(name = Fields.REFERENCE) String reference,
    @Field(name = Fields.MAIN_TITLE) String mainTitle,
    @Field(name = Fields.DOCUMENTARY_TITLE) String documentaryTitle,
    @Field(name = Fields.AUTHORS) List<String> authors,
    @Field(name = Fields.EDITORS) List<String> editors,
    @Field(name = Fields.CO_EDITORS) List<String> coEditors,
    @Field(name = Fields.OUTLINE) List<String> outline,
    @Field(name = Fields.SHORT_REPORT) List<String> shortReport)
    implements AbstractSearchEntity {
  public static class Fields {
    private Fields() {}

    public static final String ID = "id";
    public static final String DOCUMENT_NUMBER = "document_number";
    public static final String YEAR = "year";
    public static final String DOCUMENT_TYPE = "document_type";

    /** Fundstelle * */
    public static final String REFERENCE = "reference";

    /** Haupttitel* */
    public static final String MAIN_TITLE = "main_title";

    /** Dokumentarischer Titel * */
    public static final String DOCUMENTARY_TITLE = "documentary_title";

    /** Verfasser * */
    public static final String AUTHORS = "authors";

    /** Bearbeiter * */
    public static final String EDITORS = "editors";

    /** Mitarbeiter * */
    public static final String CO_EDITORS = "co_editors";

    /** Kurzrefarat * */
    public static final String SHORT_REPORT = "short_report";

    /** Gliederung * */
    public static final String OUTLINE = "outline";
  }
}
