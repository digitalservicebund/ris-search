package de.bund.digitalservice.ris.builder.models.common;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.body.BodyElement;
import de.bund.digitalservice.ris.builder.models.preface.DocStage;
import de.bund.digitalservice.ris.builder.models.preface.DocTitle;
import de.bund.digitalservice.ris.builder.models.preface.ShortTitle;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlSeeAlso({DocStage.class, DocTitle.class, ShortTitle.class, AuthorialNote.class})
@XmlRootElement(name = "p", namespace = NormTestDataBuilder.AKN_NS)
public class AknP implements BodyElement {

  @Builder.Default
  @XmlAttribute(name = "GUID")
  private String guid = UUID.randomUUID().toString();

  @Builder.Default @XmlAttribute private String eId = "text-n1";

  @Builder.Default @XmlAnyElement private List<Object> children = new ArrayList<>();

  public static AknP withText(String text) {
    return AknP.builder().children(new ArrayList<>(List.of(text))).build();
  }

  public AknP addChild(Object child) {
    this.children.add(child);
    return this;
  }
}
