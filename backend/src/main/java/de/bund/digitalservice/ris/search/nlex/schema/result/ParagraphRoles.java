package de.bund.digitalservice.ris.search.nlex.schema.result;

/**
 * Defines constants representing predefined roles for paragraphs.
 *
 * <p>The {@code ParagraphRoles} class provides a structured way to categorize paragraphs through
 * predefined role constants. These roles can be used to identify specific functionalities,
 * classifications, or behaviors assigned to paragraphs in the context of the application.
 *
 * <p>This class is designed to be a non-instantiable utility class, only containing static
 * constants for use throughout the application.
 *
 * <p>Constants: - {@code ZOOM}: Represents a paragraph role intended to emphasize or focus on
 * specific content. This role may be used in scenarios where zoomed-in or highlighted content is
 * required.
 */
public abstract class ParagraphRoles {

  private ParagraphRoles() {}

  public static final String ZOOM = "zoom";
}
