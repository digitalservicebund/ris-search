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
@JsonldType("AdministrativeDirective")
public record AdministrativeDirectiveSchema(
    @Schema(example = "KALU000000000") @JsonldId String id,
    @Schema(description = "Dokumentnummer", example = "KALU000000000") String documentNumber,
    @Nullable @Schema(description = "Haupttitel") String headline,
    @Nullable @Schema(description = "Kurzreferat") String shortReport,
    @Schema(description = "Dokumenttyp", example = "VV") String documentType,
    @Nullable @Schema(description = "Art der Verwaltungsvorschrift", example = "Bekanntmachung")
        String documentTypeDetail,
    @Schema(description = "Aktenzeichen", example = "['ZZ', 'YY']") List<String> referenceNumbers,
    @Nullable @Schema(description = "Gültig ab Datum", example = "2003-12-15")
        LocalDate entryIntoForceDate,
    @Nullable @Schema(description = "Gültig bis Datum", example = "2005-12-01")
        LocalDate expiryDate,
    @Nullable @Schema(description = "Normgeber") String legislationAuthority,
    @Schema(description = "Fundstelle") List<String> references,
    @Schema(description = "Zitierdaten", example = "") List<LocalDate> citationDates,
    @Schema(description = "Normkette", example = "['§ 1 Abs1 SGB']") List<String> normReferences,
    @Schema(description = "Gliederung") List<String> outline,
    @Nullable List<AdministrativeDirectiveEncodingSchema> encoding) {}
