package de.bund.digitalservice.ris.builder.models.common;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:span} element, e.g. a table of contents entry's text. */
@AllArgsConstructor
@NoArgsConstructor
public class Span extends BaseElement {

  @XmlAttribute private String eId = "span-n1";

  @XmlAnyElement private String content;

  public Span(String content) {
    this.content = content;
  }
}
