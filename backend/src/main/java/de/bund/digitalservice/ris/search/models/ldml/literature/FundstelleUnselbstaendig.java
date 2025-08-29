package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

@Getter
public class FundstelleUnselbstaendig {

  @XmlAttribute(name = "periodikum")
  private String periodikum;

  @XmlAttribute(name = "zitatstelle")
  private String zitstelle;
}
