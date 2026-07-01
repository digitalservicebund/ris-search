package de.bund.digitalservice.ris.builder.models.common;

import jakarta.xml.bind.annotation.XmlAttribute;
import java.util.UUID;

public class BaseElement {

  @XmlAttribute(name = "GUID")
  protected String guid = UUID.randomUUID().toString();
}
