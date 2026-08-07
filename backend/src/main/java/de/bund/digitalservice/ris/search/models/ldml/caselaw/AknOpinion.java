package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents an opinion element within a motivation block in a Case Law LDML document. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
public class AknOpinion {

  @XmlAttribute(name = "by")
  private String by;

  @XmlAttribute(name = "type")
  private String type;

  @XmlValue private String text;

  /**
   * Helper to strip the leading '#' from the 'by' attribute so it matches the person's eId.
   *
   * @return the 'by' attribute without its leading '#', or the original value if it has none
   */
  public String getCleanByEid() {
    if (by != null && by.startsWith("#")) {
      return by.substring(1);
    }
    return by;
  }
}
