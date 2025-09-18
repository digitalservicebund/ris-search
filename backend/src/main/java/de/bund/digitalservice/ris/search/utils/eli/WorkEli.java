package de.bund.digitalservice.ris.search.utils.eli;

public record WorkEli(String jurisdiction, String agent, String year, String naturalIdentifier) {

  @Override
  public String toString() {
    return "eli/%s/%s/%s/%s".formatted(jurisdiction, agent, year, naturalIdentifier);
  }
}
