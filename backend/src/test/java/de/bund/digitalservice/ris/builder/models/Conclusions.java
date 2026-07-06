package de.bund.digitalservice.ris.builder.models;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.AknP;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.preamble.Formula;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:conclusions} element holding the norm's closing formula. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Conclusions extends BaseElement {

  @Builder.Default @XmlAttribute private String eId = "schluss-n1";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Formula formula;

  /**
   * Creates conclusions containing a single closing formula with the given text.
   *
   * @param text the closing formula text
   * @return the built {@link Conclusions}
   */
  public static Conclusions withText(String text) {
    return Conclusions.builder()
        .formula(
            Formula.builder()
                .eId("schluss-n1_formel-n1")
                .refersTo("schlussformel")
                .paragraph(AknP.withText(text))
                .build())
        .build();
  }
}
