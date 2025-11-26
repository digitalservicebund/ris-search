package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the preface of a document in an XML structure.
 *
 * <p>The `Preface` class is a model used during XML serialization and deserialization within the
 * context of the AdministrativeDirectiveLdml schema. It contains the following components:
 *
 * <p>- `longTitle`: Represents the long title of the document's preface. It is mapped to an XML
 * element with a namespace defined by the `AKN_NS` constant in the `AdministrativeDirectiveLdml`
 * class.
 *
 * <p>This class is annotated with JAXB annotations for mapping its properties to corresponding XML
 * elements.
 */
@Getter
@Setter
public class Preface {

  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  LongTitle longTitle;
}
