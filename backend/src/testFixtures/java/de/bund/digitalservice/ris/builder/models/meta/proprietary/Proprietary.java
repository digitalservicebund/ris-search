package de.bund.digitalservice.ris.builder.models.meta.proprietary;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.meta.proprietary.ris.RisMetadata;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:proprietary} element, holding RIS-specific metadata. */
@NoArgsConstructor
public class Proprietary extends BaseElement {

  @XmlAttribute private String eId = "meta-n1_proprietary-n1";

  @XmlAttribute private String source = "attributsemantik-noch-undefiniert";

  @XmlElement(name = "legalDocML.de_metadaten", namespace = NormTestDataBuilder.RIS_NS)
  @Getter
  private RisMetadata risMetadata = new RisMetadata();
}
