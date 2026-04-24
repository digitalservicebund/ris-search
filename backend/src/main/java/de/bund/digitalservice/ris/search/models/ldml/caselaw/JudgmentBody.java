package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
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

  /**
   * A list of introductory elements such as headnotes (Leitsätze) or outlines (Gliederungen) and
   * motivations
   */
  @XmlElements({
    @XmlElement(
        name = "introduction",
        namespace = CaseLawLdmlNamespaces.AKN_NS,
        type = AknMainContent.class),
    @XmlElement(
        name = "motivation",
        namespace = CaseLawLdmlNamespaces.AKN_NS,
        type = AknMainContent.class)
  })
  private List<AknMainContent> contentBlocks;

  /**
   * The operative part of the judgment (the "Tenor").
   *
   * <p>Contains the binding ruling of the court.
   */
  @XmlElement(name = "decision", namespace = CaseLawLdmlNamespaces.AKN_NS, required = false)
  private JaxbHtml decision;

  /** The factual background or history of the case (the "Tatbestand"). */
  @XmlElement(name = "background", namespace = CaseLawLdmlNamespaces.AKN_NS, required = false)
  private JaxbHtml background;

  /**
   * Searches the motivation sections for a specific entry by its domain name.
   *
   * @param term the domain term of the section to find (e.g., "Entscheidungsgründe")
   * @return an {@link Optional} containing the HTML content if found, otherwise empty
   */
  public Optional<JaxbHtml> getContentByDomainTerm(DomainTerm term) {
    if (contentBlocks == null || term == null) {
      return Optional.empty();
    }

    return contentBlocks.stream()
        .filter(item -> term.equals(item.getDomainTerm()))
        .map(AknMainContent::getContent)
        .findFirst();
  }
}
