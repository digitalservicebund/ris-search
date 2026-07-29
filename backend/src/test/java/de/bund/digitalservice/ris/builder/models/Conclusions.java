package de.bund.digitalservice.ris.builder.models;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.preamble.Formula;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:conclusions} element holding the norm's closing formula. */
@NoArgsConstructor
public class Conclusions extends BaseElement {

  @XmlAttribute private String eId = "schluss-n1";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Formula formula;

  /**
   * Creates conclusions containing a single closing formula with the given text.
   *
   * @param text the closing formula text
   */
  public Conclusions(String text) {
    this.formula = new Formula("schluss-n1_formel-n1", "schlussformel", text);
  }
}
