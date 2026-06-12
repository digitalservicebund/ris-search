package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Represents a <a href="https://schema.org/DataCatalog">schema.org/DataCatalog</a> object.
 *
 * <p>This record captures a collection of zip datasets.
 */
@JsonIgnoreProperties(
    value = {"@context", "@type", "name"},
    allowGetters = true)
@Schema(
    description =
        "Represents a <a href=\"https://schema.org/DataCatalog\">schema.org/DataCatalog</a>.")
public record ZipDataCatalogSchema(
    @Schema(
            description = "The list of zip datasets contained in this catalog.",
            requiredMode = Schema.RequiredMode.REQUIRED)
        List<ZipDataSetSchema> dataSet) {

  @Schema(example = "https://schema.org/")
  @JsonProperty(value = "@context", index = 0)
  public String getContext() {
    return "https://schema.org/";
  }

  @Schema(example = JsonldTypes.DATA_CATALOG)
  @JsonProperty(value = "@type", index = 1)
  public String getType() {
    return JsonldTypes.DATA_CATALOG;
  }

  @Schema(
      description = "The name of this data catalog.",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty(index = 2)
  public String getName() {
    return "Zip snapshot data catalog.";
  }

  /** Constructor for ZipDataCatalogSchema */
  public ZipDataCatalogSchema(
      String adminUrl, String caseLawUrl, String legislationUrl, String literatureUrl) {
    this(
        List.of(
            ZipDataSetSchema.admin(adminUrl),
            ZipDataSetSchema.caseLaw(caseLawUrl),
            ZipDataSetSchema.legislation(legislationUrl),
            ZipDataSetSchema.literature(literatureUrl)));
  }
}
