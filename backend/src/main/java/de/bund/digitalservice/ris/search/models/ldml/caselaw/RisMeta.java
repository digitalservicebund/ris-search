package de.bund.digitalservice.ris.search.models.ldml.caselaw;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Represents the RIS-specific metadata block of a Case Law LDML document. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class RisMeta {

  @XmlElementWrapper(name = "aktenzeichenListe", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "aktenzeichen", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<RisAktenzeichen> risAktenzeichen;

  @XmlElement(name = "dokumenttyp", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String risDokumentTyp;

  @XmlElement(name = "gericht", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private RisGericht risGericht;

  @XmlElement(name = "rechtskraft", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String risRechtskraft;

  @XmlElementWrapper(name = "sachgebiete", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "sachgebiet", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> risSachgebiete;

  @XmlElementWrapper(name = "streitjahre", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "streitjahr", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> risStreitjahre;

  @XmlElement(name = "spruchkoerper", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String risSpruchkoerper;

  @XmlElementWrapper(name = "deviatingCourts", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "deviatingCourt", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> deviatingCourt;

  @XmlElementWrapper(name = "abweichendeDaten", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "abweichendesDatum", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<LocalDate> risAbweichendeDaten;

  @XmlElementWrapper(name = "abweichendeDokumentnummern", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "abweichendeDokumentnummer", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> risAbweichendeDokumentnummern;

  @XmlElementWrapper(name = "abweichendeEclis", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "abweichenderEcli", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> risAbweichendeEclis;

  @XmlElementWrapper(name = "berufsbilder", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "berufsbild", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<String> risBerufsbilder;

  @XmlElementWrapper(
      name = "datenDerMuendlichenVerhandlung",
      namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "datumDerMuendlichenVerhandlung", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<LocalDate> risDatenDerMuendlichenVerhandlung;

  @XmlElementWrapper(name = "definitionen", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "definition", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<RisDefinition> risDefinitionen;

  @XmlElement(name = "dokumentationsstelle", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private String risDokumentationsstelle;

  @XmlElementWrapper(name = "personen", namespace = CaseLawLdmlNamespaces.RIS_NS)
  @XmlElement(name = "person", namespace = CaseLawLdmlNamespaces.RIS_NS)
  private List<RisPerson> risPersonen;

  public String getCourtKeyword() {
    return risGericht.getShowAs();
  }

  public List<String> getAktenzeichen() {
    return risAktenzeichen.stream()
        .filter(a -> "Aktenzeichen".equals(a.getDomainTerm()))
        .map(RisAktenzeichen::getValue)
        .toList();
  }

  /**
   * Returns the terms flagged as defined ({@code definierterBegriff}) within this document.
   *
   * @return the list of defined terms, or an empty list if none are present
   */
  public List<String> getDefinitionen() {
    if (risDefinitionen == null) {
      return List.of();
    }
    return risDefinitionen.stream().map(RisDefinition::getDefinierterBegriff).toList();
  }

  /**
   * Look up a person's display name by their eId.
   *
   * @param eId the eId of the person to look up
   * @return the person's showAs value, or their name if showAs is not set, if found
   */
  public Optional<String> getPersonNameByEid(String eId) {
    if (risPersonen == null || eId == null) {
      return Optional.empty();
    }
    return risPersonen.stream()
        .filter(person -> eId.equals(person.getEId()))
        .map(person -> person.getShowAs() != null ? person.getShowAs() : person.getName())
        .findFirst();
  }
}
