package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

@Getter
public class Meta {

  @XmlElement(name = "identification", namespace = LiteratureLdml.AKN_NS)
  private Identification identification;

  @XmlElement(name = "proprietary", namespace = LiteratureLdml.AKN_NS)
  private Proprietary proprietary;

  @XmlElement(name = "classification", namespace = LiteratureLdml.AKN_NS)
  private List<Classification> classifications;

  @XmlElement(name = "analysis", namespace = LiteratureLdml.AKN_NS)
  private Analysis analysis;

  @XmlElement(name = "references", namespace = LiteratureLdml.AKN_NS)
  private References references;

  @XmlElement(name = "gliederung", namespace = LiteratureLdml.RIS_NS)
  private Gliederung gliederung;
}
