package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;
import lombok.Setter;

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
