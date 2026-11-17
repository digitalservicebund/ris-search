package de.bund.digitalservice.ris.search.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Interface for schema files whose JsonLd node has more than one type */
public interface MultiTypedJsonldResource {
  @JsonProperty(value = "@type", index = 0)
  List<String> getType();
}
