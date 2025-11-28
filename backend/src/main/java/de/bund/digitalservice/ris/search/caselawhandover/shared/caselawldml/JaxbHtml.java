package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import de.bund.digitalservice.ris.search.caselawhandover.shared.XmlUtils;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlMixed;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Objects;
import javax.xml.transform.Templates;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Represents an HTML element in JAXB format. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class JaxbHtml {

  private static final Logger logger = LogManager.getLogger(JaxbHtml.class);

  private static final Templates removeNameSpacesXslt =
      XmlUtils.getTemplates("caselawhandover/shared/removeNameSpaces.xslt");

  @XmlAttribute(name = "name")
  private String name;

  @XmlAnyElement @XmlMixed private List<Object> html;

  /**
   * Builds a JaxbHtml object from a list of HTML content.
   *
   * @param html the list of HTML content
   * @return a JaxbHtml object or null if the input is null, empty, or contains only null elements
   */
  public static JaxbHtml build(List<Object> html) {
    if (html == null || html.isEmpty() || html.stream().allMatch(Objects::isNull)) {
      return null;
    }

    return new JaxbHtml(html);
  }

  public JaxbHtml(List<Object> html) {
    this.html = html;
  }

  public String toHtmlString() {
    return XmlUtils.applyXsltToNodeList(removeNameSpacesXslt, XmlUtils.jaxbParseToNodeList(html));
  }
}
