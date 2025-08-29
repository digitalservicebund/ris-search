package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

@Getter
public class FundstelleSelbstaendig {

  @XmlAttribute(name = "titel")
  private String titel;

  @XmlAttribute(name = "zitatstelle")
  private String zitstelle;
}
