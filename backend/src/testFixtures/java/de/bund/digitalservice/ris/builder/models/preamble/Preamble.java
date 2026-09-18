package de.bund.digitalservice.ris.builder.models.preamble;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:preamble} element, holding the table of contents and formula. */
@NoArgsConstructor
public class Preamble extends BaseElement {

  @XmlAttribute private String eId = "präambel-n1";

  // This holds the ToC
  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private BlockContainer blockContainer;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Formula formula;

  public void addFormula(String text) {
    this.formula = new Formula(text);
  }

  /**
   * Creates a table of contents, attaches it to this preamble's block container and returns it.
   *
   * @return the created {@link Toc}
   */
  public Toc addToc() {
    Toc toc = new Toc();
    this.blockContainer = new BlockContainer(toc);

    return toc;
  }
}
