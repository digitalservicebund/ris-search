package de.bund.digitalservice.ris.builder.models.meta.lifecycle;

import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:eventRef} element, a single entry in the norm's lifecycle. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRef extends BaseElement {

  @XmlAttribute private String eId;

  @XmlAttribute private String date;

  @XmlAttribute private String refersTo;

  @Builder.Default @XmlAttribute private String source = "attributsemantik-noch-undefiniert";

  @XmlAttribute private String type;

  /**
   * Creates an in-force ({@code inkrafttreten}) amendment event at the given date.
   *
   * @param date the date the event occurs, in the format YYYY-MM-DD
   * @param eId the eId of the event
   * @return the built {@link EventRef}
   */
  public static EventRef inForce(String date, String eId) {
    return EventRef.builder()
        .eId(eId)
        .refersTo("inkrafttreten")
        .type("amendment")
        .date(date)
        .build();
  }

  /**
   * Creates an out-of-force ({@code ausserkrafttreten}) amendment event at the given date.
   *
   * @param date the date the event occurs, in the format YYYY-MM-DD
   * @param eId the eId of the event
   * @return the built {@link EventRef}
   */
  public static EventRef outOfForce(String date, String eId) {
    return EventRef.builder()
        .eId(eId)
        .refersTo("ausserkrafttreten")
        .type("amendment")
        .date(date)
        .build();
  }
}
