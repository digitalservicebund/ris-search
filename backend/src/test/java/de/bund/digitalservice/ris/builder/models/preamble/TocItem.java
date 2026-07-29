package de.bund.digitalservice.ris.builder.models.preamble;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.common.Span;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:tocItem} element, a single entry in the table of contents. */
@NoArgsConstructor
public class TocItem extends BaseElement {

  @XmlAttribute private String eId = "eintrag-n1";

  @XmlAttribute private String href = "";

  @XmlAttribute private String level = "1";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Span span = new Span("Eintrag 1");

  /**
   * Creates a table of contents entry with the given text, nesting level and eId.
   *
   * @param text the entry text
   * @param level the nesting level, e.g. "1"
   * @param eId the eId of the entry
   * @return the built {@link TocItem}
   */
  public TocItem(String text, String level, String eId) {
    this.eId = eId;
    this.span = new Span(eId + "_span-n1", text);
    this.level = level;
  }
}
