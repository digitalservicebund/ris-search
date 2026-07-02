package de.bund.digitalservice.ris.builder.models.attachment;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attachments extends BaseElement {

  @Builder.Default @XmlAttribute private String eId = "anlagen-n1";

  @Builder.Default
  @XmlElement(name = "attachment", namespace = NormTestDataBuilder.AKN_NS)
  private List<Attachment> attachments = new ArrayList<>();

  public void addAttachment(String manifestationEli, String eIdNumber) {
    attachments.add(
        Attachment.builder()
            .eId("anlagen-n1_anlage-n" + eIdNumber)
            .documentRef(DocumentRef.builder().href(manifestationEli).build())
            .build());
  }

  public int getAttachmentCount() {
    return this.attachments.size();
  }
}
