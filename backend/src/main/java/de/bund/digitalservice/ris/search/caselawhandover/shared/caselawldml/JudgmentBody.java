package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the JudgmentBody element in the case law LDML format. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class JudgmentBody {

  @XmlElement(name = "introduction", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private List<AknMainContent> introductions;

  @XmlElement(name = "decision", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private JaxbHtml decision;

  @XmlElement(name = "background", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private JaxbHtml background;

  @XmlElement(name = "motivation", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private List<AknMainContent> motivations;

  public Optional<JaxbHtml> getIntroductionEntryContentByName(String name) {
    if (introductions == null) {
      return Optional.empty();
    }

    return introductions.stream()
        .filter(item -> name.equals(item.getName()))
        .map(AknMainContent::getContent)
        .findFirst();
  }

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
