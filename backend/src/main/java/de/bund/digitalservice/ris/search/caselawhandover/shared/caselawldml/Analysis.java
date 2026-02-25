package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the analysis section of a caselaw.
 *
 * <p>The `Analysis` class is primarily used within the context of XML-based schemas, particularly
 * as part of the metadata representation in the `AdministrativeDirectiveLdml` structure. It
 * encapsulates analytical elements or references that are related to the document's content.
 *
 * <p>This class includes: - `otherReferences`: A collection of references that provide additional
 * context or support for the analysis. These references are represented by the `OtherReferences`
 * class and mapped to the XML element "otherReferences" using the namespace defined by
 * `AdministrativeDirectiveLdml.AKN_NS`.
 *
 * <p>The `Analysis` class is annotated with JAXB annotations to facilitate XML mapping and promote
 * interoperability within systems that conform to the Akoma Ntoso standard.
 */
@Getter
@Setter
public class Analysis {

  @XmlElement(name = "otherReferences", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private OtherReferences otherReferences;

  @XmlElement(name = "otherAnalysis", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private OtherAnalysis otherAnalysis;
}
