package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the identification information of a document within the LDML framework.
 *
 * <p>The `Identification` class models the FRBR (Functional Requirements for Bibliographic Records)
 * Work entity, which encapsulates metadata about a legal or administrative document. The FRBR Work
 * provides identification and attribution details for the document it represents.
 *
 * <p>Key Features: - Utilizes JAXB annotations to bind to an XML element named `FRBRWork` within
 * the namespace defined by `AKN_NS` from the `AdministrativeDirectiveLdml` class. - Contains a
 * single field, `frbrWork`, which represents the FRBR Work entity.
 */
@Getter
@Setter
public class Identification {

  @XmlElement(name = "FRBRWork", namespace = AdministrativeDirectiveLdml.AKN_NS)
  private FrbrWork frbrWork;
}
