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

/** Represents an {@code akn:block} element, a generic grouping container. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlSeeAlso({AknDate.class})
public class Block {

  @Builder.Default
  @XmlAttribute(name = "GUID")
  private String guid = UUID.randomUUID().toString();

  @XmlAttribute private String eId;

  @Builder.Default @XmlAttribute private String name = "attributsemantik-noch-undefiniert";

  @Builder.Default @XmlAnyElement private List<Object> children = new ArrayList<>();
}
