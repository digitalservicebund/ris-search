package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents the references within a document in the search schema.
 *
 * <p>The {@code References} class is primarily used to encapsulate external reference information
 * related to a document. It contains a single field {@code externUrl}, which models an external URL
 * with additional metadata.
 *
 * <p>This class is part of the hierarchical structure of the application and is included within the
 * {@code Document} class to provide reference details.
 *
 * <p>Fields: - {@code externUrl}: Represents external URL information, encapsulated within the
 * {@code ExternUrl} class.
 *
 * <p>Annotations: - {@code @XmlAccessorType(XmlAccessType.FIELD)}: Configures how JAXB maps the
 * fields to XML elements. - {@code @XmlType(name = "references")}: Specifies the XML element name
 * for this class. - Lombok annotations such as {@code @Data} and {@code @Accessors(chain = true)}
 * are used for generating boilerplate code like getters, setters, and supporting method chaining.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "references")
@Data
@Accessors(chain = true)
public class References {

  @XmlElement(name = "extern-url")
  private ExternUrl externUrl;
}
