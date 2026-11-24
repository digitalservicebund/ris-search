package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 * Represents the proprietary metadata information of RIS (Legal Information System) literature
 * documents, both selbstständige and unselbstständige.
 *
 * <p>This class is intended for serialization and deserialization of XML structures containing
 * proprietary information. The XML structure adheres to the RIS namespace.
 *
 * <p>Fields: - meta: An instance of {@code RisMeta} representing the RIS metadata of the
 * publication. This field is annotated to map the "meta" XML element within the RIS namespace.
 *
 * <p>Annotations: - {@code @XmlElement} specifies the XML element name and namespace for
 * serialization. - {@code @Getter} from the Lombok library automatically generates getter methods
 * for the class fields.
 */
@Getter
public class Proprietary {

  @XmlElement(name = "meta", namespace = LiteratureNamespaces.RIS_NS)
  private RisMeta meta;
}
