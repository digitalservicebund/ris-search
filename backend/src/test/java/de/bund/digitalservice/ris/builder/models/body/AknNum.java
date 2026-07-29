package de.bund.digitalservice.ris.builder.models.body;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import java.util.UUID;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:num} element, e.g. the number of an article or paragraph. */
@NoArgsConstructor
public class AknNum {

  @XmlAttribute(name = "GUID")
  protected String guid = UUID.randomUUID().toString();

  @XmlAttribute private String eId = "art-z1_bezeichnung-n1";

  @XmlAttribute private String refersTo;

  @XmlValue private String value = "§ 1";

  /**
   * Creates a num element derived from the given parent eId and display value.
   *
   * @param parentEId the eId of the enclosing element, used to build this element's eId
   * @param value the display value, e.g. "§ 1" or "(1)"
   */
  public AknNum(String parentEId, String value) {
    this.eId = parentEId + "_bezeichnung-n1";
    this.value = value;
  }
}
