package de.bund.digitalservice.ris.builder.models;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.attachment.Attachments;
import de.bund.digitalservice.ris.builder.models.body.Body;
import de.bund.digitalservice.ris.builder.models.meta.Meta;
import de.bund.digitalservice.ris.builder.models.preamble.Preamble;
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
public class Act {

  @Builder.Default
  @XmlAttribute(name = "name")
  private String name = "/akn/ontology/de/concept/documenttype/bund/regelungstext-verkuendung";

  @Builder.Default
  @XmlElement(name = "meta", namespace = NormTestDataBuilder.AKN_NS)
  private Meta meta = Meta.builder().build();

  @Builder.Default
  @XmlElement(name = "preface", namespace = NormTestDataBuilder.AKN_NS)
  private Preface preface = Preface.builder().build();

  @XmlElement(name = "preamble", namespace = NormTestDataBuilder.AKN_NS)
  private Preamble preamble;

  @Builder.Default
  @XmlElement(name = "body", namespace = NormTestDataBuilder.AKN_NS)
  private Body body = Body.builder().build();

  @XmlElement(name = "conclusions", namespace = NormTestDataBuilder.AKN_NS)
  private Conclusions conclusions;

  @XmlElement(name = "attachments", namespace = NormTestDataBuilder.AKN_NS)
  private Attachments attachments;
}
