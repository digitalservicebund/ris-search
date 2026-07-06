package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.AknP;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:content} element wrapping a paragraph's text. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Content extends BaseElement {

  @Builder.Default @XmlAttribute private String eId = "art-z1_abs-z1_inhalt-n1";

  @Builder.Default
  @XmlElement(name = "p", namespace = NormTestDataBuilder.AKN_NS)
  private AknP paragraph =
      AknP.builder()
          .eId("art-n1_abs-z_inhalt-n1_text-n1")
          .children(List.of("This is the first paragraph text content."))
          .build();

  static Content withText(String paragraphText, String parentEId) {
    String newEId = parentEId + "_inhalt-n1";
    return Content.builder()
        .eId(newEId)
        .paragraph(AknP.builder().eId(newEId + "_text-n1").children(List.of(paragraphText)).build())
        .build();
  }
}
