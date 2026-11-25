package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/** Where a cited document was published (ris:fundstelle). */
@Getter
public class Fundstelle {

  @XmlElement(name = "periodikum", namespace = LiteratureNamespaces.RIS_NS)
  private Periodikum periodikum;

  @XmlElement(name = "zitatstelle", namespace = LiteratureNamespaces.RIS_NS)
  private String zitatstelle;

  @XmlElement(name = "fundstellenTyp", namespace = LiteratureNamespaces.RIS_NS)
  private String fundstellenTyp;
}
