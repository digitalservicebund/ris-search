package de.bund.digitalservice.ris.search.utils;

import de.bund.digitalservice.ris.search.caselawhandover.shared.XmlUtils;
import de.bund.digitalservice.ris.search.models.ldml.TimeInterval;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class is responsible for parsing and processing date-related information in XML documents
 * that conform to the LegalDocML.de schema.
 *
 * <p>The XML follows a hierarchical structure based on the Akoma Ntoso standard:
 *
 * <pre>
 * Structure Overview:
 * - <akn:akomaNtoso>
 *   └── <akn:act>
 *       ├── <akn:meta>
 *       │   ├── <akn:lifecycle>
 *       │   │   └── <akn:eventRef>: Defines key legal events with associated dates.
 *       │   └── <akn:temporalData>
 *       │       └── <akn:temporalGroup>: Groups time intervals for legal validity.
 *       │           └── <akn:timeInterval>: References <akn:eventRef> elements via @start/@end attributes.
 *       └── <akn:body>
 *           └── <akn:article>: References a <akn:temporalGroup> using the @period attribute to define its validity.
 * </pre>
 *
 * Functionality: This class provides methods to map temporal group identifiers to their
 * corresponding dates as defined in the <akn:lifecycle> section.
 */
public class LdmlTemporalData {

  private static final Logger logger = LogManager.getLogger(LdmlTemporalData.class);

  private static final String X_PATH_ALL_EVENT_REF =
      "/akn:akomaNtoso/akn:act/akn:meta/akn:lifecycle/akn:eventRef";

  private static final String X_PATH_ALL_TEMPORAL_GROUP =
      "/akn:akomaNtoso/akn:act/akn:meta/akn:temporalData/akn:temporalGroup";

  private LdmlTemporalData() {
    throw new IllegalStateException("Utility class");
  }

  public static Map<String, TimeInterval> getTemporalDataWithDatesMapping(XmlDocument xmlDocument) {
    Map<String, TimeInterval> temporalGroupToIntervalMap = new HashMap<>();
    Map<String, String> eventRefMap = getEventRefMap(xmlDocument);
    List<Node> temporalGroups;

    try {
      temporalGroups = XmlUtils.toList(xmlDocument.getNodesByXpath(X_PATH_ALL_TEMPORAL_GROUP));
    } catch (XPathExpressionException e) {
      logger.warn("Failed to evaluate XPath expression for temporal groups", e);
      return temporalGroupToIntervalMap;
    }

    for (Node temporalGroup : temporalGroups) {
      if (!(temporalGroup instanceof Element)) {
        continue;
      }

      String temporalGroupId = String.format("#%s", getAttributeValue(temporalGroup, "eId"));
      Node timeIntervalNode = getTimeIntervalNode((Element) temporalGroup);

      if (timeIntervalNode != null && timeIntervalNode instanceof Element) {
        Element timeInterval = (Element) timeIntervalNode;
        TimeInterval timeIntervalData = fetchDatesForTimeInterval(timeInterval, eventRefMap);
        temporalGroupToIntervalMap.put(temporalGroupId, timeIntervalData);
      }
    }
    return temporalGroupToIntervalMap;
  }

  private static TimeInterval fetchDatesForTimeInterval(
      Element timeIntervalNode, Map<String, String> eventRefMap) {
    String start = eventRefMap.get(buildXpathFromID(timeIntervalNode.getAttribute("start")));
    String end = eventRefMap.get(buildXpathFromID(timeIntervalNode.getAttribute("end")));
    return new TimeInterval(start, (end != null && !end.isEmpty()) ? end : null);
  }

  private static Node getTimeIntervalNode(Element temporalGroup) {
    try {
      XPath xpath = XPathFactory.newInstance().newXPath();
      return (Node)
          xpath.evaluate("./*[local-name()='timeInterval']", temporalGroup, XPathConstants.NODE);
    } catch (XPathExpressionException e) {
      logger.warn("Error evaluating XPath for timeInterval", e);
      return null;
    }
  }

  private static Map<String, String> getEventRefMap(XmlDocument xmlDocument) {
    try {
      return XmlUtils.toList(xmlDocument.getNodesByXpath(X_PATH_ALL_EVENT_REF)).stream()
          .map(e -> (Element) e)
          .collect(Collectors.toMap(e -> e.getAttribute("eId"), e -> e.getAttribute("date")));
    } catch (XPathExpressionException e) {
      logger.warn("Error parsing norm dates", e);
      return new HashMap<>();
    }
  }

  private static String getAttributeValue(Node node, String attributeName) {
    return Optional.ofNullable(node.getAttributes().getNamedItem(attributeName))
        .map(Node::getNodeValue)
        .orElse(null);
  }

  private static String buildXpathFromID(String id) {
    if (id == null || id.isEmpty()) {
      return "";
    }
    return id.replace("#", "");
  }
}
