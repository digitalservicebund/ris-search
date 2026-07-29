package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.AknP;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:content} element wrapping a paragraph's text. */
@NoArgsConstructor
public class Content extends BaseElement {

  @XmlAttribute private String eId = "art-z1_abs-z1_inhalt-n1";

  @XmlElement(name = "p", namespace = NormTestDataBuilder.AKN_NS)
  private AknP paragraph =
      new AknP("art-n1_abs-z_inhalt-n1", "This is the first paragraph text content.");

  public Content(String paragraphText, String parentEId) {
    this.eId = parentEId + "_inhalt-n1";
    this.paragraph = new AknP(eId, paragraphText);
  }
}
