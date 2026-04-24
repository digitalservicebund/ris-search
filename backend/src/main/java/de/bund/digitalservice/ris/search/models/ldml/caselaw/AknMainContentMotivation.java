package de.bund.digitalservice.ris.search.models.ldml.caselaw;

/** Abstract base class for the "Motivation" phase of a legal document. * */
public abstract class AknMainContentMotivation {

  /**
   * Represents formal "Entscheidungsgründe" (Grounds of Decision).
   *
   * <p>Used for the systematic justification of the court's final ruling.
   */
  public static final String DECISION_GROUNDS = "Entscheidungsgründe";

  /**
   * Represents simplified "Gründe" (Grounds).
   *
   * <p>Often used in shorter rulings or specific procedural orders.
   */
  public static final String GROUNDS = "Gründe";

  /**
   * Represents "Sonstiger Langtext" (Other Long Text).
   *
   * <p>A fallback category for descriptive or explanatory legal texts that do not fit into standard
   * reasoning categories.
   */
  public static final String OTHER_LONGTEXT = "Sonstiger Langtext";

  /**
   * Represents an "Abweichende Meinung" (Dissenting Opinion).
   *
   * <p>Used for minority votes or dissenting views within a judicial panel.
   */
  public static final String DISSENTING_OPINION = "Abweichende Meinung";
}
