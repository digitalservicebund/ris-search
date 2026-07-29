package de.bund.digitalservice.ris.builder.models.common;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:date} element, e.g. a legislation date reference. */
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "date", namespace = NormTestDataBuilder.AKN_NS)
public class AknDate extends BaseElement {

  @XmlAttribute private String eId;

  @XmlAttribute private String refersTo;

  @XmlAttribute private String date;
}
