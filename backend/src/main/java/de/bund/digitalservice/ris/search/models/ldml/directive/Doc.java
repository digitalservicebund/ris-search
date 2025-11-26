package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the main document structure in an XML-based model.
 *
 * <p>The `Doc` class serves as the root element or primary container for various components of a
 * structured document in the context of the AdministrativeDirectiveLdml schema. It contains the
 * following elements:
 *
 * <p>- `meta`: Encapsulates metadata information about the document, such as proprietary
 * information, analysis, identification, and classifications. - `preface`: Represents the preface
 * section of the document, which may include introductory or descriptive information. - `mainBody`:
 * Holds the main content or core structure of the document.
 *
 * <p>This class is annotated with JAXB annotations to facilitate the mapping of its properties to
 * XML elements with namespaces defined by the `AKN_NS` constant in the
 * `AdministrativeDirectiveLdml` class.
 */
@Getter
@Setter
public class Doc {

  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  private Meta meta;

  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  private Preface preface;

  @XmlElement(namespace = AdministrativeDirectiveLdml.AKN_NS)
  private MainBody mainBody;
}
