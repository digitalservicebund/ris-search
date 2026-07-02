package de.bund.digitalservice.ris.builder.models.common;

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
@XmlRootElement(name = "date", namespace = NormTestDataBuilder.AKN_NS)
public class AknDate {

  @Builder.Default
  @XmlAttribute(name = "GUID")
  private String guid = UUID.randomUUID().toString();

  @XmlAttribute private String eId;

  @XmlAttribute private String refersTo;

  @XmlAttribute private String date;
}
