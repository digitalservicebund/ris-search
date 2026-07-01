package de.bund.digitalservice.ris.builder.models;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.body.Body;
import de.bund.digitalservice.ris.builder.models.meta.Meta;
import de.bund.digitalservice.ris.builder.models.preface.Preface;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class Doc {

  @Builder.Default
  @XmlAttribute(name = "name")
  private String name = "/akn/ontology/de/concept/documenttype/bund/anlage-regelungstext";

  @XmlElement(name = "meta", namespace = NormTestDataBuilder.AKN_NS)
  private Meta meta;

  @XmlElement(name = "preface", namespace = NormTestDataBuilder.AKN_NS)
  private Preface preface;

  @XmlElement(name = "mainBody", namespace = NormTestDataBuilder.AKN_NS)
  private Body body;
}
