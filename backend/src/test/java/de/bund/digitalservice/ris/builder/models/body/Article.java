package de.bund.digitalservice.ris.builder.models.body;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.AuthorialNote;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.common.Heading;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/** Represents an {@code akn:article} element, the main structural unit of the norm's body. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@XmlRootElement(namespace = NormTestDataBuilder.AKN_NS)
public class Article extends BaseElement implements BodyElement {

  @Builder.Default @XmlTransient private int paragraphCounter = 0;

  @XmlAttribute private String eId;

  @Builder.Default @XmlAttribute private String period = "#meta-n1_geltzeiten-n1_geltungszeitgr-n1";

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private AknNum num;

  @XmlElement(namespace = NormTestDataBuilder.AKN_NS)
  private Heading heading;

  @Builder.Default
  @XmlElement(name = "paragraph", namespace = NormTestDataBuilder.AKN_NS)
  private List<AknParagraph> paragraphs = new ArrayList<>();

  /**
   * Sets the article's number.
   *
   * @param num the article number, e.g. "§ 1"
   * @return this article for chaining
   */
  public Article addNum(String num) {
    this.num = AknNum.builder().eId(eId + "_bezeichnung-n1").value(num).build();

    return this;
  }

  /**
   * Sets the article's heading, optionally with an authorial note.
   *
   * @param text the heading text
   * @param authorialNote optional authorial note text, or {@code null} for none
   * @return this article for chaining
   */
  public Article addHeading(String text, String authorialNote) {
    List<Object> headingElements = new ArrayList<>(List.of(text));
    if (authorialNote != null) {
      headingElements.add(AuthorialNote.withText(authorialNote));
    }

    this.heading = Heading.builder().eId(eId + "_überschrift-n1").headline(headingElements).build();
    return this;
  }

  /**
   * Adds a paragraph with the given text and number to the article.
   *
   * @param text the paragraph text
   * @param num the paragraph number, e.g. "(1)"
   * @return this article for chaining
   */
  public Article addParagraph(String text, String num) {
    paragraphCounter++;
    paragraphs.add(AknParagraph.withText(text, num, eId, String.valueOf(paragraphCounter)));

    return this;
  }
}
