package de.bund.digitalservice.ris.search.models.ldml.literature.uli;

import de.bund.digitalservice.ris.search.models.ldml.literature.LiteratureNamespaces;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

/**
 * Represents the proprietary metadata information for RIS (Legal Information System) documents,
 * specifically targeting metadata for unselbstständige (dependent) publications.
 *
 * <p>This class is intended for serialization and deserialization of XML structures containing
 * proprietary information. The XML structure adheres to a specific namespace defined under the RIS
 * system for dependent publications.
 *
 * <p>Fields: - meta: An instance of {@code RisMeta} representing the metadata associated with
 * dependent publications. This field is annotated to map the "meta" XML element within the
 * namespace for unselbstständige (dependent) publications.
 *
 * <p>Annotations: - {@code @XmlElement} specifies the XML element name and namespace for
 * serialization. - {@code @Getter} from the Lombok library automatically generates getter methods
 * for the class fields.
 */
@Getter
public class Proprietary {

  @XmlElement(name = "meta", namespace = LiteratureNamespaces.RIS_UNSELBSTSTAENDIG_NS)
  private RisMeta meta;
}
