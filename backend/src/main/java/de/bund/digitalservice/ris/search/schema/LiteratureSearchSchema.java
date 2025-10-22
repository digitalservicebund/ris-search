package de.bund.digitalservice.ris.search.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldId;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

@Builder
@JsonldResource
@JsonldType("Literature")
public record LiteratureSearchSchema(
    @Schema(example = "KALU000000000") @JsonldId String id,
    @Schema(example = "de") String inLanguage,
    @Schema(description = "Dokumentnummer", example = "KALU000000000") String documentNumber,
    @Schema(
            example = "2003-12-15",
            description = "The date on which the literature piece was recorded.")
        LocalDate recordingDate,
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
    @Schema(description = "Dokumentarischer Titel") String alternativeHeadline,
    @Schema(description = "Authoren", example = "[Musterfrau, Sabine]") List<String> authors,
    @Schema(description = "Mitarbeiter", example = "[Mustermann, Max]") List<String> collaborators,
    @Schema(description = "Kurzrefarat") String shortReport,
    @Schema(description = "Gliederung") String outline,
    @Nullable List<LiteratureEncodingSchema> encoding)
    implements AbstractDocumentSchema {}
