package de.bund.digitalservice.ris.builder.models.common;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:heading} element, e.g. the heading of an article or chapter. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlSeeAlso({AuthorialNote.class})
public class Heading {

  @Builder.Default
  @XmlAttribute(name = "GUID")
  protected String guid = UUID.randomUUID().toString();

  @XmlAttribute private String eId;

  @Builder.Default @XmlAnyElement private List<Object> headline = new ArrayList<>();
}
