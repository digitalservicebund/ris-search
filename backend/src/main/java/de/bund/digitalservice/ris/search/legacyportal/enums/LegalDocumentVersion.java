package de.bund.digitalservice.ris.search.legacyportal.enums;

public enum LegalDocumentVersion {
  VERSION_1_4("1.4"),
  VERSION_1_6("1.6");

  private final String version;

  LegalDocumentVersion(String version) {
    this.version = version;
  }

  public String getVersion() {
    return version;
  }

  public static LegalDocumentVersion fromString(String version) {
    for (LegalDocumentVersion legalDocumentVersion : LegalDocumentVersion.values()) {
      if (legalDocumentVersion.version.equalsIgnoreCase(version)) {
        return legalDocumentVersion;
      }
    }
    return null;
  }
}
