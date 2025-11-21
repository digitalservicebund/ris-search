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
public record AdministrativeDirectiveSchema(
    @Schema(example = "KALU000000000") @JsonldId String id,
    @Schema(description = "Dokumentnummer", example = "KALU000000000") String documentNumber,
    @Schema(description = "Dokumenttyp", example = "VV") String documentType,
    @Schema(description = "Aktenzeichen", example = "ZZ") List<String> referenceNumbers) {}
