package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Represents a base interface for document search schemas used in various legal or informational
 * contexts. This sealed interface is implemented by multiple document schemas, each tailored to a
 * specific type of document, including case law, legislation works, literature, and administrative
 * directives.
 *
 * <p>The annotated structure facilitates polymorphic serialization and deserialization using
 * the @type property in JSON. Derived types can be identified and processed accordingly.
 *
 * <p>Implementing classes: - CaseLawSearchSchema: Represents case law-related documents, including
 * details such as decision date, court type, and document type. - LegislationWorkSearchSchema:
 * Represents legislative documents including metadata like legislation dates, publication data, and
 * legislative identifiers. - LiteratureSearchSchema: Represents literature documents with details
 * such as publication years, authors, and specific language attributes. -
 * AdministrativeDirectiveSearchSchema: Represents administrative directive documents including
 * details like document type, entry into force date, and reference numbers.
 *
 * <p>This interface is a sealed type and can only be implemented by the explicitly permitted
 * classes.
 *
 * <p>The use of @JsonTypeInfo and @JsonSubTypes annotations allows the application to handle
 * instances of this interface seamlessly during JSON serialization and deserialization processes.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "@type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = CaseLawSearchSchema.class, name = "Decision"),
  @JsonSubTypes.Type(value = LiteratureSearchSchema.class, name = "Literature"),
  @JsonSubTypes.Type(value = LegislationWorkSearchSchema.class, name = "Legislation"),
  @JsonSubTypes.Type(
      value = AdministrativeDirectiveSearchSchema.class,
      name = "AdministrativeDirective")
})
public sealed interface AbstractDocumentSchema
    permits CaseLawSearchSchema,
        LegislationWorkSearchSchema,
        LiteratureSearchSchema,
        AdministrativeDirectiveSearchSchema {}
