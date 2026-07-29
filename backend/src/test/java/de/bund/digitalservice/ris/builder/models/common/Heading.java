package de.bund.digitalservice.ris.builder.models.common;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:heading} element, e.g. the heading of an article or chapter. */
@AllArgsConstructor
@NoArgsConstructor
@XmlSeeAlso({AuthorialNote.class})
public class Heading extends BaseElement {

  @XmlAttribute private String eId;

  @XmlAnyElement private List<Object> headline = new ArrayList<>();
}
