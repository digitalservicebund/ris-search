package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.common.Heading;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:chapter} element, grouping sections within the norm's body. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlRootElement(namespace = NormTestDataBuilder.AKN_NS)
public class Chapter extends BaseElement implements BodyElement {

  @Builder.Default @XmlAttribute private String eId = "kapitel-n1";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private AknNum num;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Heading heading;

  @Builder.Default
  @XmlElement(name = "section", namespace = NormTestDataBuilder.AKN_NS)
  private List<Section> sections = new ArrayList<>();

  /**
   * Sets the chapter's number.
   *
   * @param num the chapter number, e.g. "Kapitel 1"
   * @return this chapter for chaining
   */
  public Chapter addNum(String num) {
    this.num = AknNum.builder().eId(eId + "_bezeichnung-n1").value(num).build();

    return this;
  }

  /**
   * Sets the chapter's heading.
   *
   * @param text the heading text
   * @return this chapter for chaining
   */
  public Chapter addHeading(String text) {
    this.heading = Heading.builder().eId(eId + "_überschrift-n1").headline(List.of(text)).build();

    return this;
  }

  /**
   * Creates a section, lets the caller populate it, and adds it to this chapter.
   *
   * @param heading the section heading text
   * @param num the section number, e.g. "Abschnitt 1"
   * @param sectionConsumer callback used to populate the created {@link Section}
   * @return the created section
   */
  public Section addSection(String heading, String num, Consumer<Section> sectionConsumer) {
    Section section = new Section().addHeading(heading).addNum(num);
    sectionConsumer.accept(section);
    this.sections.add(section);
    return section;
  }
}
