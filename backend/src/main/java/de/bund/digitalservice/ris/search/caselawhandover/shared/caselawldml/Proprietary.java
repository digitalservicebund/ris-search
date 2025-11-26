package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the classification element in a case law LDML document. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Proprietary {
  @Builder.Default
  @XmlAttribute(name = "source")
  private String name = "attributsemantik-noch-undefiniert";

  @XmlElement(name = "meta", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private RisMeta meta;
}
