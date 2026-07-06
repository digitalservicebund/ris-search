package de.bund.digitalservice.ris.builder.models.common;

import jakarta.xml.bind.annotation.XmlAttribute;
import java.util.UUID;

/** Base class for AKN elements providing the common {@code GUID} attribute. */
public class BaseElement {

  @XmlAttribute(name = "GUID")
  protected String guid = UUID.randomUUID().toString();
}
