package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = CaseLawSearchSchema.class),
  @JsonSubTypes.Type(value = LiteratureSearchSchema.class),
  @JsonSubTypes.Type(value = LegislationWorkSearchSchema.class),
  @JsonSubTypes.Type(value = AdministrativeDirectiveSearchSchema.class)
})
public sealed interface AbstractDocumentSchema
    permits CaseLawSearchSchema,
        LegislationWorkSearchSchema,
        LiteratureSearchSchema,
        AdministrativeDirectiveSearchSchema {}
