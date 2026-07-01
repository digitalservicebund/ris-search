package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AknParagraph extends BaseElement {

  @XmlAttribute(name = "eId")
  private String eId;

  @XmlElement(name = "num", namespace = NormTestDataBuilder.AKN_NS)
  private AknNum num;

  @XmlElement(name = "content", namespace = NormTestDataBuilder.AKN_NS)
  private Content content;

  static AknParagraph withText(
      String paragraphText, String num, String parentEId, String eIdNumber) {
    String newEId = parentEId + "_abs-z" + eIdNumber;
    return AknParagraph.builder()
        .eId(newEId)
        .num(AknNum.builder().eId(newEId + "_bezeichnung-n1").value(num).build())
        .content(Content.withText(paragraphText, newEId))
        .build();
  }
}
