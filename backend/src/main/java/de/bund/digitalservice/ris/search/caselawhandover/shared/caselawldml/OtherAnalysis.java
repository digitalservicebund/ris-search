package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents additional analytical metadata associated with a legal document. *
 *
 * <p>This class serves as a container for metadata that doesn't fit into standard structural
 * fields, specifically handling documentary summaries and short texts within the RIS-specific
 * namespace.
 */
@Getter
@Setter
public class OtherAnalysis {

  /**
   * Contains documentary short texts (e.g., headnotes, or orienting sentences). *
   *
   * <p>Mapped to the {@code RIS_NS} namespace to maintain compliance with the German Federal Legal
   * Information System requirements.
   */
  @XmlElement(name = "dokumentarischeKurztexte", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private DocumentaryShortTexts documentaryShortTexts;
}
