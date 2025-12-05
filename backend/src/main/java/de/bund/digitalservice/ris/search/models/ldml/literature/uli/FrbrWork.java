package de.bund.digitalservice.ris.search.models.ldml.literature.uli;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

/**
 * Represents a bibliographic work in the FRBR (Functional Requirements for Bibliographic Records)
 * context.
 *
 * <p>This class is used for handling bibliographic details at the "work" level of the FRBR
 * hierarchy. It utilizes JAXB (Jakarta XML Binding) for the serialization and deserialization of
 * the corresponding XML structure that describes FRBR works.
 *
 * <p>Fields: - frbrAliasList: A list of alias elements (of type FrbrNameValueElement) for the work.
 * Each alias represents an alternative name or value associated with the work. Serialized under the
 * XML tag "FRBRalias". - frbrAuthors: A list of authors (of type FrbrAuthor) associated with the
 * work. Each author defines details such as the role and reference link of the contributor.
 * Serialized under the XML tag "FRBRauthor".
 */
@Getter
public class FrbrWork {

  @XmlElement(name = "FRBRalias", namespace = LiteratureNamespaces.AKN_NS)
  private List<FrbrNameValueElement> frbrAliasList;

  @XmlElement(name = "FRBRauthor", namespace = LiteratureNamespaces.AKN_NS)
  private List<FrbrAuthor> frbrAuthors;
}
