package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

/**
 * Represents a schema for administrative directive search results.
 *
 * <p>This record defines the metadata structure for an administrative directive, which includes
 * details such as document identification, title, type, associated reference numbers, and entry
 * into force date. It is primarily designed to facilitate querying, indexing, and displaying
 * administrative directives in a structured manner.
 *
 * <p>The class is annotated and structured to support JSON-LD (JSON Linked Data) serialization and
 * deserialization, ensuring compatibility with linked data principles.
 *
 * <p>Fields: - id: The unique identifier for the administrative directive, represented as a JSON-LD
 * ID. - documentNumber: The document number associated with the administrative directive. It serves
 * as an identifier for internal or external referencing. - headline: The main title or headline of
 * the administrative directive. - documentType: Specifies the type of the document, such as "VV" or
 * other predefined types. - referenceNumbers: A list of reference numbers (aktenzeichen) relevant
 * to the administrative directive. - entryIntoForceDate: Specifies the date when the administrative
 * directive became effective.
 */
@Builder
public record AdministrativeDirectiveSearchSchema(
    @Schema(example = "KALU000000000") @JsonProperty("@id") String id,
    @Schema(description = "Dokumentnummer", example = "KALU000000000") String documentNumber,
    @Nullable @Schema(description = "Haupttitel") String headline,
    @Nullable @Schema(description = "Kurzreferat") String shortReport,
    @Schema(description = "Dokumenttyp", example = "VV") String documentType,
    @Schema(description = "Aktenzeichen", example = "['ZZ', 'YY']") List<String> referenceNumbers,
    @Nullable @Schema(description = "Normgeber") String legislationAuthority,
    @Nullable @Schema(description = "Gültig ab Datum", example = "2003-12-15")
        LocalDate entryIntoForceDate)
    implements AbstractDocumentSchema, JsonldResource {

  @Override
  @Schema(example = "AdministrativeDirective")
  public String getType() {
    return "AdministrativeDirective";
  }
}
