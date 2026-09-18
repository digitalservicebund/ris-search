package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import static de.bund.digitalservice.ris.search.models.ldml.caselaw.CaseLawLdmlNamespaces.AKN_NS;
import static de.bund.digitalservice.ris.search.models.ldml.caselaw.CaseLawLdmlNamespaces.RIS_NS;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents a dissenting opinion within a Case Law LDML document. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
public class AknDissentingOpinion {

  @XmlAttribute(namespace = RIS_NS, name = "domainTerm")
  private DomainTerm domainTerm;

  @XmlElement(name = "p", namespace = AKN_NS)
  private List<String> paragraphs;

  @XmlElement(name = "block", namespace = AKN_NS)
  private AknMotivationBlock aknMotivationBlock;

  /**
   * Formats paragraphs and opinion blocks into a single comma-separated string.
   *
   * @param risMeta the RIS metadata used to resolve opinion authors' display names, may be null
   * @return the formatted text combining paragraphs and opinions
   */
  public String toFormattedText(RisMeta risMeta) {
    List<String> parts = new ArrayList<>();

    if (paragraphs != null && !paragraphs.isEmpty()) {
      parts.add(String.join(" ", paragraphs).trim());
    }

    if (aknMotivationBlock != null && aknMotivationBlock.getOpinions() != null) {
      for (AknOpinion opinion : aknMotivationBlock.getOpinions()) {
        String cleanEid = opinion.getCleanByEid();

        String personName =
            (risMeta != null) ? risMeta.getPersonNameByEid(cleanEid).orElse(cleanEid) : cleanEid;

        String opinionText = opinion.getText() != null ? opinion.getText().trim() : "";
        parts.add(personName + ": " + opinionText);
      }
    }

    return String.join(", ", parts);
  }
}
