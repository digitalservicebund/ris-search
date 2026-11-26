package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/** A Fundstelle of an unselbstständige literature document within selbstständige literature. */
@Getter
public class FundstelleSelbststaendig {

  @XmlElement(name = "titel", namespace = LiteratureNamespaces.RIS_NS)
  private String titel;

  @XmlElement(name = "zitatstelle", namespace = LiteratureNamespaces.RIS_NS)
  private String zitatstelle;
}
