package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/** The court (ris:gericht) of a cited court decision. */
@Getter
public class RisGericht {

  @XmlElement(name = "gerichtstyp", namespace = LiteratureNamespaces.RIS_NS)
  private String gerichtstyp;

  @XmlElement(name = "gerichtsort", namespace = LiteratureNamespaces.RIS_NS)
  private String gerichtsort;

  @XmlElement(name = "gerichtsbarkeit", namespace = LiteratureNamespaces.RIS_NS)
  private String gerichtsbarkeit;

  @XmlElement(name = "spruchkoerper", namespace = LiteratureNamespaces.RIS_NS)
  private String spruchkoerper;

  @XmlElement(name = "sitzDerAussenstelle", namespace = LiteratureNamespaces.RIS_NS)
  private String sitzDerAussenstelle;
}
