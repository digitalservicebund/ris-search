package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;
import org.eclipse.persistence.oxm.annotations.XmlPath;

/**
 * Base class for specialized introductory content sections in an Akoma Ntoso document.
 *
 * <p>This abstract class facilitates the mapping of specific German legal text types (like Guiding
 * Principles or Outlines) that appear in the preamble or introductory phase of a court decision.
 */
@NoArgsConstructor
@Getter
public abstract class AknMainContentIntroduction extends AknMainContent {

  /**
   * The actual HTML-formatted content of the section.
   *
   * <p>Uses {@link XmlPath} to map the content directly to the current XML node.
   */
  @XmlPath(".")
  protected JaxbHtml content;

  /**
   * Returns the specific name/type of the introductory section.
   *
   * @return a {@code String} representing the section type.
   */
  public abstract String getName();

  /**
   * Represents a "Leitsatz" (Guiding Principle) section.
   *
   * <p>These are the official core legal statements formulated by the court.
   */
  @NoArgsConstructor
  @XmlDiscriminatorValue(AknMainContentIntroduction.GuidingPrinciple.NAME)
  public static class GuidingPrinciple extends AknMainContentIntroduction {
    public static final String NAME = "Leitsatz";

    public GuidingPrinciple(JaxbHtml content) {
      this.content = content;
    }

    /**
     * Factory method to create a GuidingPrinciple if content is present.
     *
     * @param content the HTML content to wrap
     * @return a new instance, or {@code null} if content is null
     */
    public static GuidingPrinciple build(JaxbHtml content) {
      return content == null ? null : new GuidingPrinciple(content);
    }

    @Override
    public String getName() {
      return NAME;
    }
  }

  /**
   * Represents a "Gliederung" (Outline or Table of Contents) section.
   *
   * <p>Used for structured overviews in particularly long or complex court decisions.
   */
  @NoArgsConstructor
  @XmlDiscriminatorValue(AknMainContentIntroduction.Outline.NAME)
  public static class Outline extends AknMainContentIntroduction {
    public static final String NAME = "Gliederung";

    public Outline(JaxbHtml content) {
      this.content = content;
    }

    /**
     * Factory method to create an Outline if content is present.
     *
     * @param content the HTML content to wrap
     * @return a new instance, or {@code null} if content is null
     */
    public static Outline build(JaxbHtml content) {
      return content == null ? null : new Outline(content);
    }

    @Override
    public String getName() {
      return NAME;
    }
  }
}
