package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:paragraph} element within an article. */
@NoArgsConstructor
public class AknParagraph extends BaseElement {

  @XmlAttribute private String eId;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private AknNum num;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Content content;

  public AknParagraph(String paragraphText, String num, String parentEId, String eIdNumber) {
    String newEid = parentEId + "_abs-z" + eIdNumber;
    this.eId = newEid;
    this.num = new AknNum(newEid, num);
    this.content = new Content(paragraphText, newEid);
  }
}
