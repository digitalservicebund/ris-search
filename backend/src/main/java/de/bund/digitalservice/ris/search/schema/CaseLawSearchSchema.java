package de.bund.digitalservice.ris.search.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldId;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldResource;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import lombok.Builder;

/** A DTO for court decisions in a specific encoding, following schema.org naming guidelines. */
@Builder
@JsonldResource
@JsonldType("Decision")
public record CaseLawSearchSchema(
    @Schema(example = "KARE000000000") String documentNumber,
    @Schema(example = "ECLI:DE:FGRLP:1969:0905.IV85.68.0A") String ecli,
    @Schema(example = "Überschrift") String headline,
    @Schema(example = "Sonstiger Langtext") String otherLongText,
    LocalDate decisionDate,
    @Schema(example = "BGH 123/23") List<String> fileNumbers,
    @Schema(example = "FG") String courtType,
    @Schema(example = "Berlin") String location,
    @Schema(example = "Urteil") String documentType,
    @Schema(example = "Leitsatz") String outline,
    @Schema(example = "Gericht") String judicialBody,
    @Schema(example = "LArbG Hamm") String courtName, // corresponds to courtKeyword
    @Schema(examples = {"Beispielentscheidung"}) List<String> decisionName,
    @Schema(example = "DEV-123") List<String> deviatingDocumentNumber,
    // fields that aren't shared with CaseLawDocumentationUnit
    @Schema(example = "/v1/case-law/ECLI:DE:FGRLP:1969:0905.IV85.68.0A") @JsonldId String id,
    @Schema(example = "de") String inLanguage,
    Map<String, List<String>> highlightedFields)
    implements AbstractDocumentSchema {}
