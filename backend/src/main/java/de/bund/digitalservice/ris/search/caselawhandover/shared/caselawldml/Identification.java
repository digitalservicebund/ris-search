package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Identification {
  @Builder.Default
  @XmlAttribute(name = "source")
  private String name = "attributsemantik-noch-undefiniert";

  @XmlElement(name = "FRBRWork", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private FrbrElement frbrWork;

  @XmlElement(name = "FRBRExpression", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private FrbrElement frbrExpression;

  @XmlElement(name = "FRBRManifestation", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private FrbrElement frbrManifestation;
}
