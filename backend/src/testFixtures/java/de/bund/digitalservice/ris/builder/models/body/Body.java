package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.models.common.AknP;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:body} element, containing the norm's structural content. */
@NoArgsConstructor
@XmlSeeAlso({Article.class, Chapter.class, Section.class, AknP.class})
public class Body extends BaseElement {

  @XmlAttribute private String eId = "hauptteil-n1";

  @XmlAnyElement private List<BodyElement> children = new ArrayList<>();

  public Body(List<BodyElement> children) {
    this.children = new ArrayList<>(children);
  }

  public void addChild(BodyElement child) {
    this.children.add(child);
  }
}
