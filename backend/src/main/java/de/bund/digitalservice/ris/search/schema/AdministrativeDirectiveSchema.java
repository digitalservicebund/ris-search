package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

/**
 * A data transfer object representing an administrative directive, designed according to schema.org
 * conventions and annotated for JSON-LD compatibility.
 *
 * <p>This class encapsulates metadata and attributes describing an administrative directive. It
 * includes fields such as document number, headline, document type, and references to related
 * legislation or norms. Additional information about the directive's applicability, such as valid
 * dates or issuing authorities, is also contained here.
 *
 * <p>The use of JSON-LD annotations ensures compatibility with linked data structures.
 */
@Builder
@JsonldType("AdministrativeDirective")
public record AdministrativeDirectiveSchema(
    @JsonProperty("@type") String type,
    @Schema(example = "KALU000000000") @JsonProperty("@id") String id,
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
    @Nullable List<AdministrativeDirectiveEncodingSchema> encoding) {

  public AdministrativeDirectiveSchema {
    type = "AdministrativeDirective";
  }
}
