package de.bund.digitalservice.ris.builder.models.attachment;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents a single {@code akn:attachment} element referencing an attachment document. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attachment extends BaseElement {

  @Builder.Default @XmlAttribute private String eId = "anlagen-n1_anlage-n1";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private DocumentRef documentRef;
}
