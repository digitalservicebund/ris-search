package de.bund.digitalservice.ris.builder.models.common;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlRootElement(name = "inline", namespace = NormTestDataBuilder.AKN_NS)
public class Inline {

  @Builder.Default
  @XmlAttribute(name = "GUID")
  private String guid = UUID.randomUUID().toString();

  @XmlAttribute(name = "eId")
  private String eId;

  @Builder.Default
  @XmlAttribute(name = "name")
  private String name = "attributsemantik-noch-undefiniert";

  @XmlAttribute(name = "refersTo")
  private String refersTo;

  @XmlValue private String content;
}
