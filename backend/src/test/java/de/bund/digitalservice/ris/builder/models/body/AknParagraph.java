package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:paragraph} element within an article. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AknParagraph extends BaseElement {

  @XmlAttribute private String eId;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private AknNum num;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Content content;

  static AknParagraph withText(
      String paragraphText, String num, String parentEId, String eIdNumber) {
    String newEId = parentEId + "_abs-z" + eIdNumber;
    return AknParagraph.builder()
        .eId(newEId)
        .num(new AknNum(newEId, num))
        .content(Content.withText(paragraphText, newEId))
        .build();
  }
}
