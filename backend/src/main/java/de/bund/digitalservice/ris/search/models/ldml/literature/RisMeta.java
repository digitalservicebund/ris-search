package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import lombok.Getter;

@Getter
public class RisMeta {
  @XmlElementWrapper(
      name = "veroeffentlichungsJahre",
      namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  @XmlElement(
      name = "veroeffentlichungsJahr",
      namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private List<String> yearsOfPublication;

  @XmlElement(name = "gliederung", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private Gliederung gliederung;
}
