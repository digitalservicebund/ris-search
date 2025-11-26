package de.bund.digitalservice.ris.search.models.eclicrawler.ecli;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents a document within the ECLI metadata structure, encapsulating its state and associated
 * metadata.
 *
 * <p>The `Document` class is designed for XML serialization and deserialization and adheres to JAXB
 * standards. It contains: - `status`: A string indicating the current state of the document (e.g.,
 * "deleted"). - `metadata`: Descriptive metadata associated with the document, conforming to ECLI
 * standards.
 *
 * <p>Constants: - `STATUS_DELETED`: A static constant representing the "deleted" status of a
 * document.
 *
 * <p>This class ensures that the document's status and metadata are properly structured and can be
 * manipulated or stored as part of the ECLI framework.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
@XmlRootElement(namespace = "https://e-justice.europa.eu/ecli")
@XmlType(propOrder = {"status", "metadata"})
public class Document {
  public static final String STATUS_DELETED = "deleted";
  private Metadata metadata;

  private String status;
}
