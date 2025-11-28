package de.bund.digitalservice.ris.search.nlex.schema.result;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Represents a document in the search schema.
 *
 * <p>The {@code Document} class serves as a top-level structure that encapsulates the main
 * components of a document, which include references and content.
 *
 * <p>Fields: - {@code references}: Encapsulates external reference information, represented by the
 * {@code References} class. - {@code content}: Represents the main content of the document, modeled
 * by the {@code Content} class.
 *
 * <p>Annotations: - {@code @XmlAccessorType(XmlAccessType.FIELD)}: Specifies that JAXB maps the
 * fields directly to XML elements. - Lombok annotations {@code @Data} and {@code @Accessors(chain =
 * true)}: Automatically generate methods for field access and enable method chaining.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@Accessors(chain = true)
public class Document {
  private References references;

  private Content content;
}
