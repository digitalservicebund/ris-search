package de.bund.digitalservice.ris.builder.models.body;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AknNum {

  @Builder.Default
  @XmlAttribute(name = "GUID")
  protected String guid = UUID.randomUUID().toString();

  @Builder.Default @XmlAttribute private String eId = "art-z1_bezeichnung-n1";

  @XmlAttribute private String refersTo;

  @Builder.Default @XmlValue private String value = "§ 1";
}
