package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlRootElement(name = "article", namespace = NormTestDataBuilder.AKN_NS)
public class Article extends BaseElement implements BodyElement {

  // Starting with 2 to account for the default paragraph
  @Builder.Default @XmlTransient private int paragraphCounter = 1;

  @XmlAttribute(name = "eId")
  private String eId;

  @Builder.Default
  @XmlAttribute(name = "period")
  private String period = "#meta-n1_geltzeiten-n1_geltungszeitgr-n1";

  @XmlElement(name = "num", namespace = NormTestDataBuilder.AKN_NS)
  private AknNum num;

  @XmlElement(name = "heading", namespace = NormTestDataBuilder.AKN_NS)
  private Heading heading;

  @Builder.Default
  @XmlElement(name = "paragraph", namespace = NormTestDataBuilder.AKN_NS)
  private List<AknParagraph> paragraphs = new ArrayList<>();

  public Article addNum(String num) {
    this.num = AknNum.builder().eId(eId + "_bezeichnung-n1").value(num).build();

    return this;
  }

  public Article addHeading(String text) {
    this.heading = Heading.builder().eId(eId + "_überschrift-n1").headline(text).build();

    return this;
  }

  public Article addParagraph(String text, String num) {
    paragraphs.add(AknParagraph.withText(text, num, eId, String.valueOf(paragraphCounter)));
    paragraphCounter++;

    return this;
  }
}
