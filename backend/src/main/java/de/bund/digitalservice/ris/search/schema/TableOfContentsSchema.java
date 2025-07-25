package de.bund.digitalservice.ris.search.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldType;
import java.util.List;

@JsonldType("TocEntry")
public record TableOfContentsSchema(
    @Schema(example = "hauptteil-1_para-1") String id,
    @Schema(example = "1") String marker,
    @Schema(example = "Art 1") String heading,
    @Schema(example = "Art 1") String articleRangeStart,
    @Schema(example = "Art 5") String articleRangeEnd,
    List<TableOfContentsSchema> children) {}
