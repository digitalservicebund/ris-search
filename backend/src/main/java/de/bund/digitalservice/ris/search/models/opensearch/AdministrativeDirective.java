package de.bund.digitalservice.ris.search.models.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.ElementCollection;
import java.util.List;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.lang.Nullable;

@Builder
@Document(indexName = "#{@configurations.getAdministrativeDirectiveIndexName()}")
public record AdministrativeDirective(
    @Id @Field(name = Fields.ID) String id,
    @Field(name = Fields.DOCUMENT_NUMBER) String documentNumber,
    @Nullable @Field(name = Fields.LONG_TITLE) String longTitle,
    @Field(name = Fields.DOCUMENT_CATEGORY) String documentCategory,
    @Nullable @Field(name = Fields.DOCUMENT_TYPE) String documentType,
    @Nullable @Field(name = Fields.CONTENT) String content,
    @Nullable @Field(name = Fields.LEGISLATOR) String normgeber,
    @Nullable @Field(name = Fields.ENTRY_INTO_EFFECT_DATE) String entryIntoEffectDate,
    @Nullable @Field(name = Fields.EXPIRY_DATE) String expiryDate,
    @ElementCollection @Field(name = Fields.TOC_ITEMS) List<String> tocItems,
    @ElementCollection @Field(name = Fields.NORM_REFERENCES) List<String> normReferences,
    @ElementCollection @Field(name = Fields.CASELAW_REFERENCES) List<String> caselawReferences,
    @ElementCollection @Field(name = Fields.FUNDSTELLE_REFERENCES)
        List<String> fundstelleReferences,
    @ElementCollection @Field(name = Fields.ZITIERDATUM_ITEMS) List<String> zitierdatumItems,
    @ElementCollection @Field(name = Fields.REFERENCE_NUMBERS) List<String> referenceNumbers,
    @ElementCollection @Field(name = Fields.ACTIVE_ADMINISTRATIVE_REFERENCES)
        List<String> activeAdministrativeReferences,
    @ElementCollection @Field(name = Fields.ACTIVE_NORM_REFERENCES)
        List<String> activeNormReferences,
    @ElementCollection @Field(name = Fields.KEYWORDS) List<String> keywords,
    @ElementCollection @Field(name = Fields.FIELDS_OF_LAW) List<String> fieldsOfLaw,
    @ElementCollection @Field(name = Fields.ZUORDNUNGEN) List<String> zuordnungen,
    @JsonIgnore @Field(name = Literature.Fields.INDEXED_AT) String indexedAt)
    implements AbstractSearchEntity {

  public static class Fields {
    private Fields() {}

    public static final String ID = "id";

    public static final String DOCUMENT_NUMBER = "document_number";

    public static final String LONG_TITLE = "long_title";

    public static final String DOCUMENT_CATEGORY = "document_category";

    public static final String DOCUMENT_TYPE = "document_type";

    public static final String CONTENT = "content";

    public static final String LEGISLATOR = "normgeber";

    public static final String ENTRY_INTO_EFFECT_DATE = "entry_into_effect_date";

    public static final String EXPIRY_DATE = "expiry_date";

    public static final String TOC_ITEMS = "tocItems";

    public static final String NORM_REFERENCES = "norm_references";

    public static final String CASELAW_REFERENCES = "caselaw_references";

    public static final String FUNDSTELLE_REFERENCES = "fundstelle_references";

    public static final String ZITIERDATUM_ITEMS = "zitierdatum_items";

    public static final String REFERENCE_NUMBERS = "reference_numbers";

    public static final String ACTIVE_ADMINISTRATIVE_REFERENCES =
        "active_administrative_references";

    public static final String ACTIVE_NORM_REFERENCES = "active_norm_references";

    public static final String KEYWORDS = "keywords";

    public static final String FIELDS_OF_LAW = "fields_of_law";

    public static final String ZUORDNUNGEN = "zuordnungen";
  }
}
