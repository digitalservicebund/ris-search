package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.common.Heading;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:chapter} element, grouping sections within the norm's body. */
@NoArgsConstructor
@XmlRootElement(namespace = NormTestDataBuilder.AKN_NS)
public class Chapter extends BaseElement implements BodyElement {

  @XmlAttribute private String eId = "kapitel-n1";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private AknNum num;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Heading heading;

  @XmlAnyElement private List<BodyElement> children = new ArrayList<>();

  public Chapter(String heading, String num) {
    this.num = new AknNum(eId, num);
    this.heading =
        Heading.builder().eId(eId + "_überschrift-n1").headline(List.of(heading)).build();
  }

  public Chapter(String heading, String num, String eId) {
    this(heading, num);
    this.eId = eId;
  }

  /**
   * Creates a section, lets the caller populate it, and adds it to this chapter.
   *
   * @param heading the section heading text
   * @param num the section number, e.g. "Abschnitt 1"
   * @param sectionConsumer callback used to populate the created {@link Section}
   * @return the created section
   */
  public Chapter addSection(String heading, String num, Consumer<Section> sectionConsumer) {
    Section section = new Section().addHeading(heading).addNum(num);
    sectionConsumer.accept(section);
    this.children.add(section);
    return this;
  }

  /**
   * Creates a article, lets the caller populate it, and adds it to this chapter.
   *
   * @param article to add as child
   * @return the Chapter
   */
  public Chapter addArticle(Article article) {
    this.children.add(article);
    return this;
  }
}
