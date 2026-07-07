package de.bund.digitalservice.ris.builder.models.meta.proprietary;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.meta.proprietary.ris.RisMetadata;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:proprietary} element, holding RIS-specific metadata. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Proprietary extends BaseElement {

  @Builder.Default @XmlAttribute private String eId = "meta-n1_proprietary-n1";

  @Builder.Default @XmlAttribute private String source = "attributsemantik-noch-undefiniert";

  @Builder.Default
  @XmlElement(name = "legalDocML.de_metadaten", namespace = NormTestDataBuilder.RIS_NS)
  private RisMetadata risMetadata = RisMetadata.builder().build();
}
