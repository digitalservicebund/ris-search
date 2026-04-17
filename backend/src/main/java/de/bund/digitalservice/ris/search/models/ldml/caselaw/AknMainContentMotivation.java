package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;
import org.eclipse.persistence.oxm.annotations.XmlPath;

/**
 * Abstract base class for the "Motivation" phase of a legal document. *
 *
 * <p>In the Akoma Ntoso context, this represents the judicial reasoning or justification of a court
 * decision. It handles various forms of legal argumentation through polymorphic subclasses.
 */
@NoArgsConstructor
@Getter
public abstract class AknMainContentMotivation extends AknMainContent {

  /**
   * The actual HTML-formatted legal reasoning text.
   *
   * <p>Mapped to the current node content via {@link XmlPath}.
   */
  @XmlPath(".")
  protected JaxbHtml content;

  /**
   * Returns the specific name/type of the motivation section.
   *
   * @return a {@code String} representing the section type.
   */
  public abstract String getName();

  /**
   * Represents formal "Entscheidungsgründe" (Grounds of Decision).
   *
   * <p>Used for the systematic justification of the court's final ruling.
   */
  @NoArgsConstructor
  @XmlDiscriminatorValue(AknMainContentMotivation.DecisionGrounds.NAME)
  public static class DecisionGrounds extends AknMainContentMotivation {
    public static final String NAME = "Entscheidungsgründe";

    @Override
    public String getName() {
      return NAME;
    }
  }

  /**
   * Represents simplified "Gründe" (Grounds).
   *
   * <p>Often used in shorter rulings or specific procedural orders.
   */
  @NoArgsConstructor
  @XmlDiscriminatorValue(AknMainContentMotivation.Grounds.NAME)
  public static class Grounds extends AknMainContentMotivation {
    public static final String NAME = "Gründe";

    @Override
    public String getName() {
      return NAME;
    }
  }

  /**
   * Represents "Sonstiger Langtext" (Other Long Text).
   *
   * <p>A fallback category for descriptive or explanatory legal texts that do not fit into standard
   * reasoning categories.
   */
  @NoArgsConstructor
  @XmlDiscriminatorValue(AknMainContentMotivation.OtherLongText.NAME)
  public static class OtherLongText extends AknMainContentMotivation {
    public static final String NAME = "Sonstiger Langtext";

    @Override
    public String getName() {
      return NAME;
    }
  }

  /**
   * Represents an "Abweichende Meinung" (Dissenting Opinion).
   *
   * <p>Used for minority votes or dissenting views within a judicial panel.
   */
  @NoArgsConstructor
  @XmlDiscriminatorValue(AknMainContentMotivation.DissentingOpinion.NAME)
  public static class DissentingOpinion extends AknMainContentMotivation {
    public static final String NAME = "Abweichende Meinung";

    @Override
    public String getName() {
      return NAME;
    }
  }
}
