package de.bund.digitalservice.ris.search.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldId;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import java.util.List;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

@Builder
@JsonldResource
@JsonldType("Literature")
public record LiteratureSchema(
    @Schema(example = "KALU000000000") @JsonldId String id,
    @Schema(example = "de") String inLanguage,
    @Schema(description = "Dokumentnummer", example = "KALU000000000") String documentNumber,
    @Schema(description = "Veröffentlichungsjahre", example = "[2014, 2024-09]")
        List<String> yearsOfPublication,
    @Schema(description = "Dokumenttypen", example = "['Auf']") List<String> documentTypes,
    @Schema(description = "Unselbstständige Fundstellen", example = "['BUV, 1982, 123-123']")
        List<String> dependentReferences,
    @Schema(
            description = "Selbstständige Fundstellen",
            example = "['50 Jahre Betriebs-Berater, 1987, 123-456']")
        List<String> independentReferences,
    @Schema(description = "Norm Verweise", example = "['GG, Art 6 Abs 2 S 1, 1949-05-23']")
        List<String> normReferences,
    @Schema(description = "Haupttitel") String headline,
    @Schema(description = "Zusätze zum Haupttitel") String headlineAdditions,
    @Schema(description = "Dokumentarischer Titel") String alternativeHeadline,
    @Schema(description = "Authoren", example = "['Musterfrau, Sabine']") List<String> authors,
    @Schema(description = "Mitarbeiter", example = "['Mustermann, Max']")
        List<String> collaborators,
    @Schema(description = "Sprachen", example = "['deu', 'eng']") List<String> languages,
    @Schema(description = "Urheber", example = "['DGB']") List<String> originators,
    @Schema(
            description = "Kongressvermerke",
            example = "['Nationaler Beispiel Kongress, 2024, Berlin, GER']")
        List<String> conferenceNotes,
    @Schema(description = "Kurzreferat") String shortReport,
    @Schema(description = "Gliederung") String outline,
    @Nullable List<LiteratureEncodingSchema> encoding) {}
