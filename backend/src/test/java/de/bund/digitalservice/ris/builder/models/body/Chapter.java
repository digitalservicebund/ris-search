package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlRootElement(name = "chapter", namespace = NormTestDataBuilder.AKN_NS)
public class Chapter extends BaseElement implements BodyElement {

  @XmlAttribute(name = "eId")
  private String eId = "kapitel-n1";

  @XmlElement(name = "num", namespace = NormTestDataBuilder.AKN_NS)
  private AknNum num;

  @XmlElement(name = "heading", namespace = NormTestDataBuilder.AKN_NS)
  private Heading heading;

  @Builder.Default
  @XmlElement(name = "section", namespace = NormTestDataBuilder.AKN_NS)
  private List<Section> sections = new ArrayList<>();

  public Chapter addNum(String num) {
    this.num = AknNum.builder().eId(eId + "_bezeichnung-n1").value(num).build();

    return this;
  }

  public Chapter addHeading(String text) {
    this.heading = Heading.builder().eId(eId + "_überschrift-n1").headline(text).build();

    return this;
  }

  public Section addSection(String heading, String num) {
    Section section = new Section().addHeading(heading).addNum(num);
    this.sections.add(section);
    return section;
  }
}
