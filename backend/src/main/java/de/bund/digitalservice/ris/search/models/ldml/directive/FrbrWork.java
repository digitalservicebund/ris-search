package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a Functional Requirements for Bibliographic Records (FRBR) Work entity.
 *
 * <p>The `FrbrWork` class corresponds to an XML element that encapsulates a list of name-value
 * pairs, commonly referred to as FRBR aliases. This class is designed for serialization and
 * deserialization within the Akoma Ntoso framework, specifically adhering to the XML namespace
 * defined in the `AdministrativeDirectiveLdml` class.
 *
 * <p>Fields: - `frbrAliasList`: A list of `FrbrNameValueElement` objects. Each element represents a
 * name-value pair assigned to the FRBR Work.
 *
 * <p>Annotations: - This class uses JAXB annotations to bind the `frbrAliasList` property to an XML
 * element named "FRBRalias", scoped within the namespace defined as `AKN_NS` by the
 * `AdministrativeDirectiveLdml` class.
 *
 * <p>This class is primarily used in the context of the Akoma Ntoso legislative document
 * representation standard, providing support for the encapsulation of metadata and attributes
 * related to FRBR Work entities.
 */
@Getter
@Setter
public class FrbrWork {

  @XmlElement(name = "FRBRalias", namespace = AdministrativeDirectiveLdml.AKN_NS)
  private List<FrbrNameValueElement> frbrAliasList;
}
