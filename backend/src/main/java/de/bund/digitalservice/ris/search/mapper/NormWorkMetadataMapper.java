package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.models.ldml.norm.NormWorkMetadata;
import jakarta.xml.bind.DataBindingException;
import jakarta.xml.bind.JAXB;
import java.io.StringReader;
import java.util.Optional;
import javax.xml.transform.stream.StreamSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.exceptions.DescriptorException;

/**
 * Extracts data from a norm work's {@code metadaten.xml} file, a work-level document (distinct from
 * the per-expression LDML documents).
 */
public class NormWorkMetadataMapper {

  private NormWorkMetadataMapper() {}

  private static final Logger logger = LogManager.getLogger(NormWorkMetadataMapper.class);

  public static final String NAMESPACE =
      "http://rechtsinformationen.bund.de/schema/norm-work-metadata/0.1";

  /**
   * Parses a String into a NormWorkMetadata.
   *
   * @param xmlContent the String to parse
   * @return a parsed NormWorkMetadata
   */
  public static Optional<NormWorkMetadata> parse(String xmlContent) {
    try {
      var source = new StreamSource(new StringReader(xmlContent));
      return Optional.of(JAXB.unmarshal(source, NormWorkMetadata.class));
    } catch (DescriptorException | DataBindingException e) {
      logger.error("Error parsing norm work metadata.", e);
      return Optional.empty();
    }
  }
}
