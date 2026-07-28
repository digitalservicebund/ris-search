package de.bund.digitalservice.ris.builder.models.attachment;

import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:documentRef} element linking an attachment to its document. */
@NoArgsConstructor
public class DocumentRef extends BaseElement {

  @XmlAttribute private String eId = "verweis-n1";

  @XmlAttribute private String href;

  @XmlAttribute
  private String showAs = "/akn/ontology/de/concept/documenttype/bund/anlage-regelungstext";

  public DocumentRef(String href) {
    this.href = href;
  }
}
