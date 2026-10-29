package de.bund.digitalservice.ris.search.xsd;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuration properties (prefix {@code xsd}) for locating the XSD schema files to parse. */
@Setter
@Getter
@ConfigurationProperties(prefix = "xsd")
public class XSDDescriptionProperties {
  private Map<String, String[]> xsdLocations = new HashMap<>();
}
