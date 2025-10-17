package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

@Getter
public class Gliederung {

  @XmlElement(name = "gliederungEntry", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private List<String> gliederungEntry;
}
