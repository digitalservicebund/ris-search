package de.bund.digitalservice.ris.builder.models.common;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Span {

  @Builder.Default
  @XmlAttribute(name = "GUID")
  private String guid = UUID.randomUUID().toString();

  @Builder.Default @XmlAttribute private String eId = "span-n1";

  @XmlValue private String content;
}
