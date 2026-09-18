package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.bund.digitalservice.ris.search.models.DocumentKind;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a <a href="https://schema.org/Dataset">schema.org/Dataset</a> object.
 *
 * <p>This record captures metadata about a specific dataset within the catalog, including its name,
 * description, modification timestamp, and its downloadable asset distribution.
 */
@JsonIgnoreProperties(
    value = {"@type"},
    allowGetters = true)
@Schema(description = "Represents <a href=\"https://schema.org/Dataset\">schema.org/Dataset</a>.")
public record ZipDataSetSchema(
    @Schema(description = "The name of the dataset.", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
    @Schema(
            description = "A short summary describing the contents of the dataset.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String description,
    @Schema(description = "The downloadable form of this dataset.")
        @JsonInclude(JsonInclude.Include.ALWAYS)
        ZipDataDownloadSchema distribution)
    implements JsonldResource {

  @Override
  @Schema(example = JsonldTypes.DATASET)
  public String getType() {
    return JsonldTypes.DATASET;
  }

  /** Constructor for admin */
  public static ZipDataSetSchema admin(String url) {
    return new ZipDataSetSchema(
        DocumentKind.ADMINISTRATIVE_DIRECTIVE.getBulkZipPath(),
        "Snapshot of all available administrative directive documents.",
        new ZipDataDownloadSchema(url));
  }

  /** Constructor for caseLaw */
  public static ZipDataSetSchema caseLaw(String url) {
    return new ZipDataSetSchema(
        DocumentKind.CASE_LAW.getBulkZipPath(),
        "Snapshot of all available case law documents.",
        new ZipDataDownloadSchema(url));
  }

  /** Constructor for legislation */
  public static ZipDataSetSchema legislation(String url) {
    return new ZipDataSetSchema(
        DocumentKind.LEGISLATION.getBulkZipPath(),
        "Snapshot of all available legislation documents.",
        new ZipDataDownloadSchema(url));
  }

  /** Constructor for literature */
  public static ZipDataSetSchema literature(String url) {
    return new ZipDataSetSchema(
        DocumentKind.LITERATURE.getBulkZipPath(),
        "Snapshot of all available literature documents.",
        new ZipDataDownloadSchema(url));
  }
}
