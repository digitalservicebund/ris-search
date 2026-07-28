package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.common.Heading;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:section} element, grouping articles within the norm's body. */
@NoArgsConstructor
@XmlRootElement(namespace = NormTestDataBuilder.AKN_NS)
public class Section extends BaseElement implements BodyElement {

  @XmlAttribute private String eId = "abschnitt-n1";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private AknNum num;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Heading heading;

  @XmlElement(name = "article", namespace = NormTestDataBuilder.AKN_NS)
  private List<Article> articles = new ArrayList<>();

  public Section(String heading, String num) {
    this.num = new AknNum(eId, num);
    this.heading =
        Heading.builder().eId(eId + "_überschrift-n1").headline(List.of(heading)).build();
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
