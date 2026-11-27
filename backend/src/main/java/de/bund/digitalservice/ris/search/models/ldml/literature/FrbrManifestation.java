package de.bund.digitalservice.ris.search.models.ldml.literature;

import de.bund.digitalservice.ris.search.models.ldml.FrbrNameValueElement;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

/**
 * Represents the FRBR (Functional Requirements for Bibliographic Records) manifestation level in
 * the bibliographic hierarchy.
 *
 * <p>Fields: - frbrAliasList: A list of alias elements (e.g. ISBN or ISSN) of the manifestation,
 * serialized under the XML tag "FRBRalias".
 */
@Getter
public class FrbrManifestation {

  @XmlElement(name = "FRBRalias", namespace = LiteratureNamespaces.AKN_NS)
  private List<FrbrNameValueElement> frbrAliasList;
}
