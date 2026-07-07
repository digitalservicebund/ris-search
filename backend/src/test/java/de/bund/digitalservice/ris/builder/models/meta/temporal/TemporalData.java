package de.bund.digitalservice.ris.builder.models.meta.temporal;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:temporalData} element, a container of {@link TemporalGroup}s. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemporalData extends BaseElement {

  // Starting with 2 to account for the default group
  @Builder.Default @XmlTransient private int temporalGroupsCounter = 2;

  @Builder.Default @XmlAttribute private String eId = "meta-n1_geltzeiten-n1";

  @Builder.Default @XmlAttribute private String source = "attributsemantik-noch-undefiniert";

  @Builder.Default
  @XmlElement(name = "temporalGroup", namespace = NormTestDataBuilder.AKN_NS)
  private List<TemporalGroup> temporalGroups =
      new ArrayList<>(List.of(TemporalGroup.builder().build()));

  /**
   * Adds a temporal group linking the given lifecycle events and returns its eId.
   *
   * @param startEventEId reference to the event the interval starts at, e.g.
   *     "#meta-n1_lebzykl-n1_ereignis-n3"
   * @param endEventEId reference to the event the interval ends at, e.g.
   *     "#meta-n1_lebzykl-n1_ereignis-n4"
   * @return the eId of the created temporal group
   */
  public String addTemporalGroup(String startEventEId, String endEventEId) {
    String temporalGroupEId = "meta-n1_geltzeiten-n1_geltungszeitgr-n" + temporalGroupsCounter;
    temporalGroups.add(TemporalGroup.withEventRefs(startEventEId, endEventEId, temporalGroupEId));
    temporalGroupsCounter++;
    return temporalGroupEId;
  }
}
