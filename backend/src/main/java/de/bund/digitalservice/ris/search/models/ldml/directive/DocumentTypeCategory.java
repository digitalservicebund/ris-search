package de.bund.digitalservice.ris.search.models.ldml.directive;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;

@XmlEnum
@XmlType(name = "category")
@Getter
public enum DocumentTypeCategory {
  VV("Verwaltungsvorschrift"),
  VE("Verwaltungsvereinbarung"),
  VB("Verbandsbeschluss"),
  ST("Stellungnahme"),
  VR("Verwaltungsregelung");

  private final String longName;

  DocumentTypeCategory(final String longName) {
    this.longName = longName;
  }

  public String getValue() {
    return name();
  }
}
