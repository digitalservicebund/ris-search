package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.oxm.annotations.XmlPath;

/**
 * Represents the main body of a court judgment in the LDML format.
 *
 * <p>This class maps the core components of a judicial decision, following the Akoma Ntoso
 * structure for introductions, operative part (Tenor), factual background (Tatbestand), and all
 * motivation sections (Entscheidungsgründe, Gründe, Rechtsfrage, Sonstiger Langtext, Abweichende
 * Meinung).
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class JudgmentBody {
  /**
   * Status of the document.
   *
   * <p>Set to "incomplete" in case of a Vorabdokument.
   */
  @XmlAttribute private String status;

  @XmlElement(name = "introduction", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private List<AknMainContent> contentBlocks;

  @XmlElement(name = "decision", namespace = CaseLawLdmlNamespaces.AKN_NS, required = false)
  private JaxbHtml tenor;

  @XmlElement(name = "background", namespace = CaseLawLdmlNamespaces.AKN_NS, required = false)
  private JaxbHtml tatbestand;

  @XmlPath("akn:motivation[@ris:domainTerm='Entscheidungsgründe']")
  private JaxbHtml entscheidungsgruende;

  @XmlPath("akn:motivation[@ris:domainTerm='Gründe']")
  private JaxbHtml gruende;

  @XmlPath("akn:motivation[starts-with(@ris:domainTerm, 'Rechtsfrage')]")
  private JaxbHtml rechtsfrage;

  @XmlPath("akn:motivation[@ris:domainTerm='Rechtsfrage (gesamt)']")
  private JaxbHtml rechtsfrageGesamt;

  @XmlPath("akn:motivation[@ris:domainTerm='Sonstiger Langtext']")
  private JaxbHtml sonstigerLangtext;

  @XmlPath("akn:motivation[@ris:domainTerm='Abweichende Meinung']")
  private AknDissentingOpinion abweichendeMeinung;

  public JaxbHtml getDecision() {
    return tenor;
  }

  public JaxbHtml getBackground() {
    return tatbestand;
  }

  /**
   * Searches the introduction sections for a specific entry by its domain name (e.g.,
   * DomainTerm.GUIDING_PRINCIPLE or DomainTerm.OUTLINE).
   *
   * @param term the domain term of the section to find
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

  /**
   * Safely formats and retrieves the dissenting opinion, or returns Optional.empty() if absent.
   *
   * @param risMeta the RisMeta object used to resolve person references by eId
   * @return an {@link Optional} containing the formatted dissenting opinion text if present
   */
  public Optional<String> getFormattedAbweichendeMeinung(RisMeta risMeta) {
    if (abweichendeMeinung == null) {
      return Optional.empty();
    }

    String formattedText = abweichendeMeinung.toFormattedText(risMeta);
    if (formattedText == null || formattedText.isBlank()) {
      return Optional.empty();
    }

    return Optional.of(formattedText);
  }
}
