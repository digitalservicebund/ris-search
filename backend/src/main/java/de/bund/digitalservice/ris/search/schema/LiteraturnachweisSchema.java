package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.bund.digitalservice.ris.search.xsd.RISSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

/**
 * API schema representing a Literaturnachweis (selbstständige or unselbstständige Literatur) in
 * JSON-LD format. Field names follow the elements of the literature LDML they are taken from.
 */
@Builder
public record LiteraturnachweisSchema(
    @JsonProperty("@context") @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String context,
    @Schema(
            example = "/v1/literaturnachweise/KALU000000000",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("@id")
        String id,
    @Schema(example = "de", requiredMode = Schema.RequiredMode.REQUIRED) String inLanguage,
    @RISSchema(
            name = "ris:dokumentnummer",
            example = "KALU000000000",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String dokumentnummer,
    @RISSchema(
            name = "ris:veroeffentlichungsJahre",
            example = "[\"2014\", \"2024-09\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> veroeffentlichungsJahre,
    @RISSchema(
            name = "ris:dokumenttypen",
            example = "[\"Auf\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> dokumenttypen,
    @RISSchema(
            name = "ris:fundstelle",
            example = "[\"BUV, 1982, 123-123\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> fundstellenUnselbstaendig,
    @RISSchema(
            name = "ris:fundstelleSelbststaendig",
            example = "[\"50 Jahre Betriebs-Berater, 1987, 123-456\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> fundstellenSelbstaendig,
    @RISSchema(
            name = "ris:referenzenNorm",
            example = "[\"GG, Art 6 Abs 2 S 1, 1949-05-23\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> referenzenNorm,
    @Schema(description = "Haupttitel") String haupttitel,
    @Schema(description = "Zusätze zum Haupttitel") String haupttitelzusatz,
    @Schema(description = "Dokumentarischer Titel") String dokumentarischerTitel,
    @Nullable @RISSchema(name = "ris:ausgabe") String ausgabe,
    @Schema(description = "Bestellnummer", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> bestellnummern,
    @RISSchema(
            name = "ris:verfasserListe",
            example = "[\"Musterfrau, Sabine\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> verfasser,
    @RISSchema(
            name = "ris:mitarbeiterListe",
            example = "[\"Mustermann, Max\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> mitarbeiter,
    @RISSchema(name = "ris:bearbeiterListe", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> bearbeiter,
    @RISSchema(name = "ris:begruenderListe", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> begruender,
    @RISSchema(name = "ris:herausgeberPersonen", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> herausgeberPersonen,
    @RISSchema(name = "ris:herausgeberInstitutionen", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> herausgeberInstitutionen,
    @RISSchema(name = "ris:verlagsangaben", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> verlagsangaben,
    @RISSchema(
            name = "ris:urheberListe",
            example = "[\"DGB\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> urheber,
    @RISSchema(
            name = "ris:kongressvermerke",
            example = "[\"Nationaler Beispiel Kongress, 2024, Berlin, GER\"]",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> kongressvermerke,
    @RISSchema(name = "ris:hochschulschriftenvermerke", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> hochschulschriftenvermerke,
    @RISSchema(name = "ris:teilbaende", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> teilbaende,
    @Schema(
            description = "Literaturtyp",
            example = "uli",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String literaturtyp,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<DocumentEncodingSchema> encoding)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.LITERATURNACHWEIS)
  public String getType() {
    return JsonldTypes.LITERATURNACHWEIS;
  }
}
