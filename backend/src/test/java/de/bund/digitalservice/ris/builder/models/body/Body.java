package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.models.common.AknP;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlSeeAlso({Article.class, Chapter.class, Section.class, AknP.class})
public class Body extends BaseElement {

  @Builder.Default @XmlAttribute private String eId = "hauptteil-n1";

  @Builder.Default @XmlAnyElement private List<BodyElement> children = new ArrayList<>();

  public void addChild(BodyElement child) {
    this.children.add(child);
  }
}
