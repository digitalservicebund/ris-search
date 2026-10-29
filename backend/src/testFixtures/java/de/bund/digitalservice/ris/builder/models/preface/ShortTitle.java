package de.bund.digitalservice.ris.builder.models.preface;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.common.Inline;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import java.util.List;

/** Represents the {@code akn:shortTitle} element, the norm's short title (Kurztitel). */
@XmlSeeAlso({Inline.class})
@XmlRootElement(namespace = NormTestDataBuilder.AKN_NS)
public class ShortTitle extends BaseElement {

  @XmlAttribute private String eId = "einleitung-n1_doktitel-n1_text-n1_kurztitel-n1";

  @XmlAnyElement private List<Object> children;

  public ShortTitle() {
    this("Short Title of the Legislation");
  }

  public ShortTitle(String shortTitle) {
    this.children = List.of(shortTitle);
  }
}
