package de.bund.digitalservice.ris.builder.models.common;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Represents an {@code akn:authorialNote} element, e.g. a footnote attached to a title or heading.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlRootElement(name = "authorialNote", namespace = NormTestDataBuilder.AKN_NS)
public class AuthorialNote extends BaseElement {

  @Builder.Default @XmlAttribute private String eId = "amtlfnote-n1";

  @Builder.Default @XmlAttribute private String marker = "1";

  @XmlElement(name = "p", namespace = NormTestDataBuilder.AKN_NS)
  private AknP paragraph;

  public static AuthorialNote withText(String text) {
    return AuthorialNote.builder().paragraph(AknP.withText(text)).build();
  }
}
