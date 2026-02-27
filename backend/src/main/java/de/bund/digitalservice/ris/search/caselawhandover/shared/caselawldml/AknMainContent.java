package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlSeeAlso;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorNode;

/**
 * The abstract base class for all main content sections within an Akoma Ntoso (AKN) document.
 *
 * <p>This class enables polymorphic handling of different legal text sections. By using EclipseLink
 * MOXy's {@link XmlDiscriminatorNode}, the specific subclass is determined at runtime based on the
 * value of the {@code ris:domainTerm} attribute in the XML.
 *
 * @see AknMainContentIntroduction
 * @see AknMainContentMotivation
 */
@NoArgsConstructor
@XmlDiscriminatorNode("@ris:domainTerm")
@XmlSeeAlso({
  AknMainContentIntroduction.GuidingPrinciple.class,
  AknMainContentIntroduction.Outline.class,
  AknMainContentMotivation.DecisionGrounds.class,
  AknMainContentMotivation.Grounds.class,
  AknMainContentMotivation.OtherLongText.class,
  AknMainContentMotivation.DissentingOpinion.class
})
public abstract class AknMainContent {

  /**
   * Returns the domain-specific name of the content section.
   *
   * @return a {@code String} representing the section type (e.g., "Leitsatz", "Gründe").
   */
  public abstract String getName();

  /**
   * Provides access to the HTML-formatted content of the section.
   *
   * @return the {@link JaxbHtml} object containing the rendered legal text.
   */
  public abstract JaxbHtml getContent();
}
