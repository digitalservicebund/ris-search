package de.bund.digitalservice.ris.builder.models.preamble;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Toc extends BaseElement {

  @Builder.Default @Transient private int tocEntriesCounter = 0;

  @Builder.Default @XmlAttribute private String eId = "präambel-n1_blockcontainer-n1_inhuebs-n1";

  @Builder.Default
  @XmlElement(name = "tocItem", namespace = NormTestDataBuilder.AKN_NS)
  private List<TocItem> tocItems = new ArrayList<>();

  public Toc addEntry(String text, String level) {
    this.tocEntriesCounter++;
    tocItems.add(
        TocItem.withTextAndLevel(
            text, level, "präambel-n1_blockcontainer-n1_inhuebs-n1_eintrag-n" + tocEntriesCounter));

    return this;
  }
}
