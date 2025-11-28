package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
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
  @XmlElement(name = "motivation", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private JaxbHtml motivation;

  @XmlElement(name = "introduction", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private AknMultipleBlock introduction;

  @XmlElement(name = "background", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private JaxbHtml background;

  @XmlElement(name = "decision", namespace = CaseLawLdmlNamespaces.AKN_NS)
  private AknMultipleBlock decision;
}
