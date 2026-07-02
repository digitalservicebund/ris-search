package de.bund.digitalservice.ris.builder.models.preface;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlRootElement(namespace = NormTestDataBuilder.AKN_NS)
public class DocStage {

  @Builder.Default
  @XmlAttribute(name = "GUID")
  String guid = UUID.randomUUID().toString();

  @Builder.Default @XmlAttribute
  private String eId = "einleitung-n1_doktitel-n1_text-n1_docstadium-n1";
}
