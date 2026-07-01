package de.bund.digitalservice.ris.builder.models;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "akomaNtoso", namespace = NormTestDataBuilder.AKN_NS)
public class AkomaNtoso {

  @XmlElement(name = "act", namespace = NormTestDataBuilder.AKN_NS)
  private Act act;
}
