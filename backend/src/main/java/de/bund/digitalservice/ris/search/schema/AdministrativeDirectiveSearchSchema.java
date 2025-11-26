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
public record AdministrativeDirectiveSearchSchema(
    @Schema(example = "KALU000000000") @JsonldId String id,
    @Schema(description = "Dokumentnummer", example = "KALU000000000") String documentNumber,
    @Nullable @Schema(description = "Haupttitel") String headline,
    @Schema(description = "Dokumenttyp", example = "VV") String documentType,
    @Schema(description = "Aktenzeichen", example = "['ZZ', 'YY']") List<String> referenceNumbers,
    @Nullable @Schema(description = "Gültig ab Datum", example = "2003-12-15")
        LocalDate entryIntoForceDate)
    implements AbstractDocumentSchema {}
