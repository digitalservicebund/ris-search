package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents proprietary metadata information within the LDML (Legal Document Markup Language)
 * framework.
 *
 * <p>The `Proprietary` class is part of the metadata hierarchy in the context of administrative
 * directives and legal documents. It encapsulates additional metadata details that are proprietary
 * in nature, using the `RisMeta` class for detailed regulatory impact statement (RIS) metadata.
 *
 * <p>This class includes: - `meta`: A property representing RIS-specific metadata, using the {@link
 * RisMeta} class. It is annotated with JAXB for mapping to the XML element `meta` within the
 * namespace defined by the `RIS_NS` constant in the {@link AdministrativeDirectiveLdml} class.
 *
 * <p>Annotations: - `@Getter` and `@Setter` from Lombok are used to auto-generate the respective
 * getter and setter methods. - JAXB annotations are applied to the `meta` property for XML
 * serialization/deserialization.
 */
@Getter
@Setter
public class Proprietary {

  @XmlElement(name = "meta", namespace = AdministrativeDirectiveLdml.RIS_NS)
  private RisMeta meta;
}
