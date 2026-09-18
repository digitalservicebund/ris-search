package de.bund.digitalservice.ris.builder.models.preface;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:docTitle} element, the norm's official title (Langtitel). */
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(namespace = NormTestDataBuilder.AKN_NS)
public class DocTitle extends BaseElement {

  @XmlAttribute private String eId = "einleitung-n1_doktitel-n1_text-n1_doctitel-n1";

  @XmlAnyElement @Getter private List<Object> children = new ArrayList<>(List.of("Test Gesetz"));

  public DocTitle(List<Object> children) {
    this.children = new ArrayList<>(children);
  }
}
