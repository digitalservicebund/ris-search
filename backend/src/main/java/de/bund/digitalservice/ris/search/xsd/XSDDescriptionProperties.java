package de.bund.digitalservice.ris.search.xsd;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "xsd")
public class XSDDescriptionProperties {
  private String schemaPrefix;
  private Map<String, String[]> xsdLocations = new HashMap<>();
}

