package de.bund.digitalservice.ris.builder.models.preamble;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.common.Span;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:tocItem} element, a single entry in the table of contents. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TocItem extends BaseElement {

  @Builder.Default @XmlAttribute private String eId = "eintrag-n1";

  @Builder.Default @XmlAttribute private String href = "";

  @Builder.Default @XmlAttribute private String level = "1";

  @Builder.Default
  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Span span = Span.builder().content("Eintrag 1").build();

  /**
   * Creates a table of contents entry with the given text, nesting level and eId.
   *
   * @param text the entry text
   * @param level the nesting level, e.g. "1"
   * @param eId the eId of the entry
   * @return the built {@link TocItem}
   */
  public static TocItem withTextAndLevel(String text, String level, String eId) {
    return TocItem.builder()
        .level(level)
        .eId(eId)
        .span(Span.builder().eId(eId + "_span-n1").content(text).build())
        .build();
  }
}
