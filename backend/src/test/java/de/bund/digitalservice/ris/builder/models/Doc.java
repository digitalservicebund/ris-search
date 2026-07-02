package de.bund.digitalservice.ris.builder.models;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.body.Body;
import de.bund.digitalservice.ris.builder.models.meta.Meta;
import de.bund.digitalservice.ris.builder.models.preface.Preface;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Doc {

  @Builder.Default @XmlAttribute
  private String name = "/akn/ontology/de/concept/documenttype/bund/anlage-regelungstext";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Meta meta;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Preface preface;

  @XmlElement(name = "mainBody", namespace = NormTestDataBuilder.AKN_NS)
  private Body body;
}
