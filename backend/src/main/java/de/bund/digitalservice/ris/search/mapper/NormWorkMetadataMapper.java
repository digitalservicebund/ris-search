package de.bund.digitalservice.ris.search.mapper;

import de.bund.digitalservice.ris.search.utils.XmlDocument;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Extracts data from a norm work's {@code metadaten.xml} file, a work-level document (distinct from
 * the per-expression LDML documents).
 */
public class NormWorkMetadataMapper {
  private NormWorkMetadataMapper() {}

  private static final Logger logger = LogManager.getLogger(NormWorkMetadataMapper.class);

  public static final String NAMESPACE =
      "http://rechtsinformationen.bund.de/schema/norm-work-metadata/0.1";

  private static final String X_PATH_VOLLZITAT = "//*[local-name()='vollzitat']/text()";

  /**
   * Parses the full citation ({@code vollzitat}) out of a work's {@code metadaten.xml} content.
   *
   * @param xmlContent the raw XML content of the metadaten.xml file
   * @return the non-blank citation text, or {@link Optional#empty()} if it's missing, blank, or the
   *     content couldn't be parsed as XML
   */
  public static Optional<String> parseVollzitat(String xmlContent) {
    try {
      var xmlDocument = new XmlDocument(xmlContent.getBytes(StandardCharsets.UTF_8));
      return xmlDocument.getNonEmptyElementByXpath(X_PATH_VOLLZITAT);
    } catch (ParserConfigurationException | IOException | SAXException e) {
      logger.error("Error parsing norm work metadata.", e);
      return Optional.empty();
    }
  }
}
