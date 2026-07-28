package de.bund.digitalservice.ris.builder.models.common;

import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:block} element, a generic grouping container. */
@NoArgsConstructor
@XmlSeeAlso({AknDate.class})
public class Block extends BaseElement {

  @XmlAttribute private String eId;

  @XmlAttribute private String name = "attributsemantik-noch-undefiniert";

  @XmlAnyElement private List<Object> children = new ArrayList<>();

  public Block(String eId, List<Object> children) {
    this.eId = eId;
    this.children = new ArrayList<>(children);
  }
}
