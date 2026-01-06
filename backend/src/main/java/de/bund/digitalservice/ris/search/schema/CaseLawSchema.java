package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

/** A DTO for court decisions in a specific encoding, following schema.org naming guidelines. */
@Builder
public record CaseLawSchema(
    @Schema(example = "KARE000000000") String documentNumber,
    @Schema(
            example = "ECLI:DE:FGRLP:1969:0905.IV85.68.0A",
            description = "European Case Law Identifier")
        String ecli,
    @Schema(description = "Tatbestand") String caseFacts,
    @Schema(description = "Entscheidungsgründe") String decisionGrounds,
    @Schema(description = "Abweichende Meinung") String dissentingOpinion,
    @Schema(description = "Gründe") String grounds,
    @Schema(description = "Leitsatz") String guidingPrinciple,
    @Schema(description = "Überschrift") String headline,
    @Schema(description = "Orientierungssatz") String headnote,
    @Schema(description = "Sonstiger Orientierungssatz") String otherHeadnote,
    @Schema(description = "Sonstiger Langtext") String otherLongText,
    @Schema(description = "Tenor") String tenor,
    @Schema(description = "Entscheidungsdatum") LocalDate decisionDate,
    @Schema(example = "BGH 123/23", description = "Aktenzeichen") List<String> fileNumbers,
    @Schema(example = "FG", description = "Gerichtstyp") String courtType,
    @Schema(example = "Berlin", description = "Gerichtssitz") String location,
    @Schema(example = "Urteil") String documentType,
    @Schema(description = "Leitsatz") String outline,
    @Schema(example = "Gericht", description = "Spruchkörper") String judicialBody,
    @Schema(example = "3. Kammer", description = "Schlagworte") List<String> keywords,
    @Schema(example = "LArbG Hamm") String courtName, // corresponds to courtKeyword
    @Schema(
            examples = {"Beispielentscheidung"},
            description = "Entscheidungsname")
        List<String> decisionName,
    @Schema(example = "DEV-123", description = "Abweichende Dokumentnummer")
        List<String> deviatingDocumentNumber,
    // fields that aren't shared with CaseLawDocumentationUnit
    @Schema(example = "/v1/case-law/ECLI:DE:FGRLP:1969:0905.IV85.68.0A") @JsonProperty("@id")
        String id,
    @Schema(example = "de") String inLanguage,
    @Nullable List<CaseLawEncodingSchema> encoding,
    Map<String, List<String>> highlightedFields)
    implements JsonldResource {

  @Override
  @Schema(example = "Decision")
  public String getType() {
    return "Decision";
  }
}
