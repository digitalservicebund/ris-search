package de.bund.digitalservice.ris.builder.models.meta.temporal;

import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:timeInterval} element, defining a period of validity. */
@AllArgsConstructor
@NoArgsConstructor
public class TimeInterval extends BaseElement {

  @XmlAttribute private String eId = "meta-n1_geltzeiten-n1_geltungszeitgr-n1_gelzeitintervall-n1";

  @XmlAttribute private String refersTo = "geltungszeit";

  @XmlAttribute private String start = "#meta-n1_lebzykl-n1_ereignis-n2";

  @XmlAttribute private String end;

  /**
   * Creates a time interval starting and ending at the given lifecycle events.
   *
   * @param startEventEId reference to the event the interval starts at
   * @param endEventEId reference to the event the interval ends at
   * @param parentEId eId of the enclosing {@link TemporalGroup}, used to derive this element's eId
   */
  public TimeInterval(String startEventEId, String endEventEId, String parentEId) {
    this.eId = parentEId + "_gelzeitintervall-n1";
    this.start = startEventEId;
    this.end = endEventEId;
  }
}
