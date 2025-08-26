package de.bund.digitalservice.ris.search.models.ldml.literature;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;

@Getter
public class Identification {

  @XmlElement(name = "FRBRExpression", namespace = LiteratureLdml.AKN_NS)
  private FrbrExpression frbrExpression;

  @XmlElement(name = "FRBRWork", namespace = LiteratureLdml.AKN_NS)
  private FrbrWork frbrWork;
}
