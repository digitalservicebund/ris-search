package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

/**
 * Represents a single FRBR name-value pair element within the context of the FRBR (Functional
 * Requirements for Bibliographic Records) framework.
 *
 * <p>This class is primarily used for modeling XML structures where name-value pairs are required,
 * such as within metadata or bibliographic frameworks. It leverages JAXB (Jakarta XML Binding) for
 * XML binding, allowing seamless serialization and deserialization to and from XML format.
 *
 * <p>Fields: - name: The name attribute of the FRBR element, typically representing an identifier
 * or label within the bibliographic structure. Serialized as an XML attribute. - value: The value
 * associated with the name attribute, providing the data or detail corresponding to the name.
 * Serialized as an XML attribute.
 */
@Getter
public class FrbrNameValueElement {

  @XmlAttribute private String name;

  @XmlAttribute private String value;
}
