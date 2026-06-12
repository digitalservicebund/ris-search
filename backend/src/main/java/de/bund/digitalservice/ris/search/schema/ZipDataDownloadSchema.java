package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a <a href="https://schema.org/DataDownload">schema.org/DataDownload</a> object.
 *
 * <p>This record captures a downloadable form of a dataset, specifying its media encoding format
 * and the direct download URL.
 */
@Schema(
    description =
        "Represents <a href=\"https://schema.org/DataDownload\">schema.org/DataDownload</a>.")
@JsonIgnoreProperties(
    value = {"@type"},
    allowGetters = true)
public record ZipDataDownloadSchema(
    @Schema(
            description = "Will always be application/zip.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String encodingFormat,
    @Schema(
            description = "The url to download the zip file.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String contentUrl)
    implements JsonldResource {
  @Override
  @Schema(example = JsonldTypes.DATA_DOWNLOAD)
  public String getType() {
    return JsonldTypes.DATA_DOWNLOAD;
  }

  public ZipDataDownloadSchema(String url) {
    this("application/zip", url);
  }
}
