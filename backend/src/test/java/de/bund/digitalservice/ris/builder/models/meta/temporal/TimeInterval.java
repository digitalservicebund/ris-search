package de.bund.digitalservice.ris.builder.models.meta.temporal;

import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeInterval extends BaseElement {

  @Builder.Default
  @XmlAttribute(name = "eId")
  private String eId = "meta-n1_geltzeiten-n1_geltungszeitgr-n1_gelzeitintervall-n1";

  @Builder.Default
  @XmlAttribute(name = "refersTo")
  private String refersTo = "geltungszeit";

  @Builder.Default
  @XmlAttribute(name = "start")
  private String start = "#meta-n1_lebzykl-n1_ereignis-n2";

  @XmlAttribute(name = "end")
  private String end;

  public static TimeInterval withEventRefs(
      String startEventEId, String endEventEId, String parentEId) {
    return TimeInterval.builder()
        .eId(parentEId + "_gelzeitintervall-n1")
        .start(startEventEId)
        .end(endEventEId)
        .build();
  }
}
