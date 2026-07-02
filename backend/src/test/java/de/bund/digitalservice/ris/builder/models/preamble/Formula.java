package de.bund.digitalservice.ris.builder.models.preamble;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.AknP;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Formula extends BaseElement {

  @Builder.Default
  @XmlAttribute(name = "eId")
  private String eId = "präambel-n1_formel-n1";

  @Builder.Default
  @XmlAttribute(name = "refersTo")
  private String refersTo = "eingangsformel";

  @Builder.Default
  @XmlAttribute(name = "name")
  private String name = "attributsemantik-noch-undefiniert";

  @XmlElement(name = "p", namespace = NormTestDataBuilder.AKN_NS)
  private AknP paragraph;

  public static Formula withText(String text) {
    return Formula.builder()
        .paragraph(
            AknP.builder().eId("präambel-n1_formel-n1_text-n1").children(List.of(text)).build())
        .build();
  }
}
