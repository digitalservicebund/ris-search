package de.bund.digitalservice.ris.builder.models.preface;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.AknP;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
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

  public LongTitle withOfficialTitle(String officialTitle) {
    this.officialTitle = DocTitle.builder().children(List.of(officialTitle)).build();
    setTitlesAndAbbreviation();
    return this;
  }

  public LongTitle withShortTitle(ShortTitle shortTitle) {
    this.shortTitle = shortTitle;
    setTitlesAndAbbreviation();
    return this;
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
