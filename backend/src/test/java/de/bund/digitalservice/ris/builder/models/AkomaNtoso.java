package de.bund.digitalservice.ris.builder.models;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the {@code akn:akomaNtoso} document root, wrapping either an {@link Act} or a {@link
 * Doc}.
 */
@Getter
@Setter
@XmlRootElement(namespace = NormTestDataBuilder.AKN_NS)
public class AkomaNtoso {

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Act act;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Doc doc;
}
