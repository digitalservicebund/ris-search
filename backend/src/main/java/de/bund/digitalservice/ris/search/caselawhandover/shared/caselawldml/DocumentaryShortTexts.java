package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.persistence.oxm.annotations.XmlPath;

/**
 * Container for short documentary and editorial texts associated with a legal decision.
 *
 * <p>This class groups specific metadata elements like titles and orienting sentences (headnotes)
 * that provide a quick overview of the case's content.
 */
@Getter
@Setter
public class DocumentaryShortTexts {

  /** The formal title or heading line of the documentary section. */
  @XmlElement(name = "titelzeile", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private TitleLine titleLine;

  /**
   * The primary "Orientierungssatz" (orienting sentence or headnote).
   *
   * <p>Usually contains a concise summary of the legal core of the decision prepared by judicial
   * documentation units.
   */
  @XmlElement(name = "orientierungssatz", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Orientierungssatz headNotes;

  /**
   * Secondary or alternative orienting sentences.
   *
   * <p>Used for supplementary summaries or notes that don't fit into the primary headnote category.
   */
  @XmlElement(name = "sonstigerOrientierungssatz", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private SonstigerOrientierungssatz otherHeadNotes;

  /** Represents the content of a title line. */
  @Getter
  @Setter
  public static class TitleLine {
    /** The HTML-formatted text content of the title line. */
    @XmlPath(".")
    private JaxbHtml content;
  }

  /** Represents the content of a standard orienting sentence (headnote). */
  @Getter
  @Setter
  public static class Orientierungssatz {
    /** The HTML-formatted text content of the headnote. */
    @XmlPath(".")
    private JaxbHtml content;
  }

  /** Represents the content of an alternative or "other" orienting sentence. */
  @Getter
  @Setter
  public static class SonstigerOrientierungssatz {
    /** The HTML-formatted text content of the additional headnote. */
    @XmlPath(".")
    private JaxbHtml content;
  }
}
