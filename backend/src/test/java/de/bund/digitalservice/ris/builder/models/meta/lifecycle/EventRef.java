package de.bund.digitalservice.ris.builder.models.meta.lifecycle;

import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRef extends BaseElement {

  @XmlAttribute(name = "eId")
  private String eId;

  @XmlAttribute(name = "date")
  private String date;

  @XmlAttribute(name = "refersTo")
  private String refersTo;

  @Builder.Default
  @XmlAttribute(name = "source")
  private String source = "attributsemantik-noch-undefiniert";

  @XmlAttribute(name = "type")
  private String type;

  public static EventRef inForce(String date, String eId) {
    return EventRef.builder()
        .eId(eId)
        .refersTo("inkrafttreten")
        .type("amendment")
        .date(date)
        .build();
  }

  public static EventRef outOfForce(String date, String eId) {
    return EventRef.builder()
        .eId(eId)
        .refersTo("ausserkrafttreten")
        .type("amendment")
        .date(date)
        .build();
  }
}
