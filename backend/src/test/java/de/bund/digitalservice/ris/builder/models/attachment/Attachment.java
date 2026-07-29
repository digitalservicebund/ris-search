package de.bund.digitalservice.ris.builder.models.attachment;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.NoArgsConstructor;

/** Represents a single {@code akn:attachment} element referencing an attachment document. */
@NoArgsConstructor
public class Attachment extends BaseElement {

  @XmlAttribute private String eId = "anlagen-n1_anlage-n1";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private DocumentRef documentRef;

  /**
   * Creates an attachment with the given number and document reference.
   *
   * @param eIdNumber the attachment number used to build the eId
   * @param documentRef the referenced attachment document
   */
  public Attachment(String eIdNumber, DocumentRef documentRef) {
    this.eId = "anlagen-n1_anlage-n" + eIdNumber;
    this.documentRef = documentRef;
  }
}
