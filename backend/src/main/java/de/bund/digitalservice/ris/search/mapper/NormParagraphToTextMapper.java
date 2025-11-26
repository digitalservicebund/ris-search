package de.bund.digitalservice.ris.search.mapper;

import static de.bund.digitalservice.ris.search.utils.MappingUtils.cleanText;

import de.bund.digitalservice.ris.search.utils.XmlDocument;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;

/**
 * The {@code NormParagraphToTextMapper} class provides functionality to process and extract
 * formatted text content from XML paragraph nodes. This includes extracting paragraph numbers and
 * cleaning their associated text content.
 *
 * <p>This class is designed for utility and is not meant to be instantiated.
 */
public class NormParagraphToTextMapper {
  private static final String X_PATH_ARTICLE_PARAGRAPHS = ".//*[local-name()='paragraph']";
  private static final String X_PATH_PARAGRAPH_NUM = ".//*[local-name()='num']";
  private static final String X_PATH_PARAGRAPH_SENTENCE_MARKER =
      ".//*[local-name()='marker' and @refersTo='satzende']";

  private NormParagraphToTextMapper() {}

  private static String extractParagraphNumber(XmlDocument paragraphXml) {
    return paragraphXml
        .getFirstMatchedNodeByXpath(X_PATH_PARAGRAPH_NUM)
        .map(Node::getTextContent)
        .orElse("");
  }

  private static String extractParagraphTextContent(XmlDocument paragraphXml) {
    paragraphXml.removeNodesByXpath(X_PATH_PARAGRAPH_NUM);
    paragraphXml.replaceNodesByXpathWithString(X_PATH_PARAGRAPH_SENTENCE_MARKER, " ");
    return paragraphXml
        .getFirstMatchedNodeByXpath(X_PATH_ARTICLE_PARAGRAPHS)
        .map(Node::getTextContent)
        .orElse("");
  }

  /**
   * Extracts and formats text from a given paragraph node. The method processes the paragraph node
   * to retrieve its number (if available) and its text content, and then combines the two into a
   * formatted string. If the paragraph number is absent, it returns the cleaned text content alone.
   *
   * @param normParagraph the XML node representing the paragraph to be processed
   * @return a formatted string containing the paragraph number and cleaned text content, or just
   *     the cleaned text content if no number is present
   * @throws ParserConfigurationException if an error occurs while processing the XML document
   */
  public static String extractTextFromParagraph(Node normParagraph)
      throws ParserConfigurationException {

    XmlDocument paragraphXml = new XmlDocument(normParagraph);

    String paragraphNumber = extractParagraphNumber(paragraphXml);
    String paragraphTextContent = extractParagraphTextContent(paragraphXml);

    if (paragraphNumber.isEmpty()) {
      return cleanText(paragraphTextContent);
    }

    return String.format("%s %s", paragraphNumber, cleanText(paragraphTextContent));
  }
}
