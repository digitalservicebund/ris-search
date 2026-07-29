package de.bund.digitalservice.ris.builder.models.common;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.body.BodyElement;
import de.bund.digitalservice.ris.builder.models.preface.DocStage;
import de.bund.digitalservice.ris.builder.models.preface.DocTitle;
import de.bund.digitalservice.ris.builder.models.preface.ShortTitle;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

/**
 * Represents an {@code akn:p} paragraph element, a generic text container used throughout the norm.
 */
@NoArgsConstructor
@XmlSeeAlso({DocStage.class, DocTitle.class, ShortTitle.class, AuthorialNote.class})
@XmlRootElement(name = "p", namespace = NormTestDataBuilder.AKN_NS)
public class AknP extends BaseElement implements BodyElement {

  @XmlAttribute private String eId = "text-n1";

  @XmlAnyElement private List<Object> children = new ArrayList<>();

  /**
   * Creates a paragraph element with the given text content.
   *
   * @param text the text content of this paragraph
   */
  public AknP(String text) {
    this.children = new ArrayList<>(List.of(text));
  }

  /**
   * Creates a paragraph element with the given parent eId and text content.
   *
   * @param parentEId the eId of the enclosing element, used to build this element's eId
   * @param text the text content of this paragraph
   */
  public AknP(String parentEId, String text) {
    this(text);
    this.eId = parentEId + "_" + eId;
  }

  /**
   * Creates a paragraph element with the given parent eId and mixed child elements.
   *
   * @param parentEId the eId of the enclosing element, used to build this element's eId
   * @param children the child elements or text nodes of this paragraph
   */
  public AknP(String parentEId, List<Object> children) {
    this.eId = parentEId + "_" + eId;
    this.children = new ArrayList<>(children);
  }

  /**
   * Adds a child element or text node to this paragraph.
   *
   * @param child the child to add, e.g. a {@link String} or another AKN element
   * @return this paragraph for chaining
   */
  public AknP addChild(Object child) {
    this.children.add(child);
    return this;
  }
}
