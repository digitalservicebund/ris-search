package de.bund.digitalservice.ris.search.models.ldml.literature.uli;

import de.bund.digitalservice.ris.search.models.ldml.FrbrExpression;
import de.bund.digitalservice.ris.search.models.ldml.FrbrWork;
import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 * Represents the identification section within the Akoma Ntoso (LegalDocumentML) metadata
 * structure.
 *
 * <p>This class encapsulates the bibliographic elements related to the identification of a
 * document, focusing on the FRBR (Functional Requirements for Bibliographic Records) model. It
 * provides information about the "work" and "expression" levels.
 *
 * <p>Fields: - frbrExpression: Represents the FRBR expression level of the document, which is
 * typically used to handle language-specific or version-specific details of a bibliographic work.
 * It is mapped to the "FRBRExpression" XML element in the Akoma Ntoso namespace. - frbrWork:
 * Represents the FRBR work level of the document, encapsulating bibliographic details such as
 * authors and titles. It is mapped to the "FRBRWork" XML element in the Akoma Ntoso namespace.
 *
 * <p>The class is designed to be serialized and deserialized using JAXB (Jakarta XML Binding), with
 * proper namespace allocation for Akoma Ntoso-compliant XML structures.
 */
@Getter
public class Identification {

  @XmlElement(name = "FRBRExpression", namespace = LiteratureNamespaces.AKN_NS)
  private FrbrExpression frbrExpression;

  @XmlElement(name = "FRBRWork", namespace = LiteratureNamespaces.AKN_NS)
  private FrbrWork frbrWork;
}
