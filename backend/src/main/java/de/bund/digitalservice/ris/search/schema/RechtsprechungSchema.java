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
    @Schema(description = "Kurztitel") String kurztitel, // corresponds to headline
    @Schema(description = "Titelzeile") String titelzeile,
    @Schema(description = "Orientierungssatz") String orientierungssatz,
    @Schema(description = "Sonstiger Orientierungssatz") String sonstigerOrientierungssatz,
    @Schema(description = "Sonstiger Langtext") String sonstigerLangtext,
    @Schema(description = "Rechtsfrage (gesamt)") String rechtsfrageGesamt,
    @Schema(description = "Rechtsfrage") String rechtsfrage,
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
    @Schema(example = "1 BvR 839, 899/96", description = "Abweichende Aktenzeichen")
        List<String> abweichendeAktenzeichen,
    @Schema(description = "Abweichende ECLIs") List<String> abweichendeEclis,
    @Schema(description = "Berufsbilder") List<String> berufsbilder,
    @Schema(description = "Kündigungsarten") List<String> kuendigungsarten,
    @Schema(description = "Herkunftsländer") List<String> herkunftslaender,
    @Schema(description = "Regionen") List<String> regionen,
    @Schema(description = "Tarifverträge") List<String> tarifvertraege,
    @Schema(description = "Kündigungsgründe") List<String> kuendigungsgruende,
    @Schema(description = "Mitwirkende Richter") List<String> mitwirkendeRichter,
    @Schema(description = "Vorgehende Entscheidungen") List<String> vorgehendeEntscheidungen,
    @Schema(description = "Nachgehende Entscheidungen") List<String> nachgehendeEntscheidungen,
    @Schema(description = "Aktivzitierung Literatur Unselbstständig")
        List<String> aktivzitierungLiteraturUnselbstaendig,
    @Schema(description = "Passivzitierung Literatur Unselbstständig")
        List<String> passivzitierungLiteraturUnselbstaendig,
    @Schema(description = "Aktivzitierung Literatur Selbstständig")
        List<String> aktivzitierungLiteraturSelbstaendig,
    @Schema(description = "Passivzitierung Literatur Selbstständig")
        List<String> passivzitierungLiteraturSelbstaendig,
    @Schema(description = "Aktivzitierung Rechtsprechung")
        List<String> aktivzitierungRechtsprechung,
    @Schema(description = "Passivzitierung Rechtsprechung")
        List<String> passivzitierungRechtsprechung,
    @Schema(description = "Aktivzitierung Verwaltungsvorschriften")
        List<String> aktivzitierungVerwaltungsvorschriften,
    @Schema(description = "Passivzitierung Verwaltungsvorschriften")
        List<String> passivzitierungVerwaltungsvorschriften,
    @Schema(example = "BGHSt 67, 273-284", description = "Amtliche Fundstellen")
        List<String> amtlicheFundstellen,
    @Schema(example = "DStR 2023, 1430-1435", description = "Nichtamtliche Fundstellen")
        List<String> nichtamtlicheFundstellen,
    @Schema(example = "vereinbar mit höherrangigem Recht (Bremen)", description = "Gesetzeskraft")
        List<String> gesetzeskraft,
    @Schema(example = "BGB § 823", description = "Normenkette") List<String> normenkette,
    @Schema(description = "Sachgebiete") List<String> sachgebiete,
    @Schema(description = "Streitjahre") List<String> streitjahre,
    @Schema(description = "Fehlerhafte Gerichte") List<String> fehlerhafteGerichte,
    @Schema(description = "Daten der mündlichen Verhandlung")
        List<LocalDate> datenDerMuendlichenVerhandlung,
    @Schema(description = "Definitionen") List<String> definitionen,
    @Schema(example = "Ja", description = "Erledigung") String erledigung,
    @Schema(example = "Ja", description = "Rechtskraft") String rechtskraft,
    @Schema(example = "Ja", description = "Gesetzgebungsauftrag") String gesetzgebungsauftrag,
    @Schema(description = "Langtextdatum") LocalDate langtextdatum,
    @Schema(description = "Rechtsmittelführer") String rechtsmittelfuehrer,
    @Schema(description = "Rechtsmittelzulassung") String rechtsmittelzulassung,
    @Schema(example = "Ja", description = "Revision") String revision,
    @Schema(description = "Letzte Veröffentlichung") LocalDate letzteVeroeffentlichung,
    @Schema(description = "Erledigungsvermerk") String erledigungsvermerk,
    @Schema(description = "Erstveröffentlichung") LocalDate erstveroeffentlichung,
    @Schema(description = "Mitteilungsdatum") LocalDate mitteilungsdatum,
    @Schema(example = "FG Berlin", description = "Gericht") String gericht,
    @Schema(description = "Gerichtsbarkeit") String gerichtsbarkeit,
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
