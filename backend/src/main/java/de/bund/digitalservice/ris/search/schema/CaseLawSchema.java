package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.xsd.RISSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

/** A DTO for court decisions in a specific encoding, following schema.org naming guidelines. */
@Builder
public record CaseLawSchema(
    @RISSchema(name = "ris:dokumentnummer", example = "KARE000000000", requiredMode = Schema.RequiredMode.REQUIRED)
        String documentNumber,
    @Schema(
            example = "ECLI:DE:FGRLP:1969:0905.IV85.68.0A",
            description = "European Case Law Identifier",
            requiredMode = Schema.RequiredMode.REQUIRED)
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
    @RISSchema(name = "ris:entscheidungsdatum", description = "Entscheidungsdatum", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate decisionDate,
    @RISSchema(
            name = "ris:aktenzeichenListe",
            example = "BGH 123/23",
            description = "Aktenzeichen",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> fileNumbers,
    @RISSchema(name = "ris:gerichtstyp", example = "FG", description = "Gerichtstyp") String courtType,
    @RISSchema(name = "ris:gerichtsort", example = "Berlin", description = "Gerichtssitz") String location,
    @RISSchema(example = "Urteil", name = "ris:dokumenttyp") String documentType,
    @Schema(description = "Leitsatz") String outline,
    @RISSchema(name = "ris:spruchkoerper", example = "1. Senat", description = "Spruchkörper") String judicialBody,
    @RISSchema(
            name = "",
            example = "Kündigung",
            description = "Schlagworte",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> keywords,
    @RISSchema(name = "ris:gericht", example = "LArbG Hamm") String courtName, // corresponds to courtKeyword
    @Schema(
            examples = {"Beispielentscheidung"},
            description = "Entscheidungsname",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> decisionName,
    @RISSchema(
            name = "ris:abweichendeDokumentnummer",
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
  @Schema(example = JsonldTypes.DECISION)
  public String getType() {
    return JsonldTypes.DECISION;
  }
}
