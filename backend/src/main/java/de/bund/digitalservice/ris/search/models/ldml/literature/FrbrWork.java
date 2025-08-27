package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

@Getter
public class FrbrWork {

  @XmlElement(name = "FRBRalias", namespace = LiteratureNamespaces.AKN_NS)
  private List<FrbrNameValueElement> frbrAliasList;

  @XmlElement(name = "FRBRauthor", namespace = LiteratureNamespaces.AKN_NS)
  private List<FrbrAuthor> frbrAuthors;
}
