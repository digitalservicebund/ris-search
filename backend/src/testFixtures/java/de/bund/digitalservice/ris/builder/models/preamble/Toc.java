package de.bund.digitalservice.ris.builder.models.preamble;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:toc} element, the norm's table of contents. */
@NoArgsConstructor
public class Toc extends BaseElement {

  @XmlTransient private int tocEntriesCounter = 0;

  @XmlAttribute private String eId = "präambel-n1_blockcontainer-n1_inhuebs-n1";

  @XmlElement(name = "tocItem", namespace = NormTestDataBuilder.AKN_NS)
  private List<TocItem> tocItems = new ArrayList<>();

  /**
   * Adds a table of contents entry with the given text and nesting level.
   *
   * @param text the entry text
   * @param level the nesting level, e.g. "1"
   * @return this table of contents for chaining
   */
  public Toc addEntry(String text, String level) {
    this.tocEntriesCounter++;
    tocItems.add(
        new TocItem(
            text, level, "präambel-n1_blockcontainer-n1_inhuebs-n1_eintrag-n" + tocEntriesCounter));

    return this;
  }
}
