package de.bund.digitalservice.ris.builder.models.preface;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.AknP;
import de.bund.digitalservice.ris.builder.models.common.AuthorialNote;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the {@code akn:longTitle} element, wrapping the norm's official and short titles. */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class LongTitle extends BaseElement {

  @XmlTransient private DocTitle officialTitle;

  @Builder.Default @XmlTransient private ShortTitle shortTitle = new ShortTitle();

  @Builder.Default @XmlAttribute private String eId = "einleitung-n1_doktitel-n1";

  @Builder.Default
  @XmlElement(name = "p", namespace = NormTestDataBuilder.AKN_NS)
  private AknP paragraph =
      AknP.builder()
          .eId("einleitung-n1_doktitel-n1_text-n1")
          .children(
              List.of(
                  DocStage.builder().build(),
                  DocTitle.builder().build(),
                  ShortTitle.builder().build()))
          .build();

  /**
   * Sets the official title and rebuilds the paragraph.
   *
   * @param officialTitle the official title text
   */
  public void setOfficialTitle(String officialTitle) {
    if (officialTitle == null) {
      this.officialTitle = null;
    } else {
      this.officialTitle =
          DocTitle.builder().children(new ArrayList<>(List.of(officialTitle))).build();
    }

    setTitlesAndAbbreviation();
  }

  /**
   * Adds an authorial note to the official title
   *
   * @param authorialNote optional authorial note text, or {@code null} for none
   */
  public void addAuthorialNote(String authorialNote) {
    this.officialTitle.getChildren().add(AuthorialNote.withText(authorialNote));
    setTitlesAndAbbreviation();
  }

  /**
   * Sets the short title and rebuilds the paragraph.
   *
   * @param shortTitle the short title to set
   */
  public void setShortTitle(ShortTitle shortTitle) {
    this.shortTitle = shortTitle;
    setTitlesAndAbbreviation();
  }

  private void setTitlesAndAbbreviation() {
    List<Object> childElements = new ArrayList<>();
    childElements.add(DocStage.builder().build());

    if (this.officialTitle != null) {
      childElements.add(this.officialTitle);
    }

    if (this.shortTitle != null) {
      childElements.add(this.shortTitle);
    }

    this.paragraph =
        AknP.builder().eId("einleitung-n1_doktitel-n1_text-n1").children(childElements).build();
  }
}
