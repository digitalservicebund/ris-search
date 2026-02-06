package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

/**
 * A data transfer object (DTO) representing the schema for literature records. This class serves as
 * a representation of literature documents with various associated metadata and conforms to
 * schema.org guidelines.
 *
 * <p>Key features of this schema include metadata about the document's language, publication
 * details, document types, references (dependent and independent), authorship, and other
 * descriptive fields.
 *
 * <p>Fields: - id: Unique identifier for the literature record. - inLanguage: Language of the
 * literature document. - documentNumber: Unique document number for the literature. -
 * yearsOfPublication: List of publication years or intervals relevant to the literature. -
 * documentTypes: List of types describing the nature of the document. - dependentReferences:
 * References to dependent citations linked to the literature. - independentReferences: References
 * to independent citations linked to the literature. - normReferences: Legal or regulatory
 * references mentioned in the literature. - headline: The main title of the literature. -
 * headlineAdditions: Additional information or context related to the main title. -
 * alternativeHeadline: Documented alternative or descriptive title for the literature. - authors:
 * List of authors responsible for the creation of the literature. - collaborators: List of
 * contributors or collaborators involved with the literature. - languages: List of languages
 * associated with the literature record (e.g., translations). - originators: List of entities
 * responsible for originating the document. - conferenceNotes: Information related to any
 * conferences or events where the literature was presented. - shortReport: A concise summary or
 * abstract of the literature. - outline: Information describing the structure or organization of
 * the document. - encoding: Optional list of encodings (e.g., formats or variants) for the
 * literature.
 */
@Builder
public record LiteratureSchema(
    @Schema(example = "KALU000000000") @JsonProperty("@id") String id,
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
    @Nullable @Schema(description = "Ausgabe") String edition,
    @Schema(description = "Bestellnummer") List<String> internationalIdentifiers,
    @Schema(description = "Autoren", example = "['Musterfrau, Sabine']") List<String> authors,
    @Schema(description = "Mitarbeiter", example = "['Mustermann, Max']")
        List<String> collaborators,
    @Schema(description = "Bearbeiter") List<String> editors,
    @Schema(description = "Begründer") List<String> founder,
    @Schema(description = "Herausgeber") List<String> publishers,
    @Schema(description = "Verlag") List<String> publishingHouses,
    @Schema(description = "Sprachen", example = "['deu', 'eng']") List<String> languages,
    @Schema(description = "Urheber", example = "['DGB']") List<String> originators,
    @Schema(
            description = "Kongressvermerke",
            example = "['Nationaler Beispiel Kongress, 2024, Berlin, GER']")
        List<String> conferenceNotes,
    @Schema(description = "Kurzreferat") String shortReport,
    @Schema(description = "Gliederung") String outline,
    @Schema(description = "Hochschulvermerk") List<String> universityNotes,
    @Schema(description = "Teilbaende") List<String> volumes,
    @Schema(description = "Literaturtyp", example = "['sli', 'uli']") String literatureType,
    @Nullable List<LiteratureEncodingSchema> encoding)
    implements JsonldResource {

  @Override
  @Schema(example = "Literature")
  public String getType() {
    return "Literature";
  }
}
