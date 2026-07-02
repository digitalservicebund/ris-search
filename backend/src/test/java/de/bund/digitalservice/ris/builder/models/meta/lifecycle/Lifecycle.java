package de.bund.digitalservice.ris.builder.models.meta.lifecycle;

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

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lifecycle extends BaseElement {

  // Starting with 3 to account for the 2 default events
  @Builder.Default @XmlTransient private int eventEIdCounter = 3;

  @Builder.Default @XmlAttribute private String eId = "meta-n1_lebzykl-n1";

  @Builder.Default @XmlAttribute private String source = "attributsemantik-noch-undefiniert";

  @Builder.Default
  @XmlElement(name = "eventRef", namespace = NormTestDataBuilder.AKN_NS)
  private List<EventRef> eventRefs =
      new ArrayList<>(
          List.of(
              EventRef.builder()
                  .eId("meta-n1_lebzykl-n1_ereignis-n1")
                  .date("2025-01-01")
                  .refersTo("ausfertigung")
                  .type("generation")
                  .build(),
              EventRef.builder()
                  .eId("meta-n1_lebzykl-n1_ereignis-n2")
                  .date("2025-01-02")
                  .refersTo("inkrafttreten")
                  .type("generation")
                  .build()));

  public String addInForceEvent(String date) {
    if (date == null) {
      return null;
    }

    String eventEId = updateAndGetNextEventEId();
    eventRefs.add(EventRef.inForce(date, eventEId));

    return "#" + eventEId;
  }

  public String addOutOfForce(String date) {
    if (date == null) {
      return null;
    }

    String eventEId = updateAndGetNextEventEId();
    eventRefs.add(EventRef.outOfForce(date, eventEId));

    return "#" + eventEId;
  }

  private String updateAndGetNextEventEId() {
    String eventEId = "meta-n1_lebzykl-n1_ereignis-n" + eventEIdCounter;
    eventEIdCounter++;

    return eventEId;
  }
}
