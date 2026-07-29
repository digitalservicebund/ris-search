package de.bund.digitalservice.ris.builder.models.meta.temporal;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:temporalGroup} element, wrapping a {@link TimeInterval}. */
@AllArgsConstructor
@NoArgsConstructor
public class TemporalGroup extends BaseElement {

  @XmlAttribute private String eId = "meta-n1_geltzeiten-n1_geltungszeitgr-n1";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private TimeInterval timeInterval = new TimeInterval();

  static TemporalGroup withEventRefs(String startEventEId, String endEventEId, String groupEId) {
    return new TemporalGroup(groupEId, new TimeInterval(startEventEId, endEventEId, groupEId));
  }
}
