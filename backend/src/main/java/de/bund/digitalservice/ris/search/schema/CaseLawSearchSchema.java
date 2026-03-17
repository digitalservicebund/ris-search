package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.Builder;

/** A DTO for court decisions in a specific encoding, following schema.org naming guidelines. */
@Builder
public record CaseLawSearchSchema(
    @Schema(example = "KARE000000000", requiredMode = Schema.RequiredMode.REQUIRED)
        String documentNumber,
    @Schema(
            example = "ECLI:DE:FGRLP:1969:0905.IV85.68.0A",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String ecli,
    @Schema(example = "Überschrift") String headline,
    @Schema(example = "Sonstiger Langtext") String otherLongText,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) LocalDate decisionDate,
    @Schema(example = "BGH 123/23", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> fileNumbers,
    @Schema(example = "FG") String courtType,
    @Schema(example = "Berlin") String location,
    @Schema(example = "Urteil") String documentType,
    @Schema(example = "Leitsatz") String outline,
    @Schema(example = "Gericht") String judicialBody,
    @Schema(example = "LArbG Hamm") String courtName, // corresponds to courtKeyword
    @Schema(
            examples = {"Beispielentscheidung"},
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> decisionName,
    @Schema(example = "DEV-123", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> deviatingDocumentNumber,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<CaseLawEncodingSchema> encoding,
    // fields that aren't shared with CaseLawDocumentationUnit
    @Schema(
            example = "/v1/case-law/ECLI:DE:FGRLP:1969:0905.IV85.68.0A",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("@id")
        String id,
    @Schema(example = "de", requiredMode = Schema.RequiredMode.REQUIRED) String inLanguage,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        Map<String, List<String>> highlightedFields)
    implements AbstractDocumentSchema, JsonldResource {

  @Override
  @Schema(example = "Decision")
  public String getType() {
    return "Decision";
  }
}
