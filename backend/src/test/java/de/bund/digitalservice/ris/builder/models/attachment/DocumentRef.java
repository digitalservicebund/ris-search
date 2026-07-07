package de.bund.digitalservice.ris.builder.models.attachment;

import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:documentRef} element linking an attachment to its document. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentRef extends BaseElement {

  @Builder.Default @XmlAttribute private String eId = "verweis-n1";

  @XmlAttribute private String href;

  @Builder.Default @XmlAttribute
  private String showAs = "/akn/ontology/de/concept/documenttype/bund/anlage-regelungstext";
}
