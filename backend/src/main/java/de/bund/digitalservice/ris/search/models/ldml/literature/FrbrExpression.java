package de.bund.digitalservice.ris.search.models.ldml.literature;

import de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml.FrbrLanguage;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;

/**
 * Represents the FRBR (Functional Requirements for Bibliographic Records) expression level in the
 * bibliographic hierarchy.
 *
 * <p>This class is utilized for handling details specific to the "expression" level of the FRBR
 * model, which involves encoding aspects such as language versions of a work. It is part of the
 * legal documentation metadata and supports XML serialization and deserialization using JAXB
 * (Jakarta XML Binding).
 *
 * <p>Fields: - frbrLanguages: A list of languages (represented by the FrbrLanguage class) that are
 * associated with this FRBR expression. These languages define the linguistic components of the
 * bibliographic expression and are serialized under the XML tag "FRBRlanguage".
 */
@Getter
public class FrbrExpression {

  @XmlElement(name = "FRBRlanguage", namespace = LiteratureNamespaces.AKN_NS)
  private List<FrbrLanguage> frbrLanguages;
}
