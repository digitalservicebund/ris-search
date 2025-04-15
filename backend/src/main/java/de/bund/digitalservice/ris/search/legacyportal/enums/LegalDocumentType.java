package de.bund.digitalservice.ris.search.legacyportal.enums;

public enum LegalDocumentType {
  LEGISLATION("LEGISLATION");

  private final String type;

  LegalDocumentType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
