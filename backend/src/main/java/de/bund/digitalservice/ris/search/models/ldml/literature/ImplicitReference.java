package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@Getter
public class ImplicitReference {

  @XmlElement(
      name = "fundstelleUnselbstaendig",
      namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private FundstelleUnselbstaendig fundstelleUnselbstaendig;

  @XmlElement(
      name = "fundstelleSelbstaendig",
      namespace = LiteratureNamespaces.RIS_SELBSTSTAENDIG_NS)
  private FundstelleSelbstaendig fundstelleSelbstaendig;
}
