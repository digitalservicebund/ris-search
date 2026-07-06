package de.bund.digitalservice.ris.builder.models.preamble;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:preamble} element, holding the table of contents and formula. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Preamble extends BaseElement {

  @Builder.Default @XmlAttribute private String eId = "präambel-n1";

  // This holds the ToC
  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private BlockContainer blockContainer;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Formula formula;

  public void addFormula(String text) {
    this.formula = Formula.withText(text);
  }

  /**
   * Creates a table of contents, attaches it to this preamble's block container and returns it.
   *
   * @return the created {@link Toc}
   */
  public Toc addToc() {
    Toc toc = Toc.builder().build();
    this.blockContainer = BlockContainer.builder().toc(toc).build();

    return toc;
  }
}
