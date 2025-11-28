package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the root structure for an administrative directive in LDML format.
 *
 * <p>The `AdministrativeDirectiveLdml` class models the overarching XML structure for
 * administrative directives or legal documents as defined by the Akoma Ntoso standard (namespace:
 * AKN_NS) within the context of Legal Document Markup Language (LDML). It utilizes JAXB annotations
 * to define its XML representation.
 *
 * <p>Key Features: - The class is annotated with `@XmlRootElement` to designate it as the root
 * element in the XML hierarchy. - Includes the `doc` property, which is the primary element
 * encapsulating the document's main structure. - Defines namespaces: - `AKN_NS`: Namespace specific
 * to the Akoma Ntoso representation. - `RIS_NS`: Namespace used for additional metadata or
 * LDML-specific extensions.
 */
@Getter
@Setter
@XmlRootElement(name = "akomaNtoso", namespace = AdministrativeDirectiveLdml.AKN_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class AdministrativeDirectiveLdml {
  public static final String AKN_NS = "http://docs.oasis-open.org/legaldocml/ns/akn/3.0";
  public static final String RIS_NS = "http://ldml.neuris.de/meta/";

  @XmlElement(name = "doc", namespace = AKN_NS)
  private Doc doc;
}
