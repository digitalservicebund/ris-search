package de.bund.digitalservice.ris.builder.models.preface;

import de.bund.digitalservice.ris.builder.NormTestDataBuilder;
import de.bund.digitalservice.ris.builder.models.common.Inline;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlSeeAlso({Inline.class})
@XmlRootElement(name = "shortTitle", namespace = NormTestDataBuilder.AKN_NS)
@XmlAccessorType(XmlAccessType.FIELD)
public class ShortTitle {

  @XmlTransient private String shortTitle;

  @XmlTransient private Inline abbreviation;

  @Builder.Default
  @XmlAttribute(name = "eId")
  private String eId = "einleitung-n1_doktitel-n1_text-n1_kurztitel-n1";

  @Builder.Default
  @XmlAttribute(name = "GUID")
  private String guid = UUID.randomUUID().toString();

  @Builder.Default @XmlAnyElement
  private List<Object> children = List.of("Short Title of the Legislation");

  public ShortTitle withTitle(String title) {
    this.shortTitle = title;
    setTitleAndAbbreviation();

    return this;
  }

  public ShortTitle withOfficialAbbreviation(String abbreviation) {
    this.abbreviation =
        Inline.builder()
            .eId("einleitung-n1_doktitel-n1_text-n1_kurztitel-n1_inline-n1")
            .refersTo("amtliche-abkuerzung")
            .content(abbreviation)
            .build();

    setTitleAndAbbreviation();

    return this;
  }

  private void setTitleAndAbbreviation() {
    List<Object> childElements = new ArrayList<>();
    if (this.shortTitle != null) {
      childElements.add(this.shortTitle);
    }

    if (this.abbreviation != null) {
      childElements.add(this.abbreviation);
    }

    this.children = childElements;
  }
}
