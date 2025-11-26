package de.bund.digitalservice.ris.search.utils.eli;

/**
 * The WorkEli class represents the base-level European Legislation Identifier (ELI) for describing
 * and identifying a legislative work. It is a structured form of metadata representation of a
 * legislative document, comprised of information about jurisdiction, publishing agent, the year of
 * publication, and a natural identifier.
 *
 * <p>A WorkEli serves as a foundational component and does not include additional details like
 * expression or manifestation metadata that are represented in derived classes like ExpressionEli
 * and ManifestationEli.
 *
 * <p>Fields: - jurisdiction: Represents the jurisdiction (country, region, etc.) where the
 * legislative work is applicable. - agent: Denotes the publishing agent responsible for producing
 * the legislative document. - year: Indicates the year in which the legislative work was published
 * or created. - naturalIdentifier: Provides a unique natural identifier for the legislative work
 * within the given jurisdiction and agent.
 *
 * <p>Overrides: - toString(): Formats the WorkEli as a standardized ELI string of the format
 * "eli/{jurisdiction}/{agent}/{year}/{naturalIdentifier}".
 */
public record WorkEli(String jurisdiction, String agent, String year, String naturalIdentifier) {

  @Override
  public String toString() {
    return "eli/%s/%s/%s/%s".formatted(jurisdiction, agent, year, naturalIdentifier);
  }
}
