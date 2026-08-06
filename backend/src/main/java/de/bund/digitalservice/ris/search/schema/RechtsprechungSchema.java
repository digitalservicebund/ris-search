package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

/** API schema representing a Rechtsprechung (case law) resource in JSON-LD format. */
@Builder
public record RechtsprechungSchema(
    @Schema(example = "KARE000000000", requiredMode = Schema.RequiredMode.REQUIRED)
        String dokumentNummer,
    @Schema(
            example = "ECLI:DE:FGRLP:1969:0905.IV85.68.0A",
            description = "European Case Law Identifier",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String ecli,
    @Schema(description = "CELEX-Nummer") String celex,
    @Schema(description = "Tatbestand") String tatbestand,
    @Schema(description = "Entscheidungsgründe") String entscheidungsgruende,
    @Schema(description = "Abweichende Meinung") String abweichendeMeinung,
    @Schema(description = "Gründe") String gruende,
    @Schema(description = "Leitsatz") String leitsatz,
    @Schema(description = "Überschrift") String headline,
    @Schema(description = "Titelzeile") String titelzeile,
    @Schema(description = "Orientierungssatz") String orientierungssatz,
    @Schema(description = "Sonstiger Orientierungssatz") String sonstigerOrientierungssatz,
    @Schema(description = "Sonstiger Langtext") String sonstigerLangtext,
    @Schema(description = "Tenor") String tenor,
    @Schema(description = "Datum", requiredMode = Schema.RequiredMode.REQUIRED) LocalDate datum,
    @Schema(description = "Abweichende Daten") List<LocalDate> abweichendeDaten,
    @Schema(description = "Gliederung") String gliederung,
    @Schema(description = "Aktenzeichen") String aktenzeichen,
    @Schema(
            example = "BGH 123/23",
            description = "Aktenzeichenliste",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> aktenzeichenListe,
    @Schema(description = "Abweichende ECLIs") List<String> abweichendeEclis,
    @Schema(description = "Berufsbilder") List<String> berufsbilder,
    @Schema(description = "Daten der mündlichen Verhandlung")
        List<LocalDate> datenDerMuendlichenVerhandlung,
    @Schema(description = "Definitionen") List<String> definitionen,
    @Schema(example = "Ja", description = "Erledigung") String erledigung,
    @Schema(description = "Erledigungsvermerk") String erledigungsvermerk,
    @Schema(example = "FG Berlin", description = "Gericht") String gericht,
    @Schema(example = "Urteil") String dokumenttyp,
    @Schema(example = "Gericht", description = "Spruchkörper") String spruchkoerper,
    @Schema(
            example = "3. Kammer",
            description = "Schlagworte",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> schlagwoerter,
    @Schema(example = "LArbG Hamm") String courtName, // corresponds to courtKeyword
    @Schema(
            examples = {"Beispielentscheidung"},
            description = "Entscheidungsnamen",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> entscheidungsnamen,
    @Schema(
            example = "DEV-123",
            description = "Abweichende Dokumentnummer",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> abweichendeDokumentnummern,
    @Schema(
            example = "/v1/case-law/ECLI:DE:FGRLP:1969:0905.IV85.68.0A",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("@id")
        String id,
    @Schema(example = "de", requiredMode = Schema.RequiredMode.REQUIRED) String inLanguage,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<CaseLawEncodingSchema> encoding,
    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Whether or not the document is a Vorabdokument")
        boolean vorabdokument)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.RECHTSPRECHUNG)
  public String getType() {
    return JsonldTypes.RECHTSPRECHUNG;
  }
}
