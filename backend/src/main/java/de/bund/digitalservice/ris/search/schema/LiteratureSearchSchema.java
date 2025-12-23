package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import org.jetbrains.annotations.Nullable;

/**
 * A record representing the schema for literature documents, used in document search contexts. The
 * schema contains metadata and detailed information about literary works, including identifiers,
 * publication details, authorship, language attributes, and document references.
 *
 * <p>The record implements the AbstractDocumentSchema interface, allowing for consistent handling
 * within a polymorphic document search system.
 *
 * <p>Key properties include: - A unique identifier and document number. - Metadata about
 * publication, including years, dates, and language attributes. - Lists of document types and
 * references, distinguishing between dependent and independent ones. - Titles and alternative
 * titles, with support for additions to the headline. - Authorship details, including authors,
 * collaborators, and originators. - Information about conferences and notes related to the
 * literature. - Encoding information for representations of the document in various formats.
 */
@Builder
public record LiteratureSearchSchema(
    @JsonProperty("@type") String type,
    @Schema(example = "KALU000000000") @JsonProperty("@id") String id,
    @Schema(example = "de") String inLanguage,
    @Schema(description = "Dokumentnummer", example = "KALU000000000") String documentNumber,
    @Schema(description = "Veröffentlichungsjahre", example = "[2014, 2024-09]")
        List<String> yearsOfPublication,
    @Schema(description = "Erstes Veröffentlichungsdatum", example = "2014-01-01")
        LocalDate firstPublicationDate,
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
    @Schema(description = "Authoren", example = "['Musterfrau, Sabine']") List<String> authors,
    @Schema(description = "Mitarbeiter", example = "['Mustermann, Max']")
        List<String> collaborators,
    @Schema(description = "Sprachen", example = "['deu', 'eng']") List<String> languages,
    @Schema(description = "Urheber", example = "['DGB']") List<String> originators,
    @Schema(
            description = "Kongressvermerke",
            example = "['Nationaler Beispiel Kongress, 2024, Berlin, GER']")
        List<String> conferenceNotes,
    @Schema(description = "Kurzreferat") String shortReport,
    @Schema(description = "Gliederung") String outline,
    @Schema(description = "Literaturtyp", example = "['sli', 'uli']") String literatureType,
    @Nullable List<LiteratureEncodingSchema> encoding)
    implements AbstractDocumentSchema {

  public static class LiteratureSearchSchemaBuilder {
    LiteratureSearchSchemaBuilder() {
      type = "Literature";
    }
  }
}
