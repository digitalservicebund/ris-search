package de.bund.digitalservice.ris.search.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldId;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import java.util.List;
import lombok.Builder;

@Builder
@JsonldResource
@JsonldType("Literature")
public record LiteratureSchema(
    @Schema(example = "KALU000000000") @JsonldId String id,
    @Schema(example = "de") String inLanguage,
    @Schema(description = "Dokumentnummer", example = "KALU000000000") String documentNumber,
    @Schema(description = "Veröffentlichungsjahre", example = "[2014, 2024-09]")
        List<String> yearsOfPublication,
    @Schema(description = "Dokumenttypen", example = "[Auf]") List<String> documentTypes,
    @Schema(description = "Unselbstständige Fundstellen", example = "[BUV, 1982, 123-123]")
        List<String> dependentReferences,
    @Schema(
            description = "Selbstständige Fundstellen",
            example = "[50 Jahre Betriebs-Berater, 1987, 123-456]")
        List<String> independentReferences,
    @Schema(description = "Haupttitel") String headline,
    @Schema(description = "Dokumentarischer Titel") String alternativeTitle,
    @Schema(description = "Authoren", example = "[Musterfrau, Sabine]") List<String> authors,
    @Schema(description = "Mitarbeiter", example = "[Mustermann, Max]") List<String> collaborators,
    @Schema(description = "Kurzrefarat") String shortReport,
    @Schema(description = "Gliederung") String outline) {}
