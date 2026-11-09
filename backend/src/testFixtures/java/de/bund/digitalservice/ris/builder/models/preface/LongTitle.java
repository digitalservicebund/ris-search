package de.bund.digitalservice.ris.builder.models.preface;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.AknP;
import de.bund.digitalservice.ris.builder.models.common.BaseElement;
import de.bund.digitalservice.ris.builder.models.common.Inline;
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

/** Represents the {@code akn:longTitle} element, wrapping the norm's official and short titles. */
@AllArgsConstructor
@Builder
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
public class LongTitle extends BaseElement {

  @XmlTransient private DocTitle officialTitle;

  @XmlTransient private ShortTitle shortTitle;

  @XmlTransient private Inline officialAbbreviation;

  @XmlAttribute private String eId;

  @XmlElement(name = "p", namespace = NormTestDataBuilder.AKN_NS)
  private AknP paragraph;

  public LongTitle() {
    this(new DocTitle(), new ShortTitle(), null);
  }

  /** Creates a LongTitle with the given title parts as string. */
  public LongTitle(String officialTitle, String shortTitle, String officialAbbreviation) {
    this(
        officialTitle == null ? null : new DocTitle(officialTitle),
        shortTitle == null ? null : new ShortTitle(shortTitle),
        officialAbbreviation == null
            ? null
            : new Inline(
                "einleitung-n1_doktitel-n1_text-n1_kurztitel-n1_inline-n1",
                "amtliche-abkuerzung",
                officialAbbreviation));
  }

  /** Creates a LongTitle with the given title components. */
  public LongTitle(DocTitle officialTitle, ShortTitle shortTitle, Inline officialAbbreviation) {
    eId = "einleitung-n1_doktitel-n1";
    List<Object> children = new ArrayList<>();

    if (officialTitle != null) {
      this.officialTitle = officialTitle;
      children.add(officialTitle);
    }
    if (shortTitle != null || officialAbbreviation != null) {
      children.add("(");
    }
    if (shortTitle != null) {
      this.shortTitle = shortTitle;
      children.add(shortTitle);
    }
    if (shortTitle != null && officialAbbreviation != null) {
      children.add(" - ");
    }
    if (officialAbbreviation != null) {
      this.officialAbbreviation = officialAbbreviation;
      children.add(officialAbbreviation);
    }

    if (shortTitle != null || officialAbbreviation != null) {
      children.add(")");
    }
    paragraph = new AknP("einleitung-n1_doktitel-n1", children);
  }
}
