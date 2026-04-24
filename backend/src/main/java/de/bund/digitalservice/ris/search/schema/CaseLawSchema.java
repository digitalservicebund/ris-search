package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

/** A DTO for court decisions in a specific encoding, following schema.org naming guidelines. */
@Builder
public record CaseLawSchema(
    @Schema(example = "KARE000000000", requiredMode = Schema.RequiredMode.REQUIRED)
        String documentNumber,
    @Schema(
            example = "ECLI:DE:FGRLP:1969:0905.IV85.68.0A",
            description = "European Case Law Identifier",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String ecli,
    @Schema(description = "Leitsatz") String guidingPrinciple,
    @Schema(description = "Überschrift") String headline,
    @Schema(description = "Orientierungssatz") String headnote,
    @Schema(description = "Sonstiger Orientierungssatz") String otherHeadnote,
    @Schema(description = "Entscheidungsdatum", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate decisionDate,
    @Schema(
            example = "BGH 123/23",
            description = "Aktenzeichen",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> fileNumbers,
    @Schema(example = "FG", description = "Gerichtstyp") String courtType,
    @Schema(example = "Berlin", description = "Gerichtssitz") String location,
    @Schema(example = "Urteil") String documentType,
    @Schema(example = "Gericht", description = "Spruchkörper") String judicialBody,
    @Schema(
            example = "3. Kammer",
            description = "Schlagworte",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> keywords,
    @Schema(example = "LArbG Hamm") String courtName, // corresponds to courtKeyword
    @Schema(
            examples = {"Beispielentscheidung"},
            description = "Entscheidungsname",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> decisionName,
    @Schema(
            example = "DEV-123",
            description = "Abweichende Dokumentnummer",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> deviatingDocumentNumber,
    // fields that aren't shared with CaseLawDocumentationUnit
    @Schema(
            example = "/v1/case-law/ECLI:DE:FGRLP:1969:0905.IV85.68.0A",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("@id")
        String id,
    @Schema(example = "de", requiredMode = Schema.RequiredMode.REQUIRED) String inLanguage,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<CaseLawEncodingSchema> encoding)
    implements JsonldResource {

  @Override
  @Schema(example = "Decision")
  public String getType() {
    return "Decision";
  }
}
