package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents the main body of a court judgment in the LDML format.
 *
 * <p>This class maps the core components of a judicial decision, following the Akoma Ntoso
 * structure for introductions, the operative part (Tenor), factual background (Tatbestand), and
 * legal reasoning (Entscheidungsgründe).
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class JudgmentBody {

  /** A list of introductory elements such as headnotes (Leitsätze) or outlines (Gliederungen). */
  @XmlElement(name = "introduction", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private List<AknMainContent> introductions;

  /**
   * The operative part of the judgment (the "Tenor").
   *
   * <p>Contains the binding ruling of the court.
   */
  @XmlElement(name = "decision", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private JaxbHtml decision;

  /** The factual background or history of the case (the "Tatbestand"). */
  @XmlElement(name = "background", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private JaxbHtml background;

  /**
   * A list of legal reasoning blocks (Motivations), which may include formal grounds or dissenting
   * opinions.
   */
  @XmlElement(name = "motivation", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private List<AknMainContent> motivations;

  /**
   * Searches the introductory sections for a specific entry by its domain name.
   *
   * @param name the name of the section to find (e.g., "Leitsatz")
   * @return an {@link Optional} containing the HTML content if found, otherwise empty
   */
  public Optional<JaxbHtml> getIntroductionEntryContentByName(String name) {
    if (introductions == null) {
      return Optional.empty();
    }

    return introductions.stream()
        .filter(item -> name.equals(item.getName()))
        .map(AknMainContent::getContent)
        .findFirst();
  }

  /**
   * Searches the motivation sections for a specific entry by its domain name.
   *
   * @param name the name of the section to find (e.g., "Entscheidungsgründe")
   * @return an {@link Optional} containing the HTML content if found, otherwise empty
   */
  public Optional<JaxbHtml> getMotivationEntryContentByName(String name) {
    if (motivations == null) {
      return Optional.empty();
    }

    return motivations.stream()
        .filter(item -> name.equals(item.getName()))
        .map(AknMainContent::getContent)
        .findFirst();
  }
}
