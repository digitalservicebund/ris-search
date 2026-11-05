package de.bund.digitalservice.ris.search.xsd;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties (prefix {@code xsd}) for locating the XSD schema file to parse.
 *
 * <p>The configured location is expected to be the root of the shared {@code ris} namespace (by
 * default {@code schema/ris/ris.xsd}), which itself includes every document-kind-specific schema
 * file in that namespace, so a single location is enough to reach the whole vocabulary.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "xsd")
public class XSDDescriptionProperties {
  private String risNamespaceLocation;
}
