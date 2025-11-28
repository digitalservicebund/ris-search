package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an active reference in a document structure, with various attributes for detailed
 * identification and contextual information.
 *
 * <p>This class is designed for use in XML-based data serialization and deserialization workflows.
 * The following attributes are mapped to XML structure using JAXB annotations:
 *
 * <p>- `typeNumber`: Annotated with `@XmlAttribute`, it indicates the type number of the reference.
 * - `reference`: Annotated with `@XmlAttribute`, it specifies the reference identifier. -
 * `section`: Annotated with `@XmlAttribute`, it specifies the section to which the reference
 * pertains. - `paragraph`: Annotated with `@XmlAttribute`, it represents the paragraph in the
 * section. - `subParagraph`: Annotated with `@XmlAttribute`, it represents the sub-paragraph in the
 * paragraph. - `position`: Annotated with `@XmlAttribute`, it indicates the position within the
 * content. - `dateOfVersion`: Annotated with `@XmlAttribute`, it provides the date version
 * associated with the reference. - `value`: Annotated with `@XmlValue`, it represents the main
 * content or value of the active reference in the XML element.
 */
@Getter
@Setter
public class ActiveReference {
  @XmlAttribute private String typeNumber;

  @XmlAttribute private String reference;

  @XmlAttribute private String section;

  @XmlAttribute private String paragraph;

  @XmlAttribute private String subParagraph;

  @XmlAttribute private String position;

  @XmlAttribute private String dateOfVersion;

  @XmlValue private String value;
}
