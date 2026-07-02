package de.bund.digitalservice.ris.builder.models;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.attachment.Attachments;
import de.bund.digitalservice.ris.builder.models.body.Body;
import de.bund.digitalservice.ris.builder.models.meta.Meta;
import de.bund.digitalservice.ris.builder.models.preamble.Preamble;
import de.bund.digitalservice.ris.builder.models.preface.Preface;
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
public class Act {

  @Builder.Default @XmlAttribute
  private String name = "/akn/ontology/de/concept/documenttype/bund/regelungstext-verkuendung";

  @Builder.Default
  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Meta meta = Meta.builder().build();

  @Builder.Default
  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Preface preface = Preface.builder().build();

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Preamble preamble;

  @Builder.Default
  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Body body = Body.builder().build();

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Conclusions conclusions;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Attachments attachments;
}
