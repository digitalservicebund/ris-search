package de.bund.digitalservice.ris.search.caselawhandover.shared.caselawldml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class RisMeta {
  @XmlElementWrapper(name = "decisionNames", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "decisionName", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> decisionName;

  @XmlElementWrapper(name = "previousDecisions", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "previousDecision", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<RelatedDecision> previousDecision;

  @XmlElementWrapper(name = "ensuingDecisions", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "ensuingDecision", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<RelatedDecision> ensuingDecision;

  @XmlElementWrapper(name = "fileNumbers", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "fileNumber", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> fileNumbers;

  @XmlElement(name = "documentType", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String documentType;

  @XmlElement(name = "courtLocation", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String courtLocation;

  @XmlElement(name = "courtType", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String courtType;

  @XmlElementWrapper(name = "legalForces", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "legalForce", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> legalForce;

  @XmlElement(name = "legalEffect", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String legalEffect;

  @XmlElementWrapper(name = "fieldOfLaws", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "fieldOfLaw", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> fieldOfLaw;

  @XmlElement(name = "yearOfDispute", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String yearOfDispute;

  @XmlElement(name = "judicialBody", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String judicialBody;

  @XmlElementWrapper(name = "deviatingCourts", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "deviatingCourt", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> deviatingCourt;

  @XmlElementWrapper(name = "deviatingDates", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "deviatingDate", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> deviatingDate;

  @XmlElementWrapper(name = "deviatingDocumentNumbers", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "deviatingDocumentNumber", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> deviatingDocumentNumber;

  @XmlElementWrapper(name = "deviatingEclis", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "deviatingEcli", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> deviatingEcli;

  @XmlElementWrapper(name = "deviatingFileNumbers", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "deviatingFileNumber", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> deviatingFileNumber;

  @XmlElement(name = "publicationStatus", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String publicationStatus;

  @XmlElement(name = "error", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private Boolean error;

  @XmlElement(name = "documentationOffice", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String documentationOffice;

  @XmlElementWrapper(name = "procedures", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "procedure", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> procedure;

  public String getCourtKeyword() {
    if (courtLocation == null) {
      return courtType;
    }
    return String.format("%s %s", courtType, courtLocation);
  }
}
