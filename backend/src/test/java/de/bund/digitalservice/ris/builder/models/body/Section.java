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

  public Section addNum(String num) {
    this.num = AknNum.builder().eId(eId + "_bezeichnung-n1").value(num).build();

    return this;
  }

  public Section addHeading(String text) {
    this.heading = Heading.builder().eId(eId + "_überschrift-n1").headline(text).build();

    return this;
  }

  public Section addArticle(Article article) {
    this.articles.add(article);
    return this;
  }
}
