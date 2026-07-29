package de.bund.digitalservice.ris.builder.models.preamble;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.AknP;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:formula} element, e.g. the norm's enacting formula (Eingangsformel). */
@NoArgsConstructor
public class Formula extends BaseElement {

  @XmlAttribute private String eId = "präambel-n1_formel-n1";

  @XmlAttribute private String refersTo = "eingangsformel";

  @XmlAttribute private String name = "attributsemantik-noch-undefiniert";

  @XmlElement(name = "p", namespace = NormTestDataBuilder.AKN_NS)
  private AknP paragraph;

  /**
   * Creates a formula with the given eId,.
   *
   * @param eId the Elements eId
   * @param refersTo what the formula refers to
   * @param text the formula text
   */
  public Formula(String eId, String refersTo, String text) {
    this(text);
    this.eId = eId;
    this.refersTo = refersTo;
  }

  /**
   * Creates a formula containing a single paragraph with the given text.
   *
   * @param text the formula text
   */
  public Formula(String text) {
    this();
    this.paragraph = new AknP("präambel-n1_formel-n1", text);
  }
}
