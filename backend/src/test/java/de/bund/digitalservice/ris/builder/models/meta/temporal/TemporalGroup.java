package de.bund.digitalservice.ris.builder.models.meta.temporal;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemporalGroup extends BaseElement {

  @Builder.Default
  @XmlAttribute(name = "eId")
  private String eId = "meta-n1_geltzeiten-n1_geltungszeitgr-n1";

  @Builder.Default
  @XmlElement(name = "timeInterval", namespace = NormTestDataBuilder.AKN_NS)
  private TimeInterval timeInterval = TimeInterval.builder().build();

  static TemporalGroup withEventRefs(String startEventEId, String endEventEId, String groupEId) {
    return TemporalGroup.builder()
        .eId(groupEId)
        .timeInterval(TimeInterval.withEventRefs(startEventEId, endEventEId, groupEId))
        .build();
  }
}
