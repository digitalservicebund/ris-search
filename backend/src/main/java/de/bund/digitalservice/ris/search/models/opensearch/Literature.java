package de.bund.digitalservice.ris.search.models.opensearch;

import jakarta.persistence.ElementCollection;
import java.util.List;
import lombok.Builder;
import org.opensearch.common.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.Setting;

/** Model class representing a literature opensearch index. */
@Builder
@Document(indexName = "#{@configurations.getLiteratureIndexName()}")
@Setting(settingPath = "/openSearch/german_analyzer.json")
public record Literature(
    @Id @Field(name = Fields.ID) String id,
    @Field(name = Fields.DOCUMENT_NUMBER) String documentNumber,
    @ElementCollection @Field(name = Fields.YEARS_OF_PUBLICATION) List<String> yearsOfPublication,
    @ElementCollection @Field(name = Fields.DOCUMENT_TYPES) List<String> documentTypes,
    @ElementCollection @Field(name = Fields.DEPENDENT_REFERENCES)
        List<DependentReference> dependentReferences,
    @ElementCollection @Field(name = Fields.INDEPENDENT_REFERENCES)
        List<IndependentReference> independentReferences,
    @Nullable @Field(name = Fields.MAIN_TITLE) String mainTitle,
    @Nullable @Field(name = Fields.DOCUMENTARY_TITLE) String documentaryTitle,
    @ElementCollection @Field(name = Fields.AUTHORS) List<Person> authors,
    @ElementCollection @Field(name = Fields.COLLABORATORS) List<Person> collaborators,
    @Nullable @Field(name = Fields.SHORT_REPORT) String shortReport)
    implements AbstractSearchEntity {
  public static class Fields {
    private Fields() {}

    public static final String ID = "id";
    public static final String DOCUMENT_NUMBER = "document_number";
    public static final String YEARS_OF_PUBLICATION = "years_of_publication";
    public static final String DOCUMENT_TYPES = "document_types";

    /** unselbstständige Fundstellen * */
    public static final String DEPENDENT_REFERENCES = "dependent_references";

    /** selbständige Fundstellen * */
    public static final String INDEPENDENT_REFERENCES = "independent_references";

    /** Haupttitel* */
    public static final String MAIN_TITLE = "main_title";

    /** Dokumentarischer Titel * */
    public static final String DOCUMENTARY_TITLE = "documentary_title";

    /** Verfasser * */
    public static final String AUTHORS = "authors";

    /** Mitarbeiter * */
    public static final String COLLABORATORS = "collaborators";

    /** Kurzrefarat * */
    public static final String SHORT_REPORT = "short_report";
  }
}
