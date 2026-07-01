package de.bund.digitalservice.ris.builder.models.meta;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.meta.identification.Identification;
import de.bund.digitalservice.ris.builder.models.meta.lifecycle.Lifecycle;
import de.bund.digitalservice.ris.builder.models.meta.proprietary.Proprietary;
import de.bund.digitalservice.ris.builder.models.meta.temporal.TemporalData;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class Meta extends BaseElement {

  @Builder.Default
  @XmlAttribute(name = "eId")
  private String eId = "meta-n1";

  @Builder.Default
  @XmlElement(name = "identification", namespace = NormTestDataBuilder.AKN_NS)
  private Identification identification = Identification.builder().build();

  @Builder.Default
  @XmlElement(name = "lifecycle", namespace = NormTestDataBuilder.AKN_NS)
  private Lifecycle lifecycle = Lifecycle.builder().build();

  @Builder.Default
  @XmlElement(name = "temporalData", namespace = NormTestDataBuilder.AKN_NS)
  private TemporalData temporalData = TemporalData.builder().build();

  @Builder.Default
  @XmlElement(name = "proprietary", namespace = NormTestDataBuilder.AKN_NS)
  private Proprietary proprietary = Proprietary.builder().build();
}
