package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents a block of opinions within the motivation section of a Case Law LDML document. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
public class AknMotivationBlock {

  @XmlAttribute(name = "name")
  private String name;

  @XmlElement(name = "opinion", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private List<AknOpinion> opinions;
}
