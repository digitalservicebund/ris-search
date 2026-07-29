package de.bund.digitalservice.ris.builder.models.common;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:inline} element, e.g. an official abbreviation marker. */
@NoArgsConstructor
@XmlRootElement(namespace = NormTestDataBuilder.AKN_NS)
public class Inline extends BaseElement {

  @XmlAttribute private String eId;

  @XmlAttribute private String name = "attributsemantik-noch-undefiniert";

  @XmlAttribute private String refersTo;

  @XmlAnyElement private String content;

  /**
   * Creates an inline element with the given eId, semantic reference, and text content.
   *
   * @param eId the element's eId
   * @param refersTo the semantic reference target, e.g. "amtliche-abkuerzung"
   * @param content the inline text content
   */
  public Inline(String eId, String refersTo, String content) {
    this.eId = eId;
    this.refersTo = refersTo;
    this.content = content;
  }
}
