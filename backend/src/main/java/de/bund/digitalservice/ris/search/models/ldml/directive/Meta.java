package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents metadata information within an administrative directive or legal document structure.
 *
 * <p>The `Meta` class functions as a container for various components of metadata, encapsulating
 * the document's proprietary, analytical, identification, and classification details. This metadata
 * provides additional informational context and organizational structure for the document.
 *
 * <p>Key Features: - `proprietary`: Contains proprietary or custom metadata, represented by the
 * {@link Proprietary} class. This section may include document-specific or organization-specific
 * metadata. - `analysis`: Represents analytical metadata, captured by the {@link Analysis} class.
 * This typically holds references or analysis linked to the document's content. - `identification`:
 * Encapsulates document identification information, including bibliographic and work-level
 * metadata, using the {@link Identification} class. - `classifications`: Represents a list of
 * document classifications, each of which is modeled by the {@link Classification} class.
 * Classifications can include keywords and sources for further categorization.
 *
 * <p>The class uses JAXB annotations to define how its properties are serialized and deserialized
 * within the XML structure, leveraging the namespaces defined in the {@link
 * AdministrativeDirectiveLdml}.
 */
@Getter
@Setter
public class Meta {

  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  private Proprietary proprietary;

  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  private Analysis analysis;

  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  private Identification identification;

  @XmlElement(name = "classification", namespace = AdministrativeDirectiveLdml.AKN_NS)
  private List<Classification> classifications;
}
