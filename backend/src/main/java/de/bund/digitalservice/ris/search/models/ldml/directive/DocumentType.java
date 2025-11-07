package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentType {

  @XmlAttribute private DocumentTypeCategory category;

  @XmlAttribute private String longTitle;

  @XmlValue private String value;
}
