package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.common.Heading;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:section} element, grouping articles within the norm's body. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlRootElement(namespace = NormTestDataBuilder.AKN_NS)
public class Section extends BaseElement implements BodyElement {

  @Builder.Default @XmlAttribute private String eId = "abschnitt-n1";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private AknNum num;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Heading heading;

  @Builder.Default
  @XmlElement(name = "article", namespace = NormTestDataBuilder.AKN_NS)
  private List<Article> articles = new ArrayList<>();

  /**
   * Sets the section's number.
   *
   * @param num the section number, e.g. "Abschnitt 1"
   * @return this section for chaining
   */
  public Section addNum(String num) {
    this.num = AknNum.builder().eId(eId + "_bezeichnung-n1").value(num).build();

    return this;
  }

  /**
   * Sets the section's heading.
   *
   * @param text the heading text
   * @return this section for chaining
   */
  public Section addHeading(String text) {
    this.heading = Heading.builder().eId(eId + "_überschrift-n1").headline(List.of(text)).build();

    return this;
  }

  /**
   * Adds an article to this section.
   *
   * @param article the article to add
   * @return this section for chaining
   */
  public Section addArticle(Article article) {
    this.articles.add(article);
    return this;
  }
}
