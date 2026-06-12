package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Represents a <a href="https://schema.org/DataCatalog">schema.org/DataCatalog</a> object.
 *
 * <p>This record captures a collection of zip datasets.
 */
@JsonIgnoreProperties(
    value = {"@type"},
    allowGetters = true)
@Schema(
    description =
        "Represents a <a href=\"https://schema.org/DataCatalog\">schema.org/DataCatalog</a>.")
public record ZipDataCatalogSchema(
    @Schema(
            description = "The name of this data catalog.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
    @Schema(
            description = "The list of zip datasets contained in this catalog.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<ZipDataSetSchema> dataSets)
    implements JsonldResource {
  @Override
  @Schema(example = JsonldTypes.DATA_CATALOG)
  public String getType() {
    return JsonldTypes.DATA_CATALOG;
  }

  public ZipDataCatalogSchema(
      String adminUrl, String caseLawUrl, String legislationUrl, String literatureUrl) {
    this(
        "Zip snapshot data catalog.",
        List.of(
            ZipDataSetSchema.admin(adminUrl),
            ZipDataSetSchema.caseLaw(caseLawUrl),
            ZipDataSetSchema.legislation(legislationUrl),
            ZipDataSetSchema.literature(literatureUrl)));
  }
}
