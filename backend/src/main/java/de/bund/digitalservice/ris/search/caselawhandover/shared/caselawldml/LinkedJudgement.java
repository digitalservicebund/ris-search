package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;

/** Represents a linked court decision (judgement) and its associated metadata. */
@Getter
@Setter
public class LinkedJudgement {
  @XmlElement(name = "dokumenttyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String documentType;

  @XmlElement(name = "entscheidungsdatum", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String decisionDate;

  @XmlElement(name = "aktenzeichen", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String fileNumber;

  @XmlElement(name = "gericht", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private RisGericht risGericht;

  /**
   * Returns a simplified string representation containing the file number and court type.
   *
   * @return a comma-separated string of the judgement details
   */
  public String asString() {
    String courtType = (risGericht != null) ? risGericht.getGerichtstyp() : null;

    return Stream.of(fileNumber, courtType)
        .filter(Objects::nonNull)
        .collect(Collectors.joining(", "));
  }
}
